package com.tuempresa.app

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import java.time.LocalDate

fun Route.formRoutes() {

    get("/") {
        call.respondText(
            this::class.java
                .getResource("/form.html")
                ?.readText() ?: "Formulario no encontrado",
            ContentType.Text.Html
        )
    }

    post("/enviar") {
        val params = call.receiveParameters()

        val nombre = params["nombre"] ?: ""
        val edad = params["edad"] ?: ""
        val correo = params["correo"] ?: ""
        val telefono = params["telefono"] ?: ""
        val fecha = params["fecha"] ?: ""

        val errores = mutableListOf<String>()

        if (!nombre.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚ ]+$")))
            errores.add("El nombre solo debe contener letras")

        if (!edad.matches(Regex("^\\d+$")))
            errores.add("La edad solo debe contener números")

        if (!correo.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$")))
            errores.add("Correo inválido")

        if (!telefono.matches(Regex("^\\d{10}$")))
            errores.add("El teléfono debe tener 10 dígitos")

        try {
            LocalDate.parse(fecha)
        } catch (e: Exception) {
            errores.add("Fecha inválida")
        }

        if (errores.isNotEmpty()) {
            call.respondText(
                errores.joinToString("<br>"),
                ContentType.Text.Html
            )
        } else {
            call.respondText(
                this::class.java
                    .getResource("/result.html")
                    ?.readText() ?: "OK",
                ContentType.Text.Html
            )
        }
    }
}
