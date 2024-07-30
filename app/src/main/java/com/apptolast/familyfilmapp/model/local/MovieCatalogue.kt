package com.apptolast.familyfilmapp.model.local

import java.util.Calendar
import java.util.Date

data class MovieCatalogue(
    val id: Int,
    val title: String,
    val synopsis: String,
    val image: String,
    val adult: Boolean,
    val releaseDate: Date,
    val voteAverage: Float,
    val ratingValue: Float,
    val genres: List<String>,
) {
    constructor() : this(
        id = -1,
        title = "",
        synopsis = "",
        image = "",
        adult = false,
        releaseDate = Calendar.getInstance().time,
        voteAverage = 0.0f,
        ratingValue = 0.0f,
        genres = emptyList(),
    )
}
