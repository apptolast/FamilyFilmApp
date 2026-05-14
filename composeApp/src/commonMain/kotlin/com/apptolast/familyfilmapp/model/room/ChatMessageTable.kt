package com.apptolast.familyfilmapp.model.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.room.AppDatabase.Companion.CHAT_MESSAGES_TABLE_NAME

// `role` is stored as a plain String to avoid adding a new TypeConverter.
@Entity(
    tableName = CHAT_MESSAGES_TABLE_NAME,
    indices = [
        Index(value = ["userId"]),
        Index(value = ["userId", "timestamp"]),
    ],
)
data class ChatMessageTable(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val userId: String,
    val role: String,
    val content: String,
    val timestamp: Long,
)

fun ChatMessageTable.toDomain(): ChatMessage = ChatMessage(
    id = id,
    role = ChatMessage.Role.valueOf(role),
    content = content,
    timestamp = timestamp,
)

fun ChatMessage.toTable(userId: String): ChatMessageTable = ChatMessageTable(
    id = id,
    userId = userId,
    role = role.name,
    content = content,
    timestamp = timestamp,
)
