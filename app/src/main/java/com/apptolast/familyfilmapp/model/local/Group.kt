package com.apptolast.familyfilmapp.model.local

data class Group(
    val id: Int,
    val ownerId: Int,
    val name: String,
    val watchedList: List<Movie>,
    val toWatchList: List<Movie>,
    val recommendedMovie: Movie,
    val users: List<Users>,
) {
    constructor() : this(
        id = -1,
        ownerId = -1,
        name = "",
        watchedList = emptyList<Movie>(),
        toWatchList = emptyList<Movie>(),
        recommendedMovie = Movie(),
        users = emptyList<Users>(),
    )
}
