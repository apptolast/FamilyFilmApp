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
//    val popularity: Float,
//    val voteAverage: Float,
//    val voteCount: Int,
//    val genres: List<Genre>,
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
//        popularity = 0f,
//        voteAverage = 0f,
//        voteCount = 0,
//        genres = emptyList(),
        posterPath = posterPath,
    )
}


fun TmdbMovieRemote.toDomain(): Movie = Movie(
    id = id,
    title = title ?: "",
    adult = adult,
    releaseDate = releaseDate ?: "",
    overview = overview ?: "",
//    popularity = popularity,
//    voteAverage = voteAverage,
//    voteCount = voteCount,
//    genres = genres.map { it.toDomain() },
    posterPath = posterPath ?: "",
)
