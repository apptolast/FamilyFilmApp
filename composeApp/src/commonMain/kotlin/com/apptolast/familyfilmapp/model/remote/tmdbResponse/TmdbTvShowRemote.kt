package com.apptolast.familyfilmapp.model.remote.tmdbResponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbTvShowRemote(
    @SerialName("id") val id: Int,
    @SerialName("adult") val adult: Boolean,
    @SerialName("name") val name: String? = null,
    @SerialName("popularity") val popularity: Float? = null,
    @SerialName("vote_average") val voteAverage: Float? = null,
    @SerialName("watch/providers") val providers: ProvidersRoot? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("number_of_seasons") val numberOfSeasons: Int? = null,
    @SerialName("number_of_episodes") val numberOfEpisodes: Int? = null,
)

@Serializable
data class TmdbTvShowWrapperRemote(
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<TmdbTvShowRemote>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int,
)
