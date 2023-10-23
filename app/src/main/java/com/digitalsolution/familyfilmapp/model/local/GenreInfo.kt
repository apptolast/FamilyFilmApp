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

    constructor(id: Int, name: String, movies: List<List<Movie>?>) : this(
        id = id,
        name = name,
        movies = movies.filterNotNull().flatten(),
    )


}
