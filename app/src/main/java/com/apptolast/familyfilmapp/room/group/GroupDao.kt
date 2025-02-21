package com.apptolast.familyfilmapp.room.group

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
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

//    @Transaction
//    open suspend fun insertGroupWithUsers(group: Group) {
//        val users = group.users
//
//        insertUserList(users)
//        insert(group.toGroupTable())
//    }

    @Query("SELECT * from $GROUPS_TABLE_NAME")
    abstract fun getGroups(): Flow<List<GroupTable>>

    @RawQuery(observedEntities = [GroupTable::class])
    abstract fun getMyGroups(query: SupportSQLiteQuery): Flow<List<GroupTable>>

    fun getMyGroups(userId: String): Flow<List<GroupTable>> {
        val query = SupportSQLiteQueryBuilder.builder(GROUPS_TABLE_NAME)
            .columns(arrayOf("*"))
            .selection("users LIKE '%' || ? || '%'", arrayOf(userId))
            .create()
        return getMyGroups(query)
    }

    @Query("SELECT * from $GROUPS_TABLE_NAME WHERE groupId = :id")
    abstract fun getGroup(id: String): Flow<GroupTable>

    @Update
    abstract suspend fun update(group: GroupTable)

    @Delete
    abstract suspend fun delete(group: GroupTable)
}
