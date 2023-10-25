package com.example.plugins

import com.example.authentication.JwtService
import com.example.repository.Repo
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtRealm = "Recipe server"
    val jwtService = JwtService()
    val db = Repo()

    authentication {
        jwt("jwt") {

            verifier(jwtService.verifier)
            realm = jwtRealm
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = db.findUserByEmail(email)
                user
            }
        }
    }
}
