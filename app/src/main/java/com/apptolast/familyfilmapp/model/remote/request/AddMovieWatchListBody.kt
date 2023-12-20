package com.apptolast.familyfilmapp.model.remote.request

import com.google.gson.annotations.SerializedName

data class AddMovieWatchListBody(
    @SerializedName("movieId") val movieId: Int,
)
