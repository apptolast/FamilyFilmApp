package com.digitalsolution.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class GenreMovies(
    @SerializedName("genre_id")
    val genreId: Int? = null,

    @SerializedName("movie_id")
    val movieId: Int? = null,

    @SerializedName("movie")
    val movie: List<MovieInfoRemote>? = null,
)
