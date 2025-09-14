package com.softtek.mentalapp.routes

import br.com.com.softtek.mentalapp.services.UserService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(service: UserService = UserService()) {

    route("/usuarios") {
        get {
            call.respond(service.listAll())
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "ID obrigatório",
                status = io.ktor.http.HttpStatusCode.BadRequest
            )
            val user = service.getById(id)
            if (user != null) {
                call.respond(user)
            } else {
                call.respondText("Usuário não encontrado", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }

        post {
            val req = call.receive<br.com.com.softtek.mentalapp.models.UserCreateRequest>()
            val created = service.register(req)
            call.respond(created)
        }

        delete("/{id}") {
            val id = call.parameters["id"]
                ?: return@delete call.respondText("Id não informado", status = io.ktor.http.HttpStatusCode.BadRequest)

            val user = service.getById(id)
            if (user != null) {
                service.delete(id)
                call.respondText("Usuário deletado com sucesso")
            } else {
                call.respondText("Usuário não encontrado", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}
