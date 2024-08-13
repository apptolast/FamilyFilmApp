package com.apptolast.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class AddToWatchMovieRemote(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("owner_id")
    val ownerId: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("users")
    val users: List<UserResponseRemote>? = null,

    @SerializedName("to_Watch")
    val toWatch: List<MovieCatalogueRemote>? = null,

    @SerializedName("to_Watched")
    val toWatched: List<MovieCatalogueRemote>? = null,

    )
