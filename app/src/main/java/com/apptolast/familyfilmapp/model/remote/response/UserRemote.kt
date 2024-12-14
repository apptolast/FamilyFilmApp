package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.MovieName
import com.apptolast.familyfilmapp.model.local.User
import com.google.gson.annotations.SerializedName

data class UserRemote(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("seenMovies")
    val seenMovies: List<MovieNameRemote>? = null,

    @SerializedName("toSeeMovies")
    val toSeeMovies: List<MovieNameRemote>? = null,

    @SerializedName("joinedGroupsIds")
    val joinedGroupsIds: List<Int>? = null,

)

data class MovieNameRemote(

    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String? = null,

)

fun UserRemote.toDomain() = User(
    id = id ?: -1,
    email = email ?: "",
    language = language ?: "es",
    seenMovies = seenMovies?.map{it.toDomain()} ?: emptyList(),
    toSeeMovies = toSeeMovies?.map{it.toDomain()} ?: emptyList(),
    joinedGroupsIds = joinedGroupsIds ?: emptyList(),
)

fun MovieNameRemote.toDomain() = MovieName(
    id = id,
    title = title ?: "",
)
