package com.apptolast.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class AddGroupRemote(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

)
