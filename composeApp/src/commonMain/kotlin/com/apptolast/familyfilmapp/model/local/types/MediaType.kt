package com.apptolast.familyfilmapp.model.local.types

enum class MediaType {
    MOVIE,
    TV_SHOW,
    ;

    companion object {
        fun fromTmdbString(value: String?): MediaType = when (value) {
            "tv" -> TV_SHOW
            else -> MOVIE
        }
    }
}
