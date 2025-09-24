package br.com.com.softtek.mentalapp.routes

import br.com.com.softtek.mentalapp.services.CheckinService
import br.com.com.softtek.mentalapp.models.CheckinRequest
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.auth.*
import io.ktor.http.*
import io.ktor.server.auth.jwt.*

fun Route.checkinRoutes(checkinService: CheckinService) {

    route("/api/v1/checkins") {
        authenticate("auth-jwt") {

            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("uid").asString()

                val req = call.receive<CheckinRequest>()
                val created = checkinService.create(userId, req)

                call.respond(HttpStatusCode.Created, created)
            }

            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("uid").asString()

                val checkins = checkinService.listByUser(userId)
                call.respond(checkins)
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("uid").asString()
                val role = principal.payload.getClaim("role").asString()

                val id = call.parameters["id"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Id não informado")
                )

                val deleted = checkinService.delete(userId, id, role == "ADMIN")
                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Check-in deletado com sucesso"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Check-in não encontrado"))
                }
            }
        }
    }
}
