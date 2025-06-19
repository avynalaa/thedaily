package com.example.thedaily.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.Application
import com.example.thedaily.data.CharacterProfile
import com.example.thedaily.ui.components.*
import com.example.thedaily.viewmodel.ChatViewModel
import com.example.thedaily.viewmodel.ChatViewModelFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    character: CharacterProfile,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(
            LocalContext.current.applicationContext as Application, 
            character.id
        )
    )
) {
    val messages by viewModel.chatMessages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(character.id) {
        // The ChatViewModel is already initialized with the characterId, so we don't need to call any method
    }

    MagicalButterflyBackground {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with magical styling
            MagicalCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Avatar(
                        name = character.name,
                        avatarUri = character.avatarUri,
                        size = 40.dp
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = character.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Online",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Messages
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true
            ) {
                // Group messages by date
                val groupedMessages = messages.groupBy {
                    val instant = Instant.ofEpochMilli(it.timestamp)
                    LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate()
                }

                // Reverse the order of grouped messages and within each group for proper display with reverseLayout
                groupedMessages.toList().reversed().forEach { (date, messagesOnDate) ->
                    // Reverse the messages within each date group so they appear in correct order
                    items(messagesOnDate.reversed()) { message ->
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
                            edited = message.edited,
                            deleteType = message.deleteType
                        )
                    }
                    item {
                        DateHeader(formatDate(date))
                    }
                }
            }

            // Input area with magical styling
            ChatInput(
                message = inputText,
                onMessageChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DeleteEditMenuDialog(
    onEdit: () -> Unit,
    onDeleteForMe: () -> Unit,
    onDeleteForEveryone: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Message Options") },
        text = {
            Column {
                TextButton(onClick = onEdit) { Text("Edit Message") }
                TextButton(onClick = onDeleteForMe) { Text("Delete for Me") }
                TextButton(onClick = onDeleteForEveryone) { Text("Delete for Everyone") }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    )
}

@Composable
private fun EditMessageDialog(
    editText: String,
    onEditTextChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Edit Message") },
        text = {
            OutlinedTextField(
                value = editText,
                onValueChange = onEditTextChange,
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = onSave) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    )
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Helper function to format date
private fun formatDate(date: java.time.LocalDate): String {
    val now = java.time.LocalDate.now()
    return when {
        date == now -> "Today"
        date == now.minusDays(1) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }
}
