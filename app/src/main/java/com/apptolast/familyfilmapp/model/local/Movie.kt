package com.apptolast.familyfilmapp.model.local

import android.os.Parcelable
import java.util.Calendar
import java.util.Date
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val isAdult: Boolean,
    val genres: List<Pair<Int, String>>,
    val image: String,
    val synopsis: String,
    val voteAverage: Float,
    val voteCount: Int,
    val releaseDate: Date,
    val language: String,
) : Parcelable {
    constructor(image: String, title: String) : this(
        id = -1,
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
