package com.apptolast.familyfilmapp.model.remote.tmdbResponse

import com.google.gson.annotations.SerializedName

data class TmdbMovieWrapperRemote(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<TmdbMovieRemote>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int,
)
