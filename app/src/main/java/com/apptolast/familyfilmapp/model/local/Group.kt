package com.apptolast.familyfilmapp.model.local

data class Group(
    val id: Int,
    val ownerId: Int,
    val name: String,
    val users: List<User>,
    val watched: List<MovieSelectedByUsers>,
    val toWatch: List<MovieSelectedByUsers>,
) {
    constructor() : this(
        id = -1,
        ownerId = -1,
        name = "",
        users = emptyList<User>(),
        watched = emptyList<MovieSelectedByUsers>(),
        toWatch = emptyList<MovieSelectedByUsers>(),
    )
}

data class MovieSelectedByUsers(
    val usersId: List<Int>,
    val movieId: Int,
) {
    constructor() : this(
        usersId = emptyList(),
        movieId = -1,
    )
}
