package org.example.countdownmanagerapi

import com.auth0.jwt.*
import com.auth0.jwt.algorithms.*
import com.fasterxml.jackson.databind.*
import com.mongodb.async.client.Observable
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
import org.litote.kmongo.async.getCollection
import org.litote.kmongo.coroutine.findOne
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.coroutine.toList
import org.litote.kmongo.eq
import java.lang.RuntimeException

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, port = 8080) {
        val loginJwt = SimpleJwt("user-auth-token")
        val database = Database.getInstance("prod")

        install(CORS) {
//            method(HttpMethod.Options)
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Put)
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
                if (post.email == null || post.hashedPassword == null)
                    throw InvalidCredentialsException("Email and a hashedPassword must be provided")
                var user: User? = database.db.getCollection<User>("users").findOne(User::email eq post.email)
                if (user == null) {
                    user = User(post.email, post.hashedPassword)
                    database.db.getCollection<User>("users").insertOne(user)
                } else if (!user.checkPassword(post.hashedPassword)) {
                    throw InvalidCredentialsException("Invalid credentials")
                }
                call.respond(mapOf("token" to loginJwt.sign(user.email)))
            }
            route("/numcd") {
                authenticate {
                    get {
                        val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                        call.respond(mapOf(
                            "OK" to true,
                            "length" to database.db
                                .getCollection<Pair<String, List<Countdown>>>("countdowns")
                                .findOne(Pair<String, List<Countdown>>::first eq principal.name)
                        ))
                    }
                }
            }
            get("/cd-{cid}") {
                call.respond(mapOf("OK" to true))
            }
            post("/cd-{cid}") {
                call.respond(mapOf("OK" to true))
            }
            patch("/cd-{cid}") {
                call.respond(mapOf("OK" to true))
            }
            delete("/cd-{cid}") {
                call.respond(mapOf("OK" to true))
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
