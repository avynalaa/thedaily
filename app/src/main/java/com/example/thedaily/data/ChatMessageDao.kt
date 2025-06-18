package com.example.thedaily.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatMessage: ChatMessage): Long

    @Query("SELECT * FROM chat_messages WHERE characterId = :characterId ORDER BY timestamp ASC")
    fun getMessagesForCharacter(characterId: Long): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE characterId = :characterId ORDER BY timestamp ASC")
    suspend fun getMessagesForCharacterOnce(characterId: Long): List<ChatMessage>

    @Query("SELECT * FROM chat_messages WHERE characterId = :characterId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestMessageForCharacter(characterId: Long): ChatMessage?

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessage>>
}
