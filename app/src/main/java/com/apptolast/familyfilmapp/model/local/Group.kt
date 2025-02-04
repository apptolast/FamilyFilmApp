package com.apptolast.familyfilmapp.model.local

data class Group(
    val id: String,
    val ownerId: String,
    val name: String,
    val users: List<User>,
    val watchedList: List<Int>, // List of movie ids
    val toWatchList: List<Int>, // List of movie ids
) {
    constructor() : this(
        id = "",
        ownerId = "",
        name = "",
        users = emptyList(),
        watchedList = emptyList(),
        toWatchList = emptyList(),
    )
}
