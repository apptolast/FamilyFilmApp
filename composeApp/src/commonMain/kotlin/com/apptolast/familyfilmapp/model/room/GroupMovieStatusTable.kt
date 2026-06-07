package com.apptolast.familyfilmapp.model.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.TypeConverters
import com.apptolast.familyfilmapp.model.local.GroupMediaStatus
import com.apptolast.familyfilmapp.model.local.types.MediaStatus
import com.apptolast.familyfilmapp.model.local.types.MediaType
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.GROUP_MOVIE_STATUS_TABLE_NAME
import com.apptolast.familyfilmapp.room.converters.MediaStatusConverter
import com.apptolast.familyfilmapp.room.converters.MediaTypeConverter

@Entity(
    tableName = GROUP_MOVIE_STATUS_TABLE_NAME,
    primaryKeys = ["groupId", "userId", "movieId", "mediaType"],
    indices = [
        Index(value = ["groupId"]),
        Index(value = ["userId"]),
        Index(value = ["groupId", "userId"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = GroupTable::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class GroupMovieStatusTable(
    val groupId: String,
    val userId: String,
    val movieId: Int,
    @TypeConverters(MediaStatusConverter::class)
    val status: MediaStatus,
    @TypeConverters(MediaTypeConverter::class)
    val mediaType: String = MediaType.MOVIE.name,
)

fun GroupMovieStatusTable.toGroupMediaStatus() = GroupMediaStatus(
    groupId = groupId,
    userId = userId,
    mediaId = movieId,
    status = status,
    mediaType = MediaType.valueOf(mediaType),
)

fun GroupMediaStatus.toGroupMediaStatusTable() = GroupMovieStatusTable(
    groupId = groupId,
    userId = userId,
    movieId = mediaId,
    status = status,
    mediaType = mediaType.name,
)
