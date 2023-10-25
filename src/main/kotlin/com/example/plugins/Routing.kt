package com.example.plugins

import com.example.authentication.JwtService
import com.example.authentication.hash
import com.example.repository.Repo
import com.example.routing.recipeRoutes
import com.example.routing.userRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    val dataBase = Repo()
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }

    routing {
        get("/") {
            call.respondText("Suck deez nuts!")
        }

        userRoutes(dataBase, jwtService, hashFunction)
        recipeRoutes(dataBase, hashFunction)

    }
}
