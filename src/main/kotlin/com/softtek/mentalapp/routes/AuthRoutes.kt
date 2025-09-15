package com.softtek.mentalapp.routes

import br.com.com.softtek.mentalapp.auth.JwtConfig
import br.com.com.softtek.mentalapp.models.LoginRequest
import br.com.com.softtek.mentalapp.models.LoginResponse
import br.com.com.softtek.mentalapp.services.UserService
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*

fun Route.authRoutes(userService: UserService = UserService()) {
    route("/api/v1/auth") {
        post("/login") {
            val req = call.receive<LoginRequest>()
            val user = userService.authenticate(req.email, req.password)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Credenciais inválidas"))
                return@post
            }
            val token = JwtConfig.sign(user.id, user.role)
            call.respond(LoginResponse(accessToken = token, user = user))
        }
    }
}
