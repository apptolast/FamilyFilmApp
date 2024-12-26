package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.Movie
import com.google.gson.annotations.SerializedName

data class TmdbMovieRemote(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("original_title") val originalTitle: String? = null,
    @SerializedName("adult") val adult: Boolean? = null,
    @SerializedName("release_date") val releaseDate: String? = null, // Format: 2024-10-23
    @SerializedName("original_language") val originalLanguage: String? = null,
    @SerializedName("overview") val overview: String? = null,
    @SerializedName("popularity") val popularity: Float? = null,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("vote_average") val voteAverage: Float? = null,
    @SerializedName("vote_count") val voteCount: Int? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("genre_ids") val genresIds: List<Int>? = null,
    @SerializedName("video") val video: Boolean? = null,
)

fun TmdbMovieRemote.toDomain() = Movie(
    id = -1,
    tmdbId = id ?: -1,
    title = title ?: "",
    originalTitle = originalTitle ?: "",
    adult = adult == true,
    releaseDate = releaseDate ?: "",
    originalLanguage = originalLanguage ?: "",
    overview = overview ?: "",
    popularity = popularity ?: 0f,
    posterPath = posterPath ?: "",
    voteAverage = voteAverage ?: 0f,
    voteCount = voteCount ?: 0,
    backdropPath = backdropPath ?: "",
    genresIds = genresIds ?: emptyList(),
    video = video == true,
)
