package com.apptolast.familyfilmapp.model.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.USERS_TABLE_NAME

@Entity(
    tableName = USERS_TABLE_NAME,
    indices = [
        Index(value = ["email"]),
        Index(value = ["username"]),
    ],
)
data class UserTable(
    @PrimaryKey(autoGenerate = false)
    val userId: String,
    var email: String,
    var language: String,
    var photoUrl: String,
    var username: String = "",
) {
    constructor(userId: String) : this(
        userId = userId,
        email = "",
        language = "",
        photoUrl = "",
        username = "",
    )
}

fun UserTable.toUser() = User(
    id = userId,
    email = email,
    language = language,
    photoUrl = photoUrl,
    username = username.takeIf { it.isNotBlank() },
)

fun User.toUserTable() = UserTable(
    userId = id,
    email = email,
    language = language,
    photoUrl = photoUrl,
    username = username.orEmpty(),
)
