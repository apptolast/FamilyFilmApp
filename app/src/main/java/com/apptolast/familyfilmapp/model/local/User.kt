package com.apptolast.familyfilmapp.model.local

import com.apptolast.familyfilmapp.model.local.types.MovieStatus

data class User(
    val id: String,
    val email: String,
    val language: String,
    val statusMovies: Map<String, MovieStatus>, // Map with key-value pair: MovieId, Status
) {
    constructor() : this(
        id = "",
        email = "",
        language = "",
        statusMovies = mapOf(),
    )
}
