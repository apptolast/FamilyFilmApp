package com.apptolast.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class GenreMoviesRemote(
    @SerializedName("genre_id")
    val genreId: Int? = null,

    @SerializedName("movie_id")
    val movieId: Int? = null,

    @SerializedName("movie")
    val movie: MovieRemote? = null,
)
