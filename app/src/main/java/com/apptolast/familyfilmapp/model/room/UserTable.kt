package com.apptolast.familyfilmapp.model.room

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.apptolast.familyfilmapp.model.local.SelectedMovie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.USERS_TABLE_NAME

//@Entity(tableName = USERS_TABLE_NAME)
//data class UserTable(
//    @PrimaryKey(autoGenerate = false)
//    var userId: String,
//    @Ignore val groupIds: HashSet<String>,
//    val email: String,
//    val language: String,
////    @Ignore val watched: List<SelectedMovie>,
////    @Ignore val toWatch: List<SelectedMovie>,
//)

@Entity(tableName = USERS_TABLE_NAME)
data class UserTable(
    @PrimaryKey(autoGenerate = false)
    val userId: String,
    @Ignore val groupIds: List<String>,
    var email: String,
    var language: String,
//    @Ignore val watched: List<SelectedMovie>,
//    @Ignore val toWatch: List<SelectedMovie>,
) {
    constructor(userId: String) : this(
        userId = userId,
        groupIds = emptyList(),
        email = "",
        language = "",
//        watched = emptyList(),
//        toWatch = emptyList(),
    )
}

fun UserTable.toUser() = User(
    id = userId,
    groupIds = groupIds,
    email = email,
    language = language,
    watched = emptyList<SelectedMovie>(),
    toWatch = emptyList<SelectedMovie>(),
//    watched = watched,
//    toWatch = toWatch,
)

fun User.toUserTable() = UserTable(
    userId = id,
    groupIds = groupIds,
    email = email,
    language = language,
//    watched = watched,
//    toWatch = toWatch,
)
