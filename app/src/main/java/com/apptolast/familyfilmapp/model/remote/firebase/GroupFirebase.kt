package com.apptolast.familyfilmapp.model.remote.firebase

import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.room.GroupTable
import com.apptolast.familyfilmapp.model.room.toUserTable
import com.apptolast.familyfilmapp.repositories.Repository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import java.util.Date

data class GroupFirebase(
    val id: String,
    var ownerId: String,
    var name: String,
    val users: List<String>,
    val watchedList: List<Int>, // List of movie ids
    val toWatchList: List<Int>, // List of movie ids
    val lastUpdated: Date?,
) {
    constructor() : this(
        id = "",
        ownerId = "",
        name = "",
        users = emptyList(),
        watchedList = emptyList(),
        toWatchList = emptyList(),
        lastUpdated = null,
    )
}

suspend fun GroupFirebase.toGroupTable(repository: Repository) = GroupTable(
    groupId = id,
    ownerId = ownerId,
    name = name,
    users = users.map { userId ->
        repository.getUserById(userId).map { it.toUserTable() }.single()
    },
    watchedList = watchedList,
    toWatchList = toWatchList,
    lastUpdated = lastUpdated,
)

fun GroupFirebase.toGroup() = Group(
    id = id,
    ownerId = ownerId,
    name = name,
    users = emptyList<User>(), // Fetch users from repository in a later step
    watchedList = watchedList,
    toWatchList = toWatchList,
    lastUpdated = lastUpdated,
)

fun Group.toGroupFirebase(group: Group) = GroupFirebase(
    id = id,
    ownerId = ownerId,
    name = name,
    users = users.map { it.id },
    watchedList = emptyList<Int>(),
    toWatchList = emptyList<Int>(),
    lastUpdated = lastUpdated,
)
