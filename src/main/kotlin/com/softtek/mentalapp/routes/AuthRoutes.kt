package com.softtek.mentalapp.routes

import br.com.com.softtek.mentalapp.auth.JwtConfig
import br.com.com.softtek.mentalapp.models.*
import br.com.com.softtek.mentalapp.services.UserService
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.auth.*
import io.ktor.http.*
import io.ktor.server.auth.jwt.JWTPrincipal

fun Route.authRoutes(userService: UserService = UserService()) {

    route("/api/v1/auth") {
        post("/login") {
            val req = call.receive<LoginRequest>()
            val user = userService.authenticate(req.email, req.password)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Credenciais inválidas"))
                return@post
            }

            val accessToken = JwtConfig.generateAccessToken(user.id, user.role)
            val refreshToken = JwtConfig.generateRefreshToken(user.id, user.role)

            call.respond(
                LoginResponse(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    user = user
                )
            )
        }

        post("/refresh") {
            val req = call.receive<RefreshRequest>()
            try {
                val decoded = JwtConfig.verifyToken(req.refreshToken)
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Refresh token inválido ou expirado"))
                val uid = decoded.getClaim("uid").asString()
                val role = decoded.getClaim("role").asString()

                val user = userService.getById(uid)
                    ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "Usuário não encontrado"))

                val newAccessToken = JwtConfig.generateAccessToken(user.id, user.role)
                val newRefreshToken = JwtConfig.generateRefreshToken(user.id, user.role)

                call.respond(
                    mapOf(
                        "accessToken" to newAccessToken,
                        "refreshToken" to newRefreshToken
                    )
                )
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Refresh token inválido ou expirado"))
            }
        }
    }

    authenticate("auth-jwt") {
        get("/users/me") {
            val principal = call.principal<JWTPrincipal>()
            val uid = principal!!.payload.getClaim("uid").asString()
            val user = userService.getById(uid)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Usuário não encontrado"))

            call.respond(user)
        }
    }
}
