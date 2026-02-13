//package com.tuempresa.app
//
//import io.ktor.server.application.*
//import io.ktor.server.routing.*
//import io.ktor.server.response.*
//import io.ktor.server.request.*
//import io.ktor.http.*
//import io.ktor.http.content.*
//import java.io.File
//import java.util.*
//import java.net.URL
//import javax.net.ssl.HttpsURLConnection
//import org.json.JSONObject
//
//fun Route.formRoutes() {
//
//    val uploadDir = File("uploads")
//    if (!uploadDir.exists()) uploadDir.mkdirs()
//
//    get("/") {
//        call.respondText(
//            this::class.java.getResource("/form.html")!!.readText(),
//            ContentType.Text.Html
//        )
//    }
//
//    // 📸 imágenes para carrusel
//    get("/imagenes") {
//        val images = uploadDir.listFiles()
//            ?.filter { it.extension.lowercase() in listOf("jpg","jpeg","png","webp") }
//            ?.map { "/uploads/${it.name}" }
//            ?: emptyList()
//
//        call.respond(images)
//    }
//
//    post("/enviar") {
//
//        var nombre = ""
//        var edad = ""
//        var correo = ""
//        var telefono = ""
//        var fecha = ""
//        var recaptchaToken = ""
//
//        val multipart = call.receiveMultipart()
//
//        multipart.forEachPart { part ->
//
//            when (part) {
//
//                is PartData.FormItem -> {
//                    when(part.name){
//                        "nombre" -> nombre = part.value.trim()
//                        "edad" -> edad = part.value.trim()
//                        "correo" -> correo = part.value.trim()
//                        "telefono" -> telefono = part.value.trim()
//                        "fecha" -> fecha = part.value.trim()
//                        "g-recaptcha-response" -> recaptchaToken = part.value
//                    }
//                }
//
//                is PartData.FileItem -> {
//                    if (part.name == "imagenes") {
//
//                        val ext = part.originalFileName
//                            ?.substringAfterLast('.', "jpg")
//                            ?.lowercase()
//
//                        val file = File(uploadDir, "${UUID.randomUUID()}.$ext")
//
//                        part.streamProvider().use { input ->
//                            file.outputStream().use { output ->
//                                input.copyTo(output)
//                            }
//                        }
//                    }
//                }
//
//                else -> {}
//            }
//
//            part.dispose()
//        }
//
//        /* =============================
//           ✅ VALIDACIONES PROFESIONALES
//           ============================= */
//
//        val regexNombre = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")
//        val regexEdad = Regex("^\\d+$")
//        val regexCorreo = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
//        val regexTelefono = Regex("^\\d{10}\$")
//
//        if (!regexNombre.matches(nombre)) {
//            call.respond(HttpStatusCode.BadRequest, "Nombre inválido")
//            return@post
//        }
//
//        if (!regexEdad.matches(edad)) {
//            call.respond(HttpStatusCode.BadRequest, "Edad inválida")
//            return@post
//        }
//
//        if (!regexCorreo.matches(correo)) {
//            call.respond(HttpStatusCode.BadRequest, "Correo inválido")
//            return@post
//        }
//
//        if (!regexTelefono.matches(telefono)) {
//            call.respond(HttpStatusCode.BadRequest, "Teléfono debe tener 10 dígitos")
//            return@post
//        }
//
//        try {
//            java.sql.Date.valueOf(fecha)
//        } catch (e: Exception) {
//            call.respond(HttpStatusCode.BadRequest, "Fecha inválida")
//            return@post
//        }
//
//        /* =============================
//           🔐 VALIDAR RECAPTCHA
//           ============================= */
//
//        val secretKey = "6LezqGgsAAAAAK4O5uCsHMNRM9LmAvSwmyIut-pV"
//
//        val url = URL("https://www.google.com/recaptcha/api/siteverify")
//        val params = "secret=$secretKey&response=$recaptchaToken"
//
//        val conn = url.openConnection() as HttpsURLConnection
//        conn.requestMethod = "POST"
//        conn.doOutput = true
//
//        conn.outputStream.use {
//            it.write(params.toByteArray())
//        }
//
//        val response = conn.inputStream.bufferedReader().readText()
//        val json = JSONObject(response)
//
//        if (!json.getBoolean("success")) {
//            call.respond(HttpStatusCode.Forbidden, "Captcha inválido")
//            return@post
//        }
//
//        /* =============================
//           ✅ INSERTAR BD
//           ============================= */
//
//        Database.getConnection().use { connDb ->
//
//            val sql = """
//            INSERT INTO registros(nombre,edad,correo,telefono,fecha)
//            VALUES(?,?,?,?,?)
//        """
//
//            connDb.prepareStatement(sql).use { ps ->
//
//                ps.setString(1, nombre)
//                ps.setInt(2, edad.toInt())
//                ps.setString(3, correo)
//                ps.setString(4, telefono)
//                ps.setDate(5, java.sql.Date.valueOf(fecha))
//
//                ps.executeUpdate()
//            }
//        }
//
//        call.respondText("OK")
//    }
//}


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
                        "nombre" -> nombre = part.value.trim()
                        "edad" -> edad = part.value.trim()
                        "correo" -> correo = part.value.trim()
                        "telefono" -> telefono = part.value.trim()
                        "fecha" -> fecha = part.value.trim()
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

        // ===============================
        // 🔐 VALIDAR RECAPTCHA
        // ===============================

        val secretKey = "6LezqGgsAAAAAK4O5uCsHMNRM9LmAvSwmyIut-pV"

        val url = URL("https://www.google.com/recaptcha/api/siteverify")
        val params = "secret=$secretKey&response=$recaptchaToken"

        val conn = url.openConnection() as HttpsURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true

        conn.outputStream.use {
            it.write(params.toByteArray())
        }

        val response = conn.inputStream.bufferedReader().readText()
        val json = JSONObject(response)

        if (!json.getBoolean("success")) {
            call.respond(HttpStatusCode.Forbidden, "Captcha inválido")
            return@post
        }

        // ===============================
        // ✅ VALIDACIONES BACKEND
        // ===============================

        val nombreRegex = Regex("^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$")
        val edadRegex = Regex("^[0-9]+$")
        val correoRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        val telefonoRegex = Regex("^[0-9]{10}$")
        val fechaRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")

        if (!nombreRegex.matches(nombre)) {
            call.respond(HttpStatusCode.BadRequest,"Nombre solo acepta letras")
            return@post
        }

        if (!edadRegex.matches(edad)) {
            call.respond(HttpStatusCode.BadRequest,"Edad solo acepta números")
            return@post
        }

        if (!correoRegex.matches(correo)) {
            call.respond(HttpStatusCode.BadRequest,"Correo inválido")
            return@post
        }

        if (!telefonoRegex.matches(telefono)) {
            call.respond(HttpStatusCode.BadRequest,"Teléfono debe tener 10 dígitos")
            return@post
        }

        if (!fechaRegex.matches(fecha)) {
            call.respond(HttpStatusCode.BadRequest,"Fecha inválida")
            return@post
        }

        // ===============================
        // ✅ INSERTAR BD
        // ===============================

        Database.getConnection().use { connDb ->

            val sql = """
                INSERT INTO registros(nombre,edad,correo,telefono,fecha)
                VALUES(?,?,?,?,?)
            """

            connDb.prepareStatement(sql).use { ps ->

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
