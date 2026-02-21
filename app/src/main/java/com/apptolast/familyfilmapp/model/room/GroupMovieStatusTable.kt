package com.apptolast.familyfilmapp.model.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.TypeConverters
import com.apptolast.familyfilmapp.model.local.GroupMovieStatus
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.GROUP_MOVIE_STATUS_TABLE_NAME
import com.apptolast.familyfilmapp.room.converters.MovieStatusConverter

@Entity(
    tableName = GROUP_MOVIE_STATUS_TABLE_NAME,
    primaryKeys = ["groupId", "userId", "movieId"],
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
    @TypeConverters(MovieStatusConverter::class)
    val status: MovieStatus,
)

fun GroupMovieStatusTable.toGroupMovieStatus() = GroupMovieStatus(
    groupId = groupId,
    userId = userId,
    movieId = movieId,
    status = status,
)

fun GroupMovieStatus.toGroupMovieStatusTable() = GroupMovieStatusTable(
    groupId = groupId,
    userId = userId,
    movieId = movieId,
    status = status,
)
