package com.example.thedaily.data

import com.example.thedaily.data.ReplyConfig
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

enum class RelationshipContext {
    STRANGERS,
    FRIENDS,
    BEST_FRIENDS,
    FAMILY,
    COWORKERS,
    ENEMIES,
    RIVALS,
    LOVE_INTEREST,
    PARTNER,
    EX
}

@Serializable
@Entity(tableName = "character_profiles")
data class CharacterProfile(
    @PrimaryKey val id: Long = System.currentTimeMillis(),
    val name: String,
    val phoneNumber: String = "",
    val systemPrompt: String,
    val personalityTags: List<String> = emptyList(),
    val interestTags: List<String> = emptyList(),
    val dealbreakerTags: List<String> = emptyList(),
    val affectionLevel: Double = 50.0,
    val mood: String = "Neutral",
    @Contextual val replyConfig: ReplyConfig = ReplyConfig(),
    val avatarUri: String? = null,
    val isCurrent: Boolean = false,
    val isOnline: Boolean = false,
    val relationshipContext: RelationshipContext = RelationshipContext.STRANGERS,
    val relationshipHistory: String = ""
)
