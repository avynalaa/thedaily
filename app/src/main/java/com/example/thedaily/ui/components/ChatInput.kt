package com.example.thedaily.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thedaily.ui.theme.ButterflyColors

@Composable
fun ChatInput(
    message: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Magical animations
    val infiniteTransition = rememberInfiniteTransition(label = "magical_input")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )
    
    val float by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Magical background with gradient and shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            ButterflyColors.VividPurple.copy(alpha = 0.1f + shimmer * 0.1f),
                            ButterflyColors.RoseWing.copy(alpha = 0.05f + shimmer * 0.05f),
                            ButterflyColors.MagicalGlow.copy(alpha = 0.1f + shimmer * 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            ButterflyColors.VividPurple.copy(alpha = 0.3f),
                            ButterflyColors.RoseWing.copy(alpha = 0.2f),
                            ButterflyColors.SkyBlue.copy(alpha = 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
        )
        
        // Floating sparkles
        repeat(3) { index ->
            val sparkleOffset = float + (index * 2.5f)
            Box(
                modifier = Modifier
                    .offset(
                        x = (20 + index * 60).dp,
                        y = (-sparkleOffset).dp
                    )
                    .size(4.dp)
                    .rotate(rotation + (index * 120f))
                    .background(
                        color = when (index) {
                            0 -> ButterflyColors.GoldenShimmer.copy(alpha = shimmer * 0.8f)
                            1 -> ButterflyColors.SilverShimmer.copy(alpha = shimmer * 0.6f)
                            else -> ButterflyColors.MagicalGlow.copy(alpha = shimmer * 0.7f)
                        },
                        shape = CircleShape
                    )
            )
        }
        
        // Content row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Magical attachment button with butterfly
            IconButton(
                onClick = { /* Handle attachment */ },
                modifier = Modifier
                    .size(40.dp)
                    .scale(1f + shimmer * 0.05f)
            ) {
                Text(
                    text = "🦋",
                    fontSize = 20.sp,
                    modifier = Modifier.rotate(rotation * 0.1f)
                )
            }

            // Magical text input using BasicTextField for full control
            Box(
                modifier = Modifier.weight(1f)
            ) {
                if (message.isEmpty()) {
                    Text(
                        text = "✨ Whisper your thoughts...",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Serif,
                            color = ButterflyColors.VividPurple.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                
                BasicTextField(
                    value = message,
                    onValueChange = onMessageChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(ButterflyColors.VividPurple),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }

            // Magical send button
            IconButton(
                onClick = {
                    if (message.isNotBlank()) {
                        onSend()
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = if (message.isNotBlank()) {
                            Brush.radialGradient(
                                colors = listOf(
                                    ButterflyColors.VividPurple,
                                    ButterflyColors.RoseWing
                                )
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Transparent
                                )
                            )
                        },
                        shape = CircleShape
                    )
                    .scale(if (message.isNotBlank()) 1f + shimmer * 0.1f else 1f)
            ) {
                if (message.isNotBlank()) {
                    Text(
                        text = "🚀",
                        fontSize = 18.sp,
                        modifier = Modifier.rotate(rotation * 0.05f)
                    )
                } else {
                    Text(
                        text = "🎤",
                        fontSize = 18.sp,
                        modifier = Modifier.scale(1f + shimmer * 0.1f)
                    )
                }
            }
        }
    }
}
