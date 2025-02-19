package com.apptolast.familyfilmapp.model.local

import java.util.Date

data class Group(
    val id: String,
    val ownerId: String,
    val name: String,
    val users: List<User>,
    val lastUpdated: Date?,
) {
    constructor() : this(
        id = "",
        ownerId = "",
        name = "",
        users = emptyList(),
        lastUpdated = null,
    )
}
