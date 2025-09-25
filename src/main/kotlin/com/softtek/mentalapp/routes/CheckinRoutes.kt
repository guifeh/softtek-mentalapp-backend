package br.com.com.softtek.mentalapp.routes

import br.com.com.softtek.mentalapp.services.CheckinService
import br.com.com.softtek.mentalapp.models.CheckinRequest
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.auth.*
import io.ktor.http.*
import io.ktor.server.auth.jwt.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@kotlinx.serialization.Serializable
data class ErrorResponse(val error: String)

fun Route.checkinRoutes(checkinService: CheckinService) {

    route("/api/v1/checkins") {
        authenticate("auth-jwt") {

            post {
                try {
                    val principal = call.principal<JWTPrincipal>()
                        ?: return@post call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))

                    val userId = principal.payload.getClaim("uid").asString()
                    val req = call.receive<CheckinRequest>()

                    val created = checkinService.create(userId, req)
                    call.respond(HttpStatusCode.Created, created)

                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Erro nos dados"))
                }
            }

            get {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))

                val userId = principal.payload.getClaim("uid").asString()
                val checkins = checkinService.listByUser(userId)

                call.respond(checkins)
            }

            get("/report") {
                val principal = call.principal<JWTPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token inválido"))
                val requesterId = principal.payload.getClaim("uid").asString()
                val requesterRole = principal.payload.getClaim("role").asString()

                val startParam = call.request.queryParameters["startDate"]
                val endParam = call.request.queryParameters["endDate"]
                val targetUserId = call.request.queryParameters["userId"]

                if (startParam.isNullOrBlank() || endParam.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Parâmetros startDate e endDate são obrigatórios"))
                    return@get
                }

                if (!targetUserId.isNullOrBlank() && requesterRole != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Apenas ADMIN pode consultar outros usuários"))
                    return@get
                }

                val userIdFilter = if (targetUserId.isNullOrBlank()) requesterId else targetUserId

                try {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val startDate = LocalDate.parse(startParam, formatter)
                    val endDate = LocalDate.parse(endParam, formatter)

                    val reportList = checkinService.getCheckinsByDateRange(userIdFilter, startDate, endDate)
                    call.respond(HttpStatusCode.OK, reportList)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Formato de data inválido. Use yyyy-MM-dd"))
                }
            }

            get("/report/summary") {
                val principal = call.principal<JWTPrincipal>() ?: return@get call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("error" to "Token inválido")
                )

                val requesterId = principal.payload.getClaim("uid").asString()
                val requesterRole = principal.payload.getClaim("role").asString()

                val startParam = call.request.queryParameters["startDate"]
                val endParam = call.request.queryParameters["endDate"]
                val targetUserId = call.request.queryParameters["userId"]

                if (startParam.isNullOrBlank() || endParam.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Parâmetros startDate e endDate são obrigatórios"))
                    return@get
                }

                if (!targetUserId.isNullOrBlank() && requesterRole != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Apenas ADMIN pode consultar outros usuários"))
                    return@get
                }

                val userIdFilter = if (targetUserId.isNullOrBlank()) requesterId else targetUserId

                try {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val startDate = LocalDate.parse(startParam, formatter)
                    val endDate = LocalDate.parse(endParam, formatter)

                    val summary = checkinService.getCheckinSummary(userIdFilter, startDate, endDate)
                    call.respond(HttpStatusCode.OK, summary)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Formato de data inválido. Use yyyy-MM-dd"))
                }
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))

                val userId = principal.payload.getClaim("uid").asString()
                val role = principal.payload.getClaim("role").asString()

                val id = call.parameters["id"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Id não informado"))

                val deleted = checkinService.delete(userId, id, role == "ADMIN")

                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Check-in deletado com sucesso"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Check-in não encontrado"))
                }
            }
        }
    }
}
