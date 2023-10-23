package com.digitalsolution.familyfilmapp.model.local

data class GenreInfo(
    val id: Int,
    val name: String,
    val movies: List<Movie>,
) {
    constructor() : this(
        id = -1,
        name = "",
        movies = emptyList<Movie>(),
    )
}
