package com.example.thedaily.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import kotlinx.serialization.Serializable

// Placed here to be accessible within the package
enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    ERROR
}

@Serializable
@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = CharacterProfile::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["characterId"])]
)
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val characterId: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val text: String,
    val isFromUser: Boolean,
    val status: MessageStatus = MessageStatus.SENT,
    val errorMessage: String? = null
)