package org.example.countdownmanagerapi

import com.auth0.jwt.*
import com.auth0.jwt.algorithms.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.mongodb.DBObject
import com.mongodb.async.client.Observable
import com.mongodb.client.model.BsonField
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.*
import org.example.countdownmanagerapi.data.MetaParam
import org.litote.kmongo.async.getCollection
import org.litote.kmongo.eq
import java.lang.RuntimeException

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, port = 8080) {
        val loginJwt = SimpleJwt("user-auth-token")
        val database = Database.getInstance("prod")
        val userRepo = UserRepository(database.db.getCollection<User>("users"))
        val countdownRepo = CountdownRepository(database.db.getCollection<Countdown>("countdowns"))

        install(CORS) {
//            method(HttpMethod.Options)
            method(HttpMethod.Get)
            method(HttpMethod.Post)
//            method(HttpMethod.Put)
            method(HttpMethod.Delete)
            method(HttpMethod.Patch)
            header(HttpHeaders.Authorization)
            allowCredentials = true
            anyHost()
        }
        install(StatusPages) {
            exception<InvalidCredentialsException> { exception ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("OK" to false, "error" to (exception.message ?: ""))
                )
            }
        }
        install(Authentication) {
            jwt {
                verifier(loginJwt.verifier)
                validate {
                    UserIdPrincipal(it.payload.getClaim("email").asString())
                }
            }
        }
        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT) // pretty print JSON
            }
        }

        routing {
            post("/login-register") {
                val post = call.receive<LoginRegister>()
                val user = userRepo.getOrCreate(post)
                countdownRepo.owner = user
                call.respond(mapOf("token" to loginJwt.sign(user.email)))
            }
            route("/numcd") {
                authenticate {
                    get {
                        countdownRepo.checkCall(call.principal()) {
                            call.respond(countdownRepo.getMeta(MetaParam.LENGTH))
                        }
                    }
                }
            }
            route("/cd") {
                authenticate {
                    get {
                        countdownRepo.checkCall(call.principal()) {
                            val params = call.request.queryParameters
                            call.respond(mapOf(
                                "OK" to true,
                                "countdowns" to countdownRepo.findMany(
                                    if (params.contains("id")) Countdown::id eq params["id"] else null
                                )
                            ))
                        }
                    }
                    post {
                        val cd = call.receive<Countdown>()
                        countdownRepo.checkCall(call.principal(), listOf(cd)) {
                            call.respond(mapOf(
                                "OK" to countdownRepo.insert(cd)
                            ))
                        }
                    }
                    delete {
                        val cd = call.receive<Countdown>().copy(owner = countdownRepo.owner?.email)
                        countdownRepo.checkCall(call.principal(), listOf(cd)) {
                            call.respond(mapOf(
                                "OK" to countdownRepo.delete(cd)
                            ))
                        }
                    }
                    patch {
                        val cd = call.receive<Countdown>().copy(owner = countdownRepo.owner?.email)
                        countdownRepo.checkCall(call.principal(), listOf(cd)) {
                            call.respond(mapOf(
                                "OK" to countdownRepo.update(cd)
                            ))
                        }
                    }
                }
            }
        }

//        Database.destroyAll()
    }
    server.start(wait = true)
}

open class SimpleJwt(val secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()
    fun sign(email: String): String =
        JWT.create().withClaim("email", email).sign(algorithm)
}

class InvalidCredentialsException(message: String) : RuntimeException(message)

class LoginRegister(val email: String?, val hashedPassword: String?)
