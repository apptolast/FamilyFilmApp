package com.apptolast.familyfilmapp.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Calendar
import java.util.Date

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

val FAKE_MOVIES_WATCHLIST = listOf(
    Movie(1, "Movie Title 1", false, listOf(1 to "Comedy"), "image1.jpg", "Synopsis 1", 7.5f, 100, Calendar.getInstance().time, "en"),
    Movie(2, "Movie Title 2", false, listOf(2 to "Action"), "image2.jpg", "Synopsis 2", 8.0f, 200, Calendar.getInstance().time, "en"),
    Movie(3, "Movie Title 3", true, listOf(3 to "Horror"), "image3.jpg", "Synopsis 3", 6.0f, 150, Calendar.getInstance().time, "en"),
    Movie(4, "Movie Title 4", false, listOf(4 to "Sci-Fi"), "image4.jpg", "Synopsis 4", 9.0f, 250, Calendar.getInstance().time, "en"),
    Movie(5, "Movie Title 5", true, listOf(5 to "Thriller"), "image5.jpg", "Synopsis 5", 5.5f, 120, Calendar.getInstance().time, "en")
)

val FAKE_MOVIES_VIEWLIST = listOf(
    Movie(6, "Movie Title 6", false, listOf(1 to "Drama"), "image6.jpg", "Synopsis 6", 7.0f, 180, Calendar.getInstance().time, "en"),
    Movie(7, "Movie Title 7", false, listOf(2 to "Fantasy"), "image7.jpg", "Synopsis 7", 8.5f, 210, Calendar.getInstance().time, "en"),
    Movie(8, "Movie Title 8", true, listOf(3 to "Mystery"), "image8.jpg", "Synopsis 8", 6.5f, 160, Calendar.getInstance().time, "en"),
    Movie(9, "Movie Title 9", false, listOf(4 to "Animation"), "image9.jpg", "Synopsis 9", 9.5f, 290, Calendar.getInstance().time, "en"),
    Movie(10, "Movie Title 10", true, listOf(5 to "Documentary"), "image10.jpg", "Synopsis 10", 4.5f, 110, Calendar.getInstance().time, "en")
)
