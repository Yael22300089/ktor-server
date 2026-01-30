package com.tuempresa.app

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import java.io.File
import java.util.*

fun Route.formRoutes() {

    val uploadDir = File("uploads")
    if (!uploadDir.exists()) uploadDir.mkdirs()

    get("/") {
        call.respondText(
            this::class.java.getResource("/form.html")!!.readText(),
            ContentType.Text.Html
        )
    }

    // 📸 imágenes para el carrusel
    get("/imagenes") {
        val images = uploadDir.listFiles()
            ?.filter { it.extension.lowercase() in listOf("jpg","jpeg","png","webp") }
            ?.map { "/uploads/${it.name}" }
            ?: emptyList()

        call.respond(images)
    }

    post("/enviar") {

        val multipart = call.receiveMultipart()

        multipart.forEachPart { part ->
            if (part is PartData.FileItem && part.name == "imagenes") {

                val ext = part.originalFileName
                    ?.substringAfterLast('.', "jpg")
                    ?.lowercase()

                val file = File(uploadDir, "${UUID.randomUUID()}.$ext")

                part.streamProvider().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            part.dispose()
        }

        call.respondText("OK")
    }
}
