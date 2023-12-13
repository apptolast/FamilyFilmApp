package com.apptolast.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class MovieRemote(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("adult")
    val adult: Boolean? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("genre_ids")
    val genre: List<GenresRemote>? = null,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("synopsis")
    val synopsis: String? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("release_date")
    val releaseDate: String? = null,

    @SerializedName("vote_average")
    val voteAverage: Float? = null,

    @SerializedName("vote_count")
    val voteCount: Int? = null,
)
