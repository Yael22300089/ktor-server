package com.tuempresa.app

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import java.io.File
import java.util.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import java.io.OutputStreamWriter
import org.json.JSONObject

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

        var nombre = ""
        var edad = ""
        var correo = ""
        var telefono = ""
        var fecha = ""
        var recaptchaToken = ""

        val multipart = call.receiveMultipart()

        multipart.forEachPart { part ->

            when (part) {

                is PartData.FormItem -> {

                    when(part.name){
                        "nombre" -> nombre = part.value
                        "edad" -> edad = part.value
                        "correo" -> correo = part.value
                        "telefono" -> telefono = part.value
                        "fecha" -> fecha = part.value
                        "g-recaptcha-response" -> recaptchaToken = part.value
                    }
                }

                is PartData.FileItem -> {
                    if (part.name == "imagenes") {

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
                }

                else -> {}
            }

            part.dispose()
        }

        // ==========================
        // 🔒 VALIDAR RECAPTCHA
        // ==========================

        val secretKey = "TU_SECRET_KEY"

        val url = URL("https://www.google.com/recaptcha/api/siteverify")
        val conn = url.openConnection() as HttpsURLConnection

        conn.requestMethod = "POST"
        conn.doOutput = true

        val params = "secret=$secretKey&response=$recaptchaToken"

        OutputStreamWriter(conn.outputStream).use {
            it.write(params)
        }

        val response = conn.inputStream.bufferedReader().readText()
        val json = JSONObject(response)

        val success = json.getBoolean("success")

        if(!success){
            call.respond(HttpStatusCode.Forbidden,"Captcha inválido")
            return@post
        }

        // ==========================
        // ✅ INSERTAR EN BD
        // ==========================

        Database.getConnection().use { connDB ->

            val sql = """
            INSERT INTO registros(nombre,edad,correo,telefono,fecha)
            VALUES(?,?,?,?,?)
        """

            connDB.prepareStatement(sql).use { ps ->

                ps.setString(1, nombre)
                ps.setInt(2, edad.toInt())
                ps.setString(3, correo)
                ps.setString(4, telefono)
                ps.setDate(5, java.sql.Date.valueOf(fecha))

                ps.executeUpdate()
            }
        }

        call.respondText("OK")
    }
}
