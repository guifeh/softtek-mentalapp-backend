package br.com

import com.softtek.mentalapp.db.DatabaseFactory
import com.softtek.mentalapp.routes.healthRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.routing

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    DatabaseFactory.init()

    routing {
        healthRoutes()
    }
}
