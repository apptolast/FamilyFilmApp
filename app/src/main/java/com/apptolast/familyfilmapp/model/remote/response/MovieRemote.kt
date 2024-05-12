package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.mapper.GenreMapper.toDomain
import com.google.gson.annotations.SerializedName
import java.util.Date

data class MovieRemote(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("adult")
    val adult: Boolean? = null,

    @SerializedName("genres")
    val genres: List<GenreRemote>? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("synopsis")
    val synopsis: String? = null,

    @SerializedName("vote_average")
    val voteAverage: Float? = null,

    @SerializedName("vote_count")
    val voteCount: Int? = null,

    @SerializedName("release_date")
    val releaseDate: Date? = null,
)

fun MovieRemote.toDomain() = Movie(
    id = id ?: -1,
    title = title ?: "",
    isAdult = adult ?: false,
    genres = genres?.map { it.toDomain() } ?: emptyList(),
    image = image ?: "",
    synopsis = synopsis ?: "",
    voteAverage = voteAverage ?: 0f,
    voteCount = voteCount ?: 0,
    releaseDate = releaseDate ?: Date(),
)
