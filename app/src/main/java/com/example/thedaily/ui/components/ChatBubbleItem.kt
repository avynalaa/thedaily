package com.example.thedaily.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.thedaily.data.ChatMessage
import com.example.thedaily.ui.theme.MessengerBubbleUserEnd
import com.example.thedaily.ui.theme.MessengerBubbleUserStart
import com.example.thedaily.ui.theme.MessengerBubbleOther
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatBubbleItem(message: ChatMessage) {
    val isUser = message.isFromUser
    val bubbleBrush = if (isUser) {
        Brush.horizontalGradient(
            colors = listOf(MessengerBubbleUserStart, MessengerBubbleUserEnd)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(MessengerBubbleOther, MessengerBubbleOther)
        )
    }
    val shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomEnd = if (isUser) 0.dp else 16.dp,
        bottomStart = if (isUser) 16.dp else 0.dp
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(bubbleBrush)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.text,
                color = if (isUser) Color.White else Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = formatTimestamp(message.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
