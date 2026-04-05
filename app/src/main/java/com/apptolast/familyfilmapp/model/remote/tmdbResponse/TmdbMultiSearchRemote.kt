package com.apptolast.familyfilmapp.model.remote.tmdbResponse

import com.google.gson.annotations.SerializedName

data class TmdbMultiSearchResultRemote(
    @SerializedName("id") val id: Int,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("adult") val adult: Boolean = false,
    @SerializedName("title") val title: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("popularity") val popularity: Float?,
    @SerializedName("vote_average") val voteAverage: Float?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
)

data class TmdbMultiSearchWrapperRemote(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<TmdbMultiSearchResultRemote>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int,
)
