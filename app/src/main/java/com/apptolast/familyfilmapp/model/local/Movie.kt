package com.apptolast.familyfilmapp.model.local

import android.os.Parcelable
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieRemote
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val adult: Boolean,
    val releaseDate: String,
    val overview: String,
    val posterPath: String,
) : Parcelable {

    constructor() : this(title = "", posterPath = "")

    constructor(
        title: String,
        posterPath: String,
    ) : this(
        id = 0,
        title = title,
        adult = false,
        releaseDate = "",
        overview = "",
        posterPath = posterPath,
    )
}

fun TmdbMovieRemote.toDomain(): Movie = Movie(
    id = id,
    title = title ?: "",
    adult = adult,
    releaseDate = releaseDate ?: "",
    overview = overview ?: "",
    posterPath = posterPath ?: "",
)
