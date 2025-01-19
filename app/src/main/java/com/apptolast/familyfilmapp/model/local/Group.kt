package com.apptolast.familyfilmapp.model.local

data class Group(
    val id: Int,
    val ownerId: Int,
    val name: String,
    val users: List<User>,
    val watchedList: List<Int>,
    val toWatchList: List<Int>,
){
    constructor() : this(
        id = 0,
        ownerId = 0,
        name = "",
        users = emptyList(),
        watchedList = emptyList(),
        toWatchList = emptyList(),
    )
}
