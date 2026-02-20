package com.apptolast.familyfilmapp.model.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.USERS_TABLE_NAME

@Entity(
    tableName = USERS_TABLE_NAME,
    indices = [Index(value = ["email"])],
)
data class UserTable(
    @PrimaryKey(autoGenerate = false)
    val userId: String,
    var email: String,
    var language: String,
    var photoUrl: String,
) {
    constructor(userId: String) : this(
        userId = userId,
        email = "",
        language = "",
        photoUrl = "",
    )
}

fun UserTable.toUser() = User(
    id = userId,
    email = email,
    language = language,
    photoUrl = photoUrl,
)

fun User.toUserTable() = UserTable(
    userId = id,
    email = email,
    language = language,
    photoUrl = photoUrl,
)
