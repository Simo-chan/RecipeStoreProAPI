package com.example.routing

import com.example.data.model.Recipe
import com.example.data.model.SimpleResponse
import com.example.data.model.User
import com.example.repository.Repo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val RECIPES = "$API_VERSION/recipes"
const val CREATE_RECIPE = "$RECIPES/create"
const val UPDATE_RECIPE = "$RECIPES/update"
const val DELETE_RECIPE = "$RECIPES/delete"

private const val RECIPE_ADDED = "Recipe added"
private const val RECIPE_UPDATED = "Recipe updated"
private const val RECIPE_DELETED = "Recipe deleted"
private const val QUERY_PARAMS_MISSING = "QueryParameter: id is not present"

fun Route.recipeRoutes(
    db: Repo,
    hashFunction: (String) -> String
) {

    authenticate("jwt") {

        post(CREATE_RECIPE) {
            val recipe = try {
                call.receive<Recipe>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, MISSING_FIELDS))
                return@post
            }

            try {

                val email = call.principal<User>()!!.email
                db.addRecipe(recipe, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, RECIPE_ADDED))

            } catch (e: Exception) {

                call.respond(HttpStatusCode.Conflict, e.message ?: UNEXPECTED_PROBLEM)
            }
        }

        get(RECIPES) {
            try {

                val email = call.principal<User>()!!.email
                val recipes = db.getAllRecipes(email)
                call.respond(HttpStatusCode.OK, recipes)
            } catch (e: Exception) {

                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: UNEXPECTED_PROBLEM))

            }
        }

        post(UPDATE_RECIPE) {
            val recipe = try {
                call.receive<Recipe>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, MISSING_FIELDS))
                return@post
            }

            try {

                val email = call.principal<User>()!!.email
                db.updateRecipe(recipe, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, RECIPE_UPDATED))

            } catch (e: Exception) {

                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: UNEXPECTED_PROBLEM))
            }
        }

        delete(DELETE_RECIPE) {
            val recipeId = try {
                call.request.queryParameters["id"]!!
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, QUERY_PARAMS_MISSING))
                return@delete
            }

            try {
                val email = call.principal<User>()!!.email
                db.deleteRecipe(recipeId, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, RECIPE_DELETED))

            } catch (e: Exception) {

                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: UNEXPECTED_PROBLEM))
            }
        }
    }
}