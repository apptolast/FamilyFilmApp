package com.digitalsolution.familyfilmapp.model.local

import java.util.Calendar
import java.util.Date

data class Movie(
    val title: String,
    val isAdult: Boolean,
    val genres: List<Genre>,
    val image: String, // posterpath
    val synopsis: String, // description
    val voteAverage: Float,
    val voteCount: Int,
    val releaseDate: Date
) {
    constructor(image: String, title: String) : this(
        title = title,
        isAdult = false,
        genres = emptyList(),
        image = image,
        synopsis = "Very useful info",
        voteAverage = 0.0f,
        voteCount = 0,
        releaseDate = Calendar.getInstance().time,
    )
}

data class Genre(
    val id: Int,
    val name: String
)

