package com.apptolast.familyfilmapp.model.remote.tmdbResponse

import com.google.gson.annotations.SerializedName

data class TmdbTvShowRemote(
    @SerializedName("id") val id: Int,
    @SerializedName("adult") val adult: Boolean,
    @SerializedName("name") val name: String?,
    @SerializedName("popularity") val popularity: Float?,
    @SerializedName("vote_average") val voteAverage: Float?,
    @SerializedName("watch/providers") val providers: ProvidersRoot?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int?,
    @SerializedName("number_of_episodes") val numberOfEpisodes: Int?,
)

data class TmdbTvShowWrapperRemote(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<TmdbTvShowRemote>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int,
)
