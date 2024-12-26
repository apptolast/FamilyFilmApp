package com.apptolast.familyfilmapp.model.remote.request

import com.google.gson.annotations.SerializedName

data class GetMoviesByIdBody(
    @SerializedName("movieIds") val movieIds: List<Int>,
)
