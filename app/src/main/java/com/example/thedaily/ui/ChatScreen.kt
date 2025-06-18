package com.example.thedaily.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thedaily.data.ChatMessage
import com.example.thedaily.ui.components.Avatar
import com.example.thedaily.ui.components.ChatInput
import com.example.thedaily.ui.components.MessageBubble
import com.example.thedaily.ui.theme.MessengerBackgroundEnd
import com.example.thedaily.ui.theme.MessengerBackgroundStart
import com.example.thedaily.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    characterId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ChatViewModel
) {
    val messages by viewModel.chatMessages.collectAsState()
    val characterProfile by viewModel.characterProfile.collectAsState()
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var showErrorDialog by remember { mutableStateOf<ChatMessage?>(null) }


    if (showErrorDialog != null) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = null },
            title = { Text("Message Failed") },
            text = { Text(showErrorDialog?.errorMessage ?: "An unknown error occurred.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.resendMessage(showErrorDialog!!)
                    showErrorDialog = null
                }) {
                    Text("Resend")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showErrorDialog = null
                    onNavigateToSettings()
                }) {
                    Text("Settings")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
                        MaterialTheme.colorScheme.background
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(1000f, 2000f)
                )
            )
    ) {
        // Floating, rounded app bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                .height(72.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(32.dp),
            shadowElevation = 10.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (characterProfile != null) {
                    Avatar(
                        name = characterProfile!!.name,
                        avatarUri = characterProfile!!.avatarUri,
                        size = 40.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onNavigateToProfile)
                    ) {
                        Text(
                            characterProfile!!.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1
                        )
                        Text(
                            "Online", // Placeholder for status
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                } else {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Text("Loading...", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Main chat content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 104.dp, bottom = 90.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                messages.groupBy {
                    val instant = Instant.ofEpochMilli(it.timestamp)
                    LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate()
                }.forEach { (date, messagesOnDate) ->
                    item {
                        DateHeader(formatDate(date))
                    }
                    items(messagesOnDate) { message ->
                        val time = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(message.timestamp),
                            ZoneId.systemDefault()
                        )
                        val formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm"))
                        MessageBubble(
                            text = message.text,
                            isFromUser = message.isFromUser,
                            timestamp = formattedTime,
                            status = message.status,
                            modifier = Modifier.padding(vertical = 2.dp),
                            onClick = {
                                if (message.status == com.example.thedaily.data.MessageStatus.ERROR) {
                                    showErrorDialog = message
                                }
                            }
                        )
                    }
                }
            }
        }

        // Floating, pill-shaped input bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            shape = RoundedCornerShape(32.dp),
            shadowElevation = 12.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            ChatInput(
                message = text,
                onMessageChange = { text = it },
                onSend = {
                    if (text.isNotBlank()) {
                        viewModel.sendMessage(text)
                        text = ""
                        coroutineScope.launch {
                            if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
                        }
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        // Auto-scroll to bottom on new message
        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(index = messages.size - 1)
            }
        }
    }
}

@Composable
private fun DateHeader(date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Gray.copy(alpha = 0.2f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

private fun formatDate(date: LocalDate): String {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    return when (date) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
    }
}
