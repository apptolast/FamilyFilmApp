package com.apptolast.familyfilmapp.model.local

import kotlin.time.Instant

data class Group(
    val id: String,
    val ownerId: String,
    val name: String,
    val users: List<String>,
    val lastUpdated: Instant?,
) {
    constructor() : this(
        id = "",
        ownerId = "",
        name = "",
        users = emptyList(),
        lastUpdated = null,
    )
}
