package com.digitalsolution.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class GenresRemote(

    @SerializedName("movie_id")
    val movieId: Int? = null,

    @SerializedName("genre_id")
    val genreId: Int? = null,

    @SerializedName("genre")
    val genre: GenreRemote? = null,
)

data class GenreRemote(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,
)
