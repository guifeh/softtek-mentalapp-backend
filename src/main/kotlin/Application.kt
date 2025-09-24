package br.com

import br.com.com.softtek.mentalapp.auth.JwtConfig
import br.com.com.softtek.mentalapp.repositories.CheckinRepository
import br.com.com.softtek.mentalapp.routes.checkinRoutes
import br.com.configureSerialization
import com.softtek.mentalapp.db.DatabaseFactory
import com.softtek.mentalapp.db.Seed
import com.softtek.mentalapp.routes.authRoutes
import br.com.com.softtek.mentalapp.routes.userRoutes
import br.com.com.softtek.mentalapp.services.CheckinService
import com.mongodb.client.MongoDatabase
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.module() {
    configureSerialization()
    DatabaseFactory.init()

    JwtConfig.configureKtorAuth(this)

    val checkinRepository = CheckinRepository()
    val checkinService = CheckinService(checkinRepository)

    routing {
        authRoutes()
        userRoutes()
        checkinRoutes(checkinService)
        get("/health") { call.respond(mapOf(
            "status" to "ok",
            "message" to "Softtek Mental App backend rodando"
        )) }
    }

    Seed.createAdminIfNotExists()
}
