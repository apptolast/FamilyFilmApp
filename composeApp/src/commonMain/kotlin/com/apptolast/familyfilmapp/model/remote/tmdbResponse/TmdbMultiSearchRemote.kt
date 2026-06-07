package com.apptolast.familyfilmapp.model.remote.tmdbResponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMultiSearchResultRemote(
    @SerialName("id") val id: Int,
    @SerialName("media_type") val mediaType: String,
    @SerialName("adult") val adult: Boolean = false,
    @SerialName("title") val title: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("popularity") val popularity: Float? = null,
    @SerialName("vote_average") val voteAverage: Float? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
)

@Serializable
data class TmdbMultiSearchWrapperRemote(
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<TmdbMultiSearchResultRemote>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int,
)
