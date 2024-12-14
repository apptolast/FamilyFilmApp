package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.MovieSelectedByUsers
import com.google.gson.annotations.SerializedName

data class GroupRemote(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("ownerId")
    val ownerId: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("users")
    val users: List<UserRemote>? = null,

    @SerializedName("watched")
    val watched: List<MovieSelectedByUsersRemote>? = null,

    @SerializedName("toWatch")
    val toWatch: List<MovieSelectedByUsersRemote>? = null,
)

fun GroupRemote.toDomain() = Group(
    id = id ?: -1,
    ownerId = ownerId ?: -1,
    name = name ?: "",
    users = users?.map { it.toDomain() } ?: emptyList(),
    watched = watched?.map { it.toDomain() } ?: emptyList(),
    toWatch = toWatch?.map { it.toDomain() } ?: emptyList(),
)

data class  MovieSelectedByUsersRemote(

    @SerializedName("usersId")
    val usersId: List<Int>? = null,

    @SerializedName("movieId")
    val movieId: Int? = null,
)

fun MovieSelectedByUsersRemote.toDomain() = MovieSelectedByUsers(
    usersId = usersId ?: emptyList(),
    movieId = movieId ?: -1,
)
