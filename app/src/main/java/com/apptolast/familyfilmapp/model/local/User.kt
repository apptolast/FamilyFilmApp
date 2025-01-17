package com.apptolast.familyfilmapp.model.local

data class User(
    val id: Int,
    val email: String,
    val language: String,
    val seenMovies: List<MovieName>,
    val toSeeMovies: List<MovieName>,
    val joinedGroupsIds: List<Int>,
) {
    constructor() : this(
        id = -1,
        email = "",
        language = "",
        seenMovies = emptyList(),
        toSeeMovies = emptyList(),
        joinedGroupsIds = emptyList(),
    )
}

data class MovieName(val id: Int, val title: String)
