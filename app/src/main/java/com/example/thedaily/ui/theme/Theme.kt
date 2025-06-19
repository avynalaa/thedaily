package com.example.thedaily.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun TheDailyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use our butterfly theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> ButterflyDarkColorScheme
        else -> ButterflyLightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

// Magical Purple Butterfly Light Theme
private val ButterflyLightColorScheme = lightColorScheme(
    primary = ButterflyColors.RoyalPurple,
    onPrimary = ButterflyColors.TextOnDark,
    primaryContainer = ButterflyColors.PalePurple,
    onPrimaryContainer = ButterflyColors.DeepPurple,
    
    secondary = ButterflyColors.FuchsiaWing,
    onSecondary = ButterflyColors.TextOnDark,
    secondaryContainer = ButterflyColors.PaleRose,
    onSecondaryContainer = ButterflyColors.MagentaWing,
    
    tertiary = ButterflyColors.CelestialBlue,
    onTertiary = ButterflyColors.TextOnDark,
    tertiaryContainer = ButterflyColors.PaleBlue,
    onTertiaryContainer = ButterflyColors.MysticBlue,
    
    error = ButterflyColors.ErrorWing,
    onError = ButterflyColors.TextOnDark,
    errorContainer = Color(0xFFFFEDEA),
    onErrorContainer = ButterflyColors.ErrorWing,
    
    background = ButterflyColors.LightBackground1,
    onBackground = ButterflyColors.TextPrimary,
    surface = ButterflyColors.LightSurface1,
    onSurface = ButterflyColors.TextPrimary,
    
    surfaceVariant = ButterflyColors.LightSurface2,
    onSurfaceVariant = ButterflyColors.TextSecondary,
    outline = ButterflyColors.SoftMist,
    outlineVariant = ButterflyColors.WhisperMist,
    
    scrim = androidx.compose.ui.graphics.Color.Black,
    inverseSurface = ButterflyColors.DarkSurface1,
    inverseOnSurface = ButterflyColors.TextOnDark,
    inversePrimary = ButterflyColors.VividPurple,
    
    surfaceDim = ButterflyColors.WhisperMist,
    surfaceBright = ButterflyColors.LightBackground1,
    surfaceContainerLowest = ButterflyColors.LightBackground1,
    surfaceContainerLow = ButterflyColors.LightBackground2,
    surfaceContainer = ButterflyColors.LightSurface1,
    surfaceContainerHigh = ButterflyColors.LightSurface2,
    surfaceContainerHighest = ButterflyColors.PalePurple
)

// Magical Purple Butterfly Dark Theme
private val ButterflyDarkColorScheme = darkColorScheme(
    primary = ButterflyColors.VividPurple,
    onPrimary = ButterflyColors.DarkBackground1,
    primaryContainer = ButterflyColors.DeepPurple,
    onPrimaryContainer = ButterflyColors.SoftPurple,
    
    secondary = ButterflyColors.RoseWing,
    onSecondary = ButterflyColors.DarkBackground1,
    secondaryContainer = ButterflyColors.MagentaWing,
    onSecondaryContainer = ButterflyColors.SoftRose,
    
    tertiary = ButterflyColors.SkyBlue,
    onTertiary = ButterflyColors.DarkBackground1,
    tertiaryContainer = ButterflyColors.MysticBlue,
    onTertiaryContainer = ButterflyColors.SoftBlue,
    
    error = ButterflyColors.ErrorWing,
    onError = ButterflyColors.DarkBackground1,
    errorContainer = Color(0xFF470A0A),
    onErrorContainer = ButterflyColors.ErrorWing,
    
    background = ButterflyColors.DarkBackground1,
    onBackground = ButterflyColors.TextOnDark,
    surface = ButterflyColors.DarkSurface1,
    onSurface = ButterflyColors.TextOnDark,
    
    surfaceVariant = ButterflyColors.DarkSurface2,
    onSurfaceVariant = ButterflyColors.TextOnDarkSecondary,
    outline = ButterflyColors.MistWing,
    outlineVariant = ButterflyColors.ShadowWing,
    
    scrim = androidx.compose.ui.graphics.Color.Black,
    inverseSurface = ButterflyColors.LightSurface1,
    inverseOnSurface = ButterflyColors.TextPrimary,
    inversePrimary = ButterflyColors.RoyalPurple,
    
    surfaceDim = ButterflyColors.DarkBackground1,
    surfaceBright = ButterflyColors.ShadowWing,
    surfaceContainerLowest = androidx.compose.ui.graphics.Color.Black,
    surfaceContainerLow = ButterflyColors.DarkBackground2,
    surfaceContainer = ButterflyColors.DarkSurface1,
    surfaceContainerHigh = ButterflyColors.DarkSurface2,
    surfaceContainerHighest = ButterflyColors.TwilightWing
)

// These should be defined in your Type.kt and Shapes.kt or similar files
private val AppShapes: Shapes = Shapes()