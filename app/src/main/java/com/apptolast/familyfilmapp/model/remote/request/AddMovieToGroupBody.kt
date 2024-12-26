package com.apptolast.familyfilmapp.model.remote.request

import com.google.gson.annotations.SerializedName

data class AddMovieToGroupBody(
    @SerializedName("movieId") val movieId: Int,
    @SerializedName("groupId") val groupId: Int,
    @SerializedName("toWatch") val toWatch: Boolean,
    @SerializedName("addMovie") val addMovie: Boolean,
)
