package com.example.thedaily.data

import com.example.thedaily.data.ReplyConfig
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName = "character_profiles")
data class CharacterProfile(
    @PrimaryKey val id: Long = System.currentTimeMillis(),
    val name: String,
    val phoneNumber: String = "",
    val systemPrompt: String,
    val personalityTags: List<String> = emptyList(),
    val preferenceTags: List<String> = emptyList(),
    val dealbreakerTags: List<String> = emptyList(),
    val affectionLevel: Double = 50.0,
    val mood: String = "Neutral",
    @Contextual val replyConfig: ReplyConfig = ReplyConfig(),
    val avatarUri: String? = null,
    val isCurrent: Boolean = false,
    val isOnline: Boolean = false
)
