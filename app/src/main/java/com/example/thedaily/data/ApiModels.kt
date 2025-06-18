package com.example.thedaily.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ChatResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message
)