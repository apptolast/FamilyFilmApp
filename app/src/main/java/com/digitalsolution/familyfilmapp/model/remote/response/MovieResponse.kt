package com.digitalsolution.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("data")
    val data: List<MovieItem>? = null,
) : BaseResponse()

data class MovieItem(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("genres")
    val genres: List<GenresItem>? = null,

    @SerializedName("adult")
    val adult: Boolean? = null,

    @SerializedName("genre_ids")
    val genreIds: List<GenresItem>? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("synopsis")
    val synopsis: String? = null,

    @SerializedName("vote_average")
    val voteAverage: Float? = null,

    @SerializedName("vote_count")
    val voteCount: Int? = null,

    @SerializedName("release_date")
    val releaseDate: String? = null,

    @SerializedName("language")
    val language: String? = null
)

data class GenresItem(

    @SerializedName("movie_id")
    val movieId: Int? = null,

    @SerializedName("genre_id")
    val genreId: Int? = null
)
