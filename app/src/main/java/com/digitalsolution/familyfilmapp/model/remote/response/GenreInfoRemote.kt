package com.digitalsolution.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class GenreInfoRemote(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val genreName: String? = null,

    @SerializedName("movies")
    val movies: List<GenreMovies>? = null,
)
