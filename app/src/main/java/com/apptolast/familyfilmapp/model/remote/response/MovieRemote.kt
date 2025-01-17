package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.Movie
import com.google.gson.annotations.SerializedName

data class MovieRemote(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("tmdbId") val tmdbId: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("language") val language: String? = null,
    @SerializedName("synopsis") val synopsis: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("adult") val adult: Boolean? = null,
    @SerializedName("releaseDate") val releaseDate: String? = null, // Format: yyyy-MM-dd
    @SerializedName("voteAverage") val voteAverage: Float? = null,
    @SerializedName("voteCount") val voteCount: Int? = null,
    @SerializedName("genreIds") val genresIds: List<Int>? = null,
)

fun MovieRemote.toDomain() = Movie(
    id = id ?: -1,
    tmdbId = tmdbId ?: -1,
    title = title ?: "",
    originalTitle = title ?: "",
    adult = adult == true,
    releaseDate = releaseDate ?: "",
    originalLanguage = language ?: "",
    overview = synopsis ?: "",
    popularity = 0f,
    posterPath = image ?: "",
    voteAverage = voteAverage ?: 0f,
    voteCount = voteCount ?: 0,
    backdropPath = "",
    genresIds = genresIds ?: emptyList(),
    video = false,
)
