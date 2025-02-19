package com.apptolast.familyfilmapp.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.GROUPS_TABLE_NAME
import java.util.Date

@Entity(tableName = GROUPS_TABLE_NAME)
data class GroupTable(
    @PrimaryKey(autoGenerate = false)
    val groupId: String,
    var ownerId: String,
    var name: String,
    val users: List<UserTable>,
    val lastUpdated: Date?,
) {
    constructor(groupId: String) : this(
        groupId = groupId,
        ownerId = "",
        name = "",
        users = emptyList<UserTable>(),
        lastUpdated = null,
    )
}

fun Group.toGroupTable() = GroupTable(
    groupId = id,
    ownerId = ownerId,
    name = name,
    users = users.map { it.toUserTable() },
    lastUpdated = lastUpdated,
)

fun GroupTable.toGroup() = Group(
    id = groupId,
    ownerId = ownerId,
    name = name,
    users = users.map { it.toUser() },
    lastUpdated = lastUpdated,
)
