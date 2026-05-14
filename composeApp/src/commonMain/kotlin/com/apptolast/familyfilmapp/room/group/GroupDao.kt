package com.apptolast.familyfilmapp.room.group

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apptolast.familyfilmapp.model.room.GroupTable
import com.apptolast.familyfilmapp.model.room.UserTable
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.GROUPS_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(group: GroupTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUserList(user: List<UserTable>)

    @Query("SELECT * FROM $GROUPS_TABLE_NAME")
    abstract fun getGroups(): Flow<List<GroupTable>>

    // Substring lookup over the JSON-encoded users column (Room KMP has no RawQuery).
    @Query("SELECT * FROM $GROUPS_TABLE_NAME WHERE users LIKE '%\"' || :userId || '\"%'")
    abstract fun getMyGroups(userId: String): Flow<List<GroupTable>>

    @Query("SELECT * FROM $GROUPS_TABLE_NAME WHERE groupId = :id")
    abstract fun getGroup(id: String): Flow<GroupTable?>

    @Update
    abstract suspend fun update(group: GroupTable)

    @Delete
    abstract suspend fun delete(group: GroupTable)

    @Query("DELETE FROM $GROUPS_TABLE_NAME")
    abstract suspend fun deleteAll()
}
