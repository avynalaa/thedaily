package com.example.thedaily.ui.theme

import androidx.compose.ui.graphics.Color

// New color palette based on the design
val DarkBackground = Color(0xFF121212)
val LightText = Color(0xFFE0E0E0)
val AccentGreen = Color(0xFF00BFA5)
val GrayText = Color(0xFF9E9E9E)
val SentMessageRed = Color(0xFFD32F2F)
val ReceivedMessageWhite = Color(0xFFFFFFFF)
val ActiveTabGray = Color(0xFF424242)
val FabRed = Color(0xFFE53935)
val UserAvatarBg = Color(0xFF00897B)

// Messenger-like color palette (keeping for reference, but not used in the new design)
val MessengerBackgroundStart = Color(0xFFF5F7FA)
val MessengerBackgroundEnd   = Color(0xFFE5E9EF)
val MessengerBubbleUserStart = Color(0xFF3772FF)
val MessengerBubbleUserEnd   = Color(0xFF549EFF)
val MessengerBubbleOther     = Color(0xFFFFFFFF)
val MessengerInputBackground = Color(0xFFFFFFFF)
val MessengerPrimary         = Color(0xFF3772FF)
val MessengerOnPrimary       = Color(0xFFFFFFFF)

// Magical Purple Butterfly Color System 🦋
object ButterflyColors {
    // Primary Butterfly Wing - Deep Purples (main theme)
    val DeepPurple = Color(0xFF4C1D95)           // Rich royal purple
    val RoyalPurple = Color(0xFF6D28D9)          // Vibrant purple
    val VividPurple = Color(0xFF8B5CF6)          // Bright purple  
    val SoftPurple = Color(0xFFA78BFA)           // Light purple
    val PalePurple = Color(0xFFDDD6FE)           // Very pale purple
    val WhisperPurple = Color(0xFFF5F3FF)        // Almost white purple
    
    // Secondary Butterfly Wing - Magical Pink-Purples
    val MagentaWing = Color(0xFF86198F)          // Deep magenta
    val FuchsiaWing = Color(0xFFBE185D)          // Rich fuchsia
    val RoseWing = Color(0xFFEC4899)             // Bright pink
    val SoftRose = Color(0xFFF9A8D4)             // Light pink
    val PaleRose = Color(0xFFFCE7F3)             // Very pale pink
    
    // Accent Wing - Mystical Blues (complement to purple)
    val MysticBlue = Color(0xFF1E40AF)           // Deep blue
    val CelestialBlue = Color(0xFF3B82F6)        // Bright blue
    val SkyBlue = Color(0xFF60A5FA)              // Light blue
    val SoftBlue = Color(0xFF93C5FD)             // Very light blue
    val PaleBlue = Color(0xFFDBEAFE)             // Almost white blue
    
    // Magical Neutrals - Purple-tinted grays
    val DarkWing = Color(0xFF1F1B2E)             // Very dark purple-gray
    val TwilightWing = Color(0xFF2D2438)         // Dark purple-gray
    val ShadowWing = Color(0xFF483D54)           // Medium purple-gray
    val MistWing = Color(0xFF6B5B73)             // Light purple-gray
    val SoftMist = Color(0xFF9B91A1)             // Very light purple-gray
    val WhisperMist = Color(0xFFE2DFE7)          // Almost white purple-gray
    
    // Background Gradients - Ethereal and magical
    val LightBackground1 = Color(0xFFFBFAFF)     // Pure ethereal white
    val LightBackground2 = Color(0xFFF8F6FF)     // Slight purple tint
    val LightSurface1 = Color(0xFFF3F0FF)        // Light purple surface
    val LightSurface2 = Color(0xFFEDE9FE)        // Medium purple surface
    
    val DarkBackground1 = Color(0xFF0F0A1A)      // Deep magical night
    val DarkBackground2 = Color(0xFF1A0F2E)      // Dark purple night
    val DarkSurface1 = Color(0xFF261738)        // Dark purple surface
    val DarkSurface2 = Color(0xFF332042)        // Medium dark purple surface
    
    // Special Effects Colors
    val GoldenShimmer = Color(0xFFFFD700)        // Golden butterfly dust
    val SilverShimmer = Color(0xFFC0C0C0)        // Silver butterfly dust
    val MagicalGlow = Color(0xFFE879F9)          // Magical glow effect
    
    // Status Colors (butterfly-themed)
    val SuccessWing = Color(0xFF10B981)          // Emerald green
    val WarningWing = Color(0xFFF59E0B)          // Amber
    val ErrorWing = Color(0xFFEF4444)            // Soft red
    val InfoWing = Color(0xFF3B82F6)             // Blue
    
    // Text Colors for perfect readability
    val TextPrimary = Color(0xFF1F1B2E)          // Very dark purple
    val TextSecondary = Color(0xFF4C1D95)        // Deep purple
    val TextTertiary = Color(0xFF6B5B73)         // Medium purple-gray
    val TextOnDark = Color(0xFFFBFAFF)           // Pure white with purple hint
    val TextOnDarkSecondary = Color(0xFFE2DFE7)  // Light purple-gray
}
