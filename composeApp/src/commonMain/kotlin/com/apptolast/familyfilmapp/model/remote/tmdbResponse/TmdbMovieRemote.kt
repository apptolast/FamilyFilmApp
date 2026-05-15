package com.apptolast.familyfilmapp.model.remote.tmdbResponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieRemote(
    @SerialName("id") val id: Int,
    @SerialName("adult") val adult: Boolean,
    @SerialName("title") val title: String? = null,
    @SerialName("popularity") val popularity: Float? = null,
    @SerialName("vote_average") val voteAverage: Float? = null,
    @SerialName("watch/providers") val providers: ProvidersRoot? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
)

@Serializable
data class ProvidersRoot(@SerialName("results") val results: Map<String, CountryProviders> = emptyMap())

@Serializable
data class CountryProviders(
    @SerialName("link") val link: String? = null,
    @SerialName("flatrate") val stream: List<ProviderRemote>? = null,
    @SerialName("buy") val buy: List<ProviderRemote>? = null,
    @SerialName("rent") val rent: List<ProviderRemote>? = null,
)

@Serializable
data class ProviderRemote(
    @SerialName("logo_path") val logoPath: String,
    @SerialName("provider_id") val providerId: Int,
    @SerialName("provider_name") val providerName: String,
    @SerialName("display_priority") val displayPriority: Int = 0,
)
