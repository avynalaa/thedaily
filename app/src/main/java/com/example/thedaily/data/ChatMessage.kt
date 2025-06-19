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
    RECEIVED,
    READ,
    ERROR
}

enum class DeleteType {
    NONE, // Not deleted
    USER_ONLY, // User deleted for themselves, character still "sees" it
    FOR_EVERYONE, // Deleted for both, character sees deletion
    CHARACTER_DELETED, // Character deleted their own message
    UNDONE // Withdrawn, app pretends it never existed
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
    val errorMessage: String? = null,
    val edited: Boolean = false,
    val editTimestamp: Long? = null,
    val deleteType: DeleteType = DeleteType.NONE,
    val deleteTimestamp: Long? = null
) {
    // Helper method to check if message is visible to user
    fun isVisible(): Boolean {
        return deleteType != DeleteType.FOR_EVERYONE && 
               deleteType != DeleteType.USER_ONLY
    }
    
    // Helper method to get display text
    fun getDisplayText(): String {
        return when {
            deleteType == DeleteType.FOR_EVERYONE -> "This message was deleted"
            deleteType == DeleteType.USER_ONLY -> "You deleted this message"
            else -> text
        }
    }
}