package com.apptolast.familyfilmapp.model.room

import androidx.room.Entity
import androidx.room.Index
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.types.MediaType
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.SKIPPED_MEDIA_TABLE_NAME

@Entity(
    tableName = SKIPPED_MEDIA_TABLE_NAME,
    primaryKeys = ["userId", "mediaId", "mediaType"],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["userId", "skippedAt"]),
    ],
)
data class SkippedMediaTable(
    val userId: String,
    val mediaId: Int,
    val mediaType: String,
    val title: String,
    val posterPath: String,
    val overview: String,
    val releaseDate: String,
    val voteAverage: Float,
    val popularity: Float,
    val skippedAt: Long,
)

fun SkippedMediaTable.toMedia(): Media = Media(
    id = mediaId,
    title = title,
    adult = false,
    popularity = popularity,
    voteAverage = voteAverage,
    streamProviders = emptyList(),
    buyProviders = emptyList(),
    rentProviders = emptyList(),
    releaseDate = releaseDate,
    overview = overview,
    posterPath = posterPath,
    mediaType = MediaType.valueOf(mediaType),
)

fun Media.toSkippedMediaTable(userId: String, skippedAt: Long): SkippedMediaTable = SkippedMediaTable(
    userId = userId,
    mediaId = id,
    mediaType = mediaType.name,
    title = title,
    posterPath = posterPath,
    overview = overview,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    popularity = popularity,
    skippedAt = skippedAt,
)
