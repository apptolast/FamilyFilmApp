package com.apptolast.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class GroupRemote(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("owner_id")
    val ownerId: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("users")
    val users: List<UserResponseRemote>? = null,

    @SerializedName("watch")
    val toWatchList: List<MovieCatalogueRemote>? = null,

    @SerializedName("watched")
    val watchedList: List<MovieCatalogueRemote>? = null,
)
