package com.tuempresa.app

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import java.io.File
import java.time.LocalDate
import java.util.*

fun Route.formRoutes() {

    get("/") {
        call.respondText(
            this::class.java.getResource("/form.html")?.readText()
                ?: "Formulario no encontrado",
            ContentType.Text.Html
        )
    }

    post("/enviar") {

        val multipart = call.receiveMultipart()
        val uploadDir = File("src/main/resources/static/uploads")
        if (!uploadDir.exists()) uploadDir.mkdirs()

        multipart.forEachPart { part ->

            if (part is PartData.FileItem) {
                val ext = part.originalFileName?.substringAfterLast('.') ?: "jpg"
                val fileName = "${UUID.randomUUID()}.$ext"
                val file = File(uploadDir, fileName)

                part.streamProvider().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            part.dispose()
        }

        call.respondText(
            this::class.java.getResource("/result.html")?.readText() ?: "OK",
            ContentType.Text.Html
        )
    }
}
