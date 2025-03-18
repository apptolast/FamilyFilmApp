package com.apptolast.familyfilmapp.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.USERS_TABLE_NAME
import com.apptolast.familyfilmapp.room.converters.MapStatusConverter

@Entity(tableName = USERS_TABLE_NAME)
data class UserTable(
    @PrimaryKey(autoGenerate = false)
    val userId: String,
    var email: String,
    var language: String,
    @TypeConverters(MapStatusConverter::class)
    val statusMovies: Map<String, MovieStatus>,
) {
    constructor(userId: String) : this(
        userId = userId,
        email = "",
        language = "",
        statusMovies = mapOf(),
    )
}

fun UserTable.toUser() = User(
    uid = userId,
    email = email,
    userName = "",
    language = language,
    statusMovies = statusMovies,
)

fun User.toUserTable() = UserTable(
    userId = uid,
    email = email,
    language = language,
    statusMovies = statusMovies,
)
