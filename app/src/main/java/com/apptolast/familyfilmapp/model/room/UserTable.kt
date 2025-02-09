package com.apptolast.familyfilmapp.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.apptolast.familyfilmapp.model.local.SelectedMovie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.USERS_TABLE_NAME

@Entity(tableName = USERS_TABLE_NAME)
data class UserTable(
    @PrimaryKey(autoGenerate = false)
    val userId: String,
    val groupIds: List<String>,
    var email: String,
    var language: String,
//    @Ignore val watched: List<SelectedMovie>,
//    @Ignore val toWatch: List<SelectedMovie>,
) {
    constructor(userId: String) : this(
        userId = userId,
        email = "",
        language = "",
        groupIds = emptyList(),
//        watched = emptyList(),
//        toWatch = emptyList(),
    )
}

fun UserTable.toUser() = User(
    id = userId,
    email = email,
    language = language,
    groupIds = groupIds,
    watched = emptyList<SelectedMovie>(),
    toWatch = emptyList<SelectedMovie>(),
)

fun User.toUserTable() = UserTable(
    userId = id,
    email = email,
    language = language,
    groupIds = groupIds,
//    watched = watched,
//    toWatch = toWatch,
)
