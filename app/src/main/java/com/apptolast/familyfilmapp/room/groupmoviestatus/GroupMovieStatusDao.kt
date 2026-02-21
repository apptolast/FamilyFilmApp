package com.apptolast.familyfilmapp.room.groupmoviestatus

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apptolast.familyfilmapp.model.room.GroupMovieStatusTable
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.GROUP_MOVIE_STATUS_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupMovieStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: GroupMovieStatusTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<GroupMovieStatusTable>)

    @Query(
        "DELETE FROM $GROUP_MOVIE_STATUS_TABLE_NAME WHERE groupId = :groupId AND userId = :userId AND movieId = :movieId",
    )
    suspend fun delete(groupId: String, userId: String, movieId: Int)

    @Query("SELECT * FROM $GROUP_MOVIE_STATUS_TABLE_NAME WHERE groupId = :groupId")
    fun getStatusesByGroup(groupId: String): Flow<List<GroupMovieStatusTable>>

    @Query("SELECT * FROM $GROUP_MOVIE_STATUS_TABLE_NAME WHERE groupId = :groupId AND userId = :userId")
    fun getStatusesByGroupAndUser(groupId: String, userId: String): Flow<List<GroupMovieStatusTable>>

    @Query("SELECT * FROM $GROUP_MOVIE_STATUS_TABLE_NAME WHERE userId = :userId")
    fun getStatusesByUser(userId: String): Flow<List<GroupMovieStatusTable>>

    @Query("SELECT DISTINCT movieId FROM $GROUP_MOVIE_STATUS_TABLE_NAME WHERE userId = :userId")
    suspend fun getAllMovieIdsForUser(userId: String): List<Int>

    @Query("DELETE FROM $GROUP_MOVIE_STATUS_TABLE_NAME WHERE groupId = :groupId")
    suspend fun deleteByGroup(groupId: String)

    @Query("DELETE FROM $GROUP_MOVIE_STATUS_TABLE_NAME")
    suspend fun deleteAll()
}
