package com.apptolast.familyfilmapp.model.remote.tmdbResponse

import com.google.gson.annotations.SerializedName

data class TmdbMovieRemote(
    @SerializedName("id") val id: Int,
    @SerializedName("adult") val adult: Boolean,
    @SerializedName("title") val title: String?,
    @SerializedName("popularity") val popularity: Float?,
    @SerializedName("watch/providers") val providers: ProvidersRoot?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
)


data class ProvidersRoot(
    @SerializedName("results") val results: Map<String, CountryProviders>,
)

data class CountryProviders(
    @SerializedName("link") val link: String,
    @SerializedName("flatrate") val stream: List<ProviderRemote>?,
    @SerializedName("buy") val buy: List<ProviderRemote>?,
    @SerializedName("rent") val rent: List<ProviderRemote>?,
)

data class ProviderRemote(
    @SerializedName("logo_path") val logoPath: String,
    @SerializedName("provider_id") val providerId: Int,
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("display_priority") val displayPriority: Int,
)
