package com.apptolast.familyfilmapp.model.remote.tmdbResponse

import com.google.gson.annotations.SerializedName

data class TmdbMovieRemote(
    @SerializedName("id") val id: Int,
    @SerializedName("adult") val adult: Boolean,
    @SerializedName("genre_ids") val genreIds: List<Int>?,
    @SerializedName("title") val title: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
)
