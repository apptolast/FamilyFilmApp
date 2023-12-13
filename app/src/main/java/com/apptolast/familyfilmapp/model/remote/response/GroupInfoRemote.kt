package com.apptolast.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class GroupInfoRemote(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("user_id")
    val groupCreatorId: Int? = null,

    @SerializedName("watchList")
    val watchList: List<MovieInfoRemote>? = null,

    @SerializedName("viewList")
    val viewList: List<MovieInfoRemote>? = null,

    @SerializedName("users")
    val users: List<UsersRemote>? = null,

)
