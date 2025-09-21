package br.com

import br.com.com.softtek.mentalapp.auth.JwtConfig
import br.com.configureSerialization
import com.softtek.mentalapp.db.DatabaseFactory
import com.softtek.mentalapp.db.Seed
import com.softtek.mentalapp.routes.authRoutes
import br.com.com.softtek.mentalapp.routes.userRoutes
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.module() {
    configureSerialization()
    DatabaseFactory.init()

    JwtConfig.configureKtorAuth(this)

    routing {
        authRoutes()
        userRoutes()
        get("/health") { call.respond(mapOf(
            "status" to "ok",
            "message" to "Softtek Mental App backend rodando"
        )) }
    }

    Seed.createAdminIfNotExists()
}
