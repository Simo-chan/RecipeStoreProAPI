package com.example.routing

import com.example.authentication.JwtService
import com.example.data.model.LoginRequest
import com.example.data.model.RegisterRequest
import com.example.data.model.SimpleResponse
import com.example.data.model.User
import com.example.repository.Repo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


private const val LOGIN_UNSUCCESSFUL = "Email or password is incorrect"
const val MISSING_FIELDS = "FIll all fields"
const val UNEXPECTED_PROBLEM = "Some problem occurred"

const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"


fun Route.userRoutes(
    db: Repo,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {

    post(REGISTER_REQUEST) {
        val registerRequest = try {
            call.receive<RegisterRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, MISSING_FIELDS))
            return@post
        }

        try {
            val user = User(
                registerRequest.email,
                hashFunction(registerRequest.password), registerRequest.name
            )
            db.addUser(user)
            call.respond(HttpStatusCode.OK, SimpleResponse(true, jwtService.generateToken(user)))

        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                SimpleResponse(false, e.message ?: UNEXPECTED_PROBLEM)
            )
        }
    }

    post(LOGIN_REQUEST) {
        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, MISSING_FIELDS))
            return@post
        }

        try {
            val user = db.findUserByEmail(loginRequest.email)

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, LOGIN_UNSUCCESSFUL))
            } else {

                if (user.hashPassword == hashFunction(loginRequest.password)) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, jwtService.generateToken(user)))
                } else {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, LOGIN_UNSUCCESSFUL))
                }
            }

        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                SimpleResponse(false, e.message ?: UNEXPECTED_PROBLEM)
            )
        }
    }
}
