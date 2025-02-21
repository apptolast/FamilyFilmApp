package com.apptolast.familyfilmapp.model.remote.tmdbResponse

import com.apptolast.familyfilmapp.model.local.Movie
import com.google.gson.annotations.SerializedName

data class TmdbMovieRemote(
    @SerializedName("id") val id: Int,
    @SerializedName("adult") val adult: Boolean,
    @SerializedName("title") val title: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
)

fun TmdbMovieRemote.toDomain(): Movie = Movie(
    id = id,
    adult = adult,
    title = title ?: "",
    overview = overview ?: "",
    releaseDate = releaseDate ?: "",
    posterPath = posterPath ?: "",
)
