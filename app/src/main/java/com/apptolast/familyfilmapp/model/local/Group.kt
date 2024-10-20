package com.apptolast.familyfilmapp.model.local

data class Group(
    val id: Int,
    val ownerId: Int,
    val name: String,
//    val watchedList: List<MovieCatalogue>,
//    val toWatchList: List<MovieCatalogue>,
    val users: List<User>,
) {
    constructor() : this(
        id = -1,
        ownerId = -1,
        name = "",
//        watchedList = emptyList<MovieCatalogue>(),
//        toWatchList = emptyList<MovieCatalogue>(),
        users = emptyList<User>(),
    )
}
