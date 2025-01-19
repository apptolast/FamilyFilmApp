package com.apptolast.familyfilmapp.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val isAdult: Boolean,
    val releaseDate: Date,
    val overview: String,
    val popularity: Float,
    val voteAverage: Float,
    val voteCount: Int,
    val genres: List<Genre>,
    val posterPath: String,
) : Parcelable {

    constructor() : this(title = "", posterPath = "")

    constructor(
        title: String,
        posterPath: String,
    ) : this(
        id = 0,
        title = title,
        isAdult = false,
        releaseDate = Date(),
        overview = "",
        popularity = 0f,
        voteAverage = 0f,
        voteCount = 0,
        genres = emptyList(),
        posterPath = posterPath,
    )
}
