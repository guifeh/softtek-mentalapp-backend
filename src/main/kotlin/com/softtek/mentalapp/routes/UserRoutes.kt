package br.com.com.softtek.mentalapp.routes

import br.com.com.softtek.mentalapp.models.*
import br.com.com.softtek.mentalapp.services.UserService
import br.com.com.softtek.mentalapp.auth.JwtConfig
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.auth.*
import io.ktor.http.*
import io.ktor.server.auth.jwt.JWTPrincipal
import br.com.com.softtek.mentalapp.models.*

fun Route.userRoutes(userService: UserService = UserService()) {

    route("/api/v1/users") {

        post("/register"){
            try {
                val req = call.receive<UserCreateRequest>()
                val user = userService.register(req)
                call.respond(HttpStatusCode.Created, user)
            }catch (e: IllegalArgumentException){
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Erro inesperado: ${e.message}")
                )
            }
        }

        authenticate("auth-jwt") {

            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val uid = principal!!.payload.getClaim("uid").asString()
                val user = userService.getById(uid)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Usuário não encontrado"))

                call.respond(user)
            }

            get {
                val principal = call.principal<JWTPrincipal>()
                val role = principal!!.payload.getClaim("role").asString()
                if (role != "ADMIN") {
                    return@get call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Acesso negado"))
                }

                val users = userService.listAll().map { it }
                call.respond(users)
            }

            put("/{id}") {
                val id = call.parameters["id"]
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Id não informado")

                val req = call.receive<UserUpdateRequest>()

                val updated = userService.updateUser(id, req)
                    ?: return@put call.respond(HttpStatusCode.NotFound, "Usuário não encontrado")

                call.respond(updated)
            }

            delete("/{id}") {
                val idParam = call.parameters["id"] ?: return@delete call.respondText(
                    "Id não informado",
                    status = HttpStatusCode.BadRequest
                )

                val principal = call.principal<JWTPrincipal>()
                val role = principal!!.payload.getClaim("role").asString()
                if (role != "ADMIN") {
                    return@delete call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Acesso negado"))
                }

                val deleted = userService.delete(idParam)
                if (deleted) {
                    call.respondText("Usuário deletado com sucesso")
                } else {
                    call.respondText("Usuário não encontrado", status = HttpStatusCode.NotFound)
                }
            }
        }
    }
}

