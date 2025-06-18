package com.example.thedaily.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.thedaily.data.MessageStatus

@Composable
fun MessageBubble(
    text: String,
    isFromUser: Boolean,
    timestamp: String,
    status: MessageStatus,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val bubbleColor = when {
        status == MessageStatus.ERROR -> MaterialTheme.colorScheme.errorContainer
        isFromUser -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val bubbleShape = RoundedCornerShape(
        topStart = if (isFromUser) 16.dp else 4.dp,
        topEnd = if (isFromUser) 4.dp else 16.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = if (isFromUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = bubbleShape,
            color = bubbleColor,
            shadowElevation = 2.dp,
            modifier = if (status == MessageStatus.ERROR) modifier.clickable(onClick = onClick) else modifier
        ) {
            Text(
                text = text,
                color = if (isFromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        Row(
            modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            if (isFromUser) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = when (status) {
                        MessageStatus.SENDING -> Icons.Default.AccessTime
                        MessageStatus.SENT -> Icons.Default.Done
                        MessageStatus.DELIVERED -> Icons.Default.DoneAll
                        MessageStatus.READ -> Icons.Default.DoneAll
                        MessageStatus.ERROR -> Icons.Default.Error
                    },
                    contentDescription = "Message Status",
                    tint = when (status) {
                        MessageStatus.READ -> MaterialTheme.colorScheme.primary
                        MessageStatus.ERROR -> MaterialTheme.colorScheme.error
                        else -> Color.Gray
                    },
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
