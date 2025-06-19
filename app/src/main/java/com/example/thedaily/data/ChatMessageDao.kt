package com.example.thedaily.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE characterId = :characterId ORDER BY timestamp DESC")
    fun getMessagesForCharacter(characterId: Long): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE characterId = :characterId ORDER BY timestamp DESC")
    suspend fun getMessagesForCharacterOnce(characterId: Long): List<ChatMessage>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE id = :id")
    suspend fun getMessageById(id: Long): ChatMessage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatMessage: ChatMessage): Long

    @Update
    suspend fun update(chatMessage: ChatMessage)

    @Delete
    suspend fun delete(chatMessage: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM chat_messages WHERE characterId = :characterId")
    suspend fun deleteAllForCharacter(characterId: Long)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAll()

    @Query("SELECT * FROM chat_messages WHERE characterId = :characterId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastMessageForCharacter(characterId: Long): ChatMessage?

    @Query("SELECT COUNT(*) FROM chat_messages WHERE characterId = :characterId AND isFromUser = 0 AND status != 'READ'")
    suspend fun getUnreadCount(characterId: Long): Int

    @Query("UPDATE chat_messages SET status = 'READ' WHERE characterId = :characterId AND isFromUser = 0")
    suspend fun markAllAsRead(characterId: Long)

    // Optimized query for recent chats
    @Query("""
        SELECT cm.*, cp.name as characterName, cp.avatarUri as characterAvatar 
        FROM chat_messages cm 
        INNER JOIN character_profiles cp ON cm.characterId = cp.id 
        WHERE cm.id IN (
            SELECT MAX(id) FROM chat_messages GROUP BY characterId
        ) 
        ORDER BY cm.timestamp DESC
    """)
    suspend fun getRecentChatsOptimized(): List<ChatMessageWithCharacter>

    @Query("SELECT * FROM chat_messages WHERE status = 'ERROR' AND isFromUser = 1")
    suspend fun getFailedMessages(): List<ChatMessage>
}

data class ChatMessageWithCharacter(
    @Embedded val message: ChatMessage,
    val characterName: String,
    val characterAvatar: String?
)
