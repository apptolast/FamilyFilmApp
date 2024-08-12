package com.apptolast.familyfilmapp.model.remote.request

import com.google.gson.annotations.SerializedName

data class SearchMovieByNameBody(@SerializedName("name") val name: String)
