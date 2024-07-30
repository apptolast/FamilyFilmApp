package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.extensions.toDate
import com.apptolast.familyfilmapp.model.local.MovieCatalogue
import com.google.gson.annotations.SerializedName
import java.util.Date

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


fun MovieCatalogueRemote.toDomain() = MovieCatalogue(
    id = id ?: -1,
    title = title ?: "",
    synopsis = synopsis ?: "",
    image = image ?: "",
    adult = adult ?: false,
    releaseDate = releaseDate?.toDate("yyyy-MM-dd'T'HH:mm:ss") ?: Date(),
    voteAverage = ratingAverage ?: 0f,
    ratingValue = ratingValue ?: 0f,
    genres = genres ?: emptyList(),
)
