package com.apptolast.familyfilmapp.model.remote.tmdbResponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieWrapperRemote(
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<TmdbMovieRemote>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int,
)
