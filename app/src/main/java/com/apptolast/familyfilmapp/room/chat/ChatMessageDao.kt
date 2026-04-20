package com.apptolast.familyfilmapp.room.chat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apptolast.familyfilmapp.model.room.ChatMessageTable
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.CHAT_MESSAGES_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ChatMessageTable)

    /**
     * Returns the chat history for a user ordered chronologically (oldest first)
     * so the UI can render it directly without reversing.
     */
    @Query(
        "SELECT * FROM $CHAT_MESSAGES_TABLE_NAME WHERE userId = :userId ORDER BY timestamp ASC",
    )
    fun observeByUserId(userId: String): Flow<List<ChatMessageTable>>

    @Query("DELETE FROM $CHAT_MESSAGES_TABLE_NAME WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("DELETE FROM $CHAT_MESSAGES_TABLE_NAME")
    suspend fun deleteAll()
}
