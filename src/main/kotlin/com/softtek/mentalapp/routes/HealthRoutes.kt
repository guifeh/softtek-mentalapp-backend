package com.softtek.mentalapp.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.healthRoutes(){
    get("/health"){
        call.respond(mapOf("status" to "ok", "message" to "Softtek Mental App backend rodando"))
    }
}

