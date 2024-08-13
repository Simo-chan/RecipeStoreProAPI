package com.example.data.table

import org.jetbrains.exposed.sql.Table

object RecipeTable : Table() {

    val id = varchar("id", 512)
    val userEmail = varchar("userEmail", 512).references(UserTable.email)
    val title = text("title")
    val ingredients = text("ingredients")
    val instructions = text("instructions")
    val date = long("date")
    val isFavorite= bool("isFavorite")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}