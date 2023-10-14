package com.digitalsolution.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class MovieInfoRemote(

    @SerializedName("movie")
    val movieRemote: MovieRemote? = null,

    @SerializedName("group_id")
    val groupId: Int? = null,

    @SerializedName("movie_id")
    val movieId: Int? = null

)