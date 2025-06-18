package com.example.thedaily.data

data class ReplyConfig(
    val baseDelay: Long = 30_000, // 30 seconds base
    val onlineHours: Pair<Int, Int> = Pair(9, 21), // 9AM-9PM
    val variance: Double = 0.3 // ±30% randomness
)
