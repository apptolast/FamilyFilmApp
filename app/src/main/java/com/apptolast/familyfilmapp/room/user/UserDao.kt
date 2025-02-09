package com.apptolast.familyfilmapp.room.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apptolast.familyfilmapp.model.room.UserTable
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.USERS_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserTable)

    @Delete
    suspend fun delete(user: UserTable)

    @Query("SELECT * from $USERS_TABLE_NAME WHERE userId = :id")
    fun getUser(id: String): Flow<UserTable>

    @Query("SELECT * from $USERS_TABLE_NAME WHERE email = :email")
    fun getUserByEmail(email: String): Flow<UserTable>

    @Query("SELECT * from $USERS_TABLE_NAME")
    fun getUsers(): Flow<List<UserTable>>

    @Update
    suspend fun update(user: UserTable)

}
