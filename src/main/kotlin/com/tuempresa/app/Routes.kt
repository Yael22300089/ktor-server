package com.tuempresa.app

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.io.File
import java.util.*

fun Route.formRoutes() {

    // 👉 carpeta REAL
    val uploadDir = File("src/main/resources/static.uploads")
    if (!uploadDir.exists()) uploadDir.mkdirs()

    // 🟢 Formulario
    get("/") {
        call.respondText(
            this::class.java.getResource("/form.html")?.readText()
                ?: "Formulario no encontrado",
            ContentType.Text.Html
        )
    }

    // 🟢 Listar imágenes guardadas (para el carrusel)
    get("/imagenes") {
        val images = uploadDir.listFiles()
            ?.filter { it.extension.lowercase() in listOf("jpg", "jpeg", "png", "webp") }
            ?.map { "/static.uploads/${it.name}" }
            ?: emptyList()

        call.respond(images)
    }

    // 🟢 Envío del formulario
    post("/enviar") {

        var recaptchaToken = ""
        val multipart = call.receiveMultipart()

        multipart.forEachPart { part ->

            when (part) {

                is PartData.FormItem -> {
                    if (part.name == "g-recaptcha-response") {
                        recaptchaToken = part.value
                    }
                }

                is PartData.FileItem -> {
                    if (part.name == "imagenes") {
                        val ext = part.originalFileName
                            ?.substringAfterLast('.') ?: "jpg"

                        val fileName = "${UUID.randomUUID()}.$ext"
                        val file = File(uploadDir, fileName)

                        part.streamProvider().use { input ->
                            file.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                }

                else -> {}
            }

            part.dispose()
        }

        // 🔐 reCAPTCHA (NO MODIFICADO)
        if (recaptchaToken.isBlank() || !validarRecaptcha(recaptchaToken)) {
            call.respondText(
                "❌ reCAPTCHA inválido",
                ContentType.Text.Html,
                HttpStatusCode.Forbidden
            )
            return@post
        }

        call.respondRedirect("/")
    }
}

suspend fun validarRecaptcha(token: String): Boolean {

    val secret = System.getenv("RECAPTCHA_SECRET")
        ?: "6LcBlFksAAAAANzwQxK7NNCdsAU1QNQIgMXMEJTT"

    val client = HttpClient(CIO)

    val response = client.post("https://www.google.com/recaptcha/api/siteverify") {
        setBody(
            listOf(
                "secret" to secret,
                "response" to token
            ).formUrlEncode()
        )
        headers {
            append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }
    }

    val body = response.bodyAsText()
    client.close()

    return body.contains("\"success\": true")
}
