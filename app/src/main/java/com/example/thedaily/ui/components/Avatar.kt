package com.example.thedaily.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlin.math.abs

@Composable
fun Avatar(
    name: String,
    avatarUri: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    val initials = name.split(' ')
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")

    val backgroundColor = rememberDominantColor(name)
    var imageLoadFailed by remember { mutableStateOf(false) }
    var imageLoaded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        // Show initials if no avatar URI or if image failed to load
        if (avatarUri.isNullOrBlank() || imageLoadFailed || !imageLoaded) {
            Text(
                text = initials,
                color = Color.White,
                fontSize = (size.value / 2.5).sp
            )
        }
        
        // Try to load image if URI is provided
        if (!avatarUri.isNullOrBlank() && !imageLoadFailed) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "$name's avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
                onSuccess = { 
                    imageLoaded = true
                    imageLoadFailed = false
                },
                onError = { 
                    imageLoadFailed = true
                    imageLoaded = false
                }
            )
        }
    }
}

@Composable
private fun rememberDominantColor(input: String): Color {
    val colors = listOf(
        Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7),
        Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF03A9F4), Color(0xFF00BCD4),
        Color(0xFF009688), Color(0xFF4CAF50), Color(0xFF8BC34A), Color(0xFFCDDC39),
        Color(0xFFFFC107), Color(0xFFFF9800), Color(0xFFFF5722), Color(0xFF795548)
    )
    val index = abs(input.hashCode()) % colors.size
    return colors[index]
}
