package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.google.gson.annotations.SerializedName

data class GroupRemote(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("owner_id")
    val ownerId: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("watched_list")
    val watchedList: List<MovieRemote>? = null,

    @SerializedName("to_watch_list")
    val toWatchList: List<MovieRemote>? = null,

    @SerializedName("recommended_movie")
    val recommendedMovie: MovieRemote? = null,

    @SerializedName("users")
    val users: List<UserRemote>? = null,
)

fun GroupRemote.toDomain() = Group(
    id = id ?: -1,
    ownerId = ownerId ?: -1,
    name = name ?: "",
    watchedList = watchedList?.map { it.toDomain() } ?: emptyList(),
    toWatchList = toWatchList?.map { it.toDomain() } ?: emptyList(),
    recommendedMovie = recommendedMovie?.toDomain() ?: Movie(),
    users = users?.map { it.toDomain() } ?: emptyList(),
)
