package com.example.repository

import com.example.data.model.Recipe
import com.example.data.model.User
import com.example.data.table.RecipeTable
import com.example.data.table.UserTable
import com.example.repository.DataBaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class Repo {

    suspend fun addUser(user: User) {
        dbQuery {
            UserTable.insert { ut ->
                ut[email] = user.email
                ut[name] = user.userName
                ut[hashPassword] = user.hashPassword
            }

        }
    }

    suspend fun findUserByEmail(email: String) = dbQuery {
        UserTable.select { UserTable.email.eq(email) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }
        return User(
            email = row[UserTable.email],
            userName = row[UserTable.name],
            hashPassword = row[UserTable.hashPassword]
        )
    }


    //  ========= RECIPES ==========

    suspend fun addRecipe(recipe: Recipe, email: String) {
        dbQuery {
            RecipeTable.insert { rt ->
                rt[id] = recipe.id
                rt[userEmail] = email
                rt[title] = recipe.title
                rt[ingredients] = recipe.ingredients
                rt[instructions] = recipe.instructions
                rt[date] = recipe.date
                rt[isFavorite] = recipe.isFavorite
            }
        }
    }

    suspend fun getAllRecipes(email: String): List<Recipe> = dbQuery {
        RecipeTable.select {
            RecipeTable.userEmail.eq(email)
        }.mapNotNull { rowToRecipe(it) }
    }

    suspend fun updateRecipe(recipe: Recipe, email: String) {
        dbQuery {
            RecipeTable.update(
                where = { RecipeTable.userEmail.eq(email) and RecipeTable.id.eq(recipe.id) }
            ) { rt ->
                rt[title] = recipe.title
                rt[ingredients] = recipe.ingredients
                rt[instructions] = recipe.instructions
                rt[date] = recipe.date
                rt[isFavorite] = recipe.isFavorite
            }
        }
    }

    suspend fun deleteRecipe(id: String, email: String) {
        dbQuery {
            RecipeTable.deleteWhere {
                userEmail.eq(email) and
                        RecipeTable.id.eq(id)
            }
        }
    }

    private fun rowToRecipe(row: ResultRow?): Recipe? {
        if (row == null) {
            return null
        }
        return Recipe(
            id = row[RecipeTable.id],
            title = row[RecipeTable.title],
            ingredients = row[RecipeTable.ingredients],
            instructions = row[RecipeTable.instructions],
            date = row[RecipeTable.date],
            isFavorite = row[RecipeTable.isFavorite]
        )
    }

}