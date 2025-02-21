package com.apptolast.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class MovieCatalogueRemote(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("synopsis")
    val synopsis: String? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("adult")
    val adult: Boolean? = null,

    @SerializedName("release_date")
    val releaseDate: String? = null,

    @SerializedName("rating_average")
    val ratingAverage: Float? = null,

    @SerializedName("rating_value")
    val ratingValue: Float? = null,

    @SerializedName("genres")
    val genres: List<String>? = null,
)
