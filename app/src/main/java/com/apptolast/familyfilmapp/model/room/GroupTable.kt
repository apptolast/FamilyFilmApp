package com.apptolast.familyfilmapp.model.room

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.GROUPS_TABLE_NAME

@Entity(tableName = GROUPS_TABLE_NAME)
data class GroupTable(
    @PrimaryKey(autoGenerate = false)
    val groupId: String,
    var ownerId: String,
    var name: String,
    @Ignore val users: List<UserTable>,
//    @Ignore val watchedList: List<Int>, // List of movie ids
//    @Ignore val toWatchList: List<Int>, // List of movie ids
) {
    constructor(groupId: String) : this(
        groupId = groupId,
        ownerId = "",
        name = "",
        users = emptyList<UserTable>(),
//        watchedList = emptyList<Int>(),
//        toWatchList = emptyList<Int>(),
    )
}

fun Group.toGroupTable() = GroupTable(
    groupId = id,
    ownerId = ownerId,
    name = name,
    users = users.map { it.toUserTable() },
//    watchedList = watchedList,
//    toWatchList = toWatchList,
)

fun GroupTable.toGroup(/*users:List<User>*/) = Group(
    id = groupId,
    ownerId = ownerId,
    name = name,
    users = users.map { it.toUser() },
    watchedList = emptyList<Int>(),
    toWatchList = emptyList<Int>(),
)
