package com.apptolast.familyfilmapp.room.skippedmedia

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apptolast.familyfilmapp.model.local.MediaKey
import com.apptolast.familyfilmapp.model.room.SkippedMediaTable
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.SKIPPED_MEDIA_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface SkippedMediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SkippedMediaTable)

    @Query(
        "SELECT * FROM $SKIPPED_MEDIA_TABLE_NAME WHERE userId = :userId ORDER BY skippedAt DESC",
    )
    fun observeByUser(userId: String): Flow<List<SkippedMediaTable>>

    @Query(
        "SELECT mediaId AS mediaId, mediaType AS mediaType FROM $SKIPPED_MEDIA_TABLE_NAME WHERE userId = :userId",
    )
    suspend fun getKeysByUser(userId: String): List<MediaKey>

    @Query(
        "SELECT * FROM $SKIPPED_MEDIA_TABLE_NAME WHERE userId = :userId AND mediaId = :mediaId AND mediaType = :mediaType LIMIT 1",
    )
    suspend fun getByKey(userId: String, mediaId: Int, mediaType: String): SkippedMediaTable?

    @Query(
        "DELETE FROM $SKIPPED_MEDIA_TABLE_NAME WHERE userId = :userId AND mediaId = :mediaId AND mediaType = :mediaType",
    )
    suspend fun deleteByKey(userId: String, mediaId: Int, mediaType: String)

    @Query("DELETE FROM $SKIPPED_MEDIA_TABLE_NAME")
    suspend fun deleteAll()
}
