package br.com.com.softtek.mentalapp.routes

import br.com.com.softtek.mentalapp.models.AssessmentRequest
import br.com.com.softtek.mentalapp.services.AssessmentService
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Route.assessmentRoutes(service: AssessmentService){
    route("/api/v1/assessments"){
        authenticate("auth-jwt") {

            post{
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.payload.getClaim("uid").asString()

                val req = call.receive<AssessmentRequest>()
                val created = service.create(userId, req)

                call.respond(HttpStatusCode.Created, created)
            }

            get {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.payload.getClaim("uid").asString()

                val list = service.listByUser(userId)
                call.respond(HttpStatusCode.OK, list)
            }
            get("/summary") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("uid").asString()

                val summary = service.getSummary(userId)
                call.respond(summary)
            }
        }
    }
}