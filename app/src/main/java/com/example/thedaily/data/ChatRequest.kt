package com.example.thedaily.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis() // Add timestamp
)

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<Message>
)