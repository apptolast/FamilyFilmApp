package com.apptolast.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class MovieInfoRemote(

    @SerializedName("group_id")
    val groupId: Int? = null,

    @SerializedName("movie_id")
    val movieId: Int? = null,

    @SerializedName("movie")
    val movieRemote: MovieRemote? = null,

)
