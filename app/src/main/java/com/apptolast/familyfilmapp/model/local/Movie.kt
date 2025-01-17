package com.apptolast.familyfilmapp.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: Int,
    val tmdbId: Int,
    val title: String,
    val originalTitle: String,
    val adult: Boolean,
    val releaseDate: String,
    val originalLanguage: String,
    val overview: String,
    val popularity: Float,
    val posterPath: String,
    val voteAverage: Float,
    val voteCount: Int,
    val backdropPath: String,
    val genresIds: List<Int>,
    val video: Boolean,
) : Parcelable {
    constructor(title: String) : this(
        id = -1,
        tmdbId = -1,
        title = title,
        originalTitle = "",
        adult = false,
        releaseDate = "",
        originalLanguage = "",
        overview = "",
        popularity = 0f,
        posterPath = "",
        voteAverage = 0f,
        voteCount = 0,
        backdropPath = "",
        genresIds = emptyList(),
        video = false,
    )

    constructor() : this("")
}
