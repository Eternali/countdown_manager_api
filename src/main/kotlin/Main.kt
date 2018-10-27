package org.example.CountdownManagerApi

import com.auth0.jwt.*
import com.auth0.jwt.algorithms.*
import com.fasterxml.jackson.databind.*
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
import java.lang.RuntimeException

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, port = 8080) {
        val loginJwt = SimpleJwt("user-auth-token")

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
                val user = users.getOrPut(post.user) {
                    User(post.user, post.hashedPassword)
                }
            }
            get("/register") {
                call.respondText("Hello World!", ContentType.Text.Plain)
            }
            get("/length-{uid}") {
                call.respondText("HELLO WORLD!")
            }
            get('/cd-{uid}{cid}') {

            }
            post('/cd-{uid}{cid}') {

            }
            update('/cd-{uid}{cid}') {

            }
            delete('/cd-{uid}{cid}') {

            }
        }
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

class LoginRegister(val user: String, val hashedPassword: String)
