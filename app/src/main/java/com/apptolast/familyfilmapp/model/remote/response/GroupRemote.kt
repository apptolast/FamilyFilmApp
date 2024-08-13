package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.Group
import com.google.gson.annotations.SerializedName

data class GroupRemote(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("owner_id")
    val ownerId: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("watched")
    val watchedList: List<MovieCatalogueRemote>? = null,

    @SerializedName("watch")
    val toWatchList: List<MovieCatalogueRemote>? = null,

    @SerializedName("users")
    val users: List<UserResponseRemote>? = null,
)

fun GroupRemote.toDomain() = Group(
    id = id ?: -1,
    ownerId = ownerId ?: -1,
    name = name ?: "",
    watchedList = watchedList?.map { it.toDomain() } ?: emptyList(),
    toWatchList = toWatchList?.map { it.toDomain() } ?: emptyList(),
    users = users?.map { it.toDomain() } ?: emptyList(),
)
