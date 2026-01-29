package com.tuempresa.app

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.*

fun main() {

    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080

    embeddedServer(
        Netty,
        port = port,
        host = "0.0.0.0"
    ) {
        install(StatusPages) {
            status(HttpStatusCode.NotFound) { call, _ ->
                call.respondRedirect("/404")
            }
        }

        routing {
            get("/404") {
                call.respondText(
                    this::class.java
                        .getResource("/error404.html")
                        ?.readText() ?: "404",
                    ContentType.Text.Html
                )
            }

            formRoutes()
        }
    }.start(wait = true)
}
