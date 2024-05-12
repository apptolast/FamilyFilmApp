package com.apptolast.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class GenreRemote(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,
)
