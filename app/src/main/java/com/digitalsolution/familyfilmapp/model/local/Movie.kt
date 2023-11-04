package com.digitalsolution.familyfilmapp.model.local

import java.util.Calendar
import java.util.Date

data class Movie(
    val title: String,
    val isAdult: Boolean,
    val genres: List<Pair<Int, String>>,
    val image: String,
    val synopsis: String,
    val voteAverage: Float,
    val voteCount: Int,
    val releaseDate: Date,
    val language: String,
) {
    constructor(image: String, title: String) : this(
        title = title,
        isAdult = true,
        genres = emptyList<Pair<Int, String>>(),
        image = image,
        synopsis = "",
        voteAverage = 0f,
        voteCount = 0,
        releaseDate = Calendar.getInstance().time,
        language = "",
    )

    constructor() : this("", "")
}
