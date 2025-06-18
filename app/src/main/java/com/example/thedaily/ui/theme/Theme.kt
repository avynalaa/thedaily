package com.example.thedaily.ui.theme
import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun TheDailyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

// You may need to adjust these references to match your actual color/typography/shape objects.
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3772FF), // Vibrant blue
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF00BFA5), // Accent green
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF5F7FA), // Light gray
    onBackground = Color(0xFF23272F), // Deep gray/blue
    surface = Color(0xFFFFFFFF), // Card/Surface
    onSurface = Color(0xFF23272F),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3772FF),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF00BFA5),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF23272F),
    onSurface = Color(0xFFE0E0E0),
)

// These should be defined in your Type.kt and Shapes.kt or similar files
private val AppShapes: Shapes = Shapes()