package com.example.thedaily.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.sp
import com.example.thedaily.ui.components.Avatar
import com.example.thedaily.viewmodel.ChatViewModel
import com.example.thedaily.viewmodel.ChatViewModelFactory
import com.example.thedaily.data.AffectionManager

data class ProfileCardItem(
    val title: String,
    val content: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactProfileScreen(
    characterId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val factory = ChatViewModelFactory(context.applicationContext as Application, characterId)
    val viewModel: ChatViewModel = viewModel(factory = factory)
    val profile by viewModel.characterProfile.collectAsState()

    val name = profile?.name ?: "Loading..."
    val avatarUri = profile?.avatarUri

    // Subjective experience line
    val subjectiveLine = profile?.let { AffectionManager.subjectiveExperience(it) } ?: ""

    // This list holds the data for our profile cards.
    val profileCards = listOf(
        ProfileCardItem("Notifications") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mute notifications", modifier = Modifier.weight(1f))
                Switch(checked = false, onCheckedChange = {})
            }
        },
        ProfileCardItem("Media, links, and docs") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                    )
                }
                Spacer(Modifier.weight(1f))
                Text("View all", color = MaterialTheme.colorScheme.primary)
            }
        },
        ProfileCardItem("Starred messages") {
            Text("No starred messages yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        ProfileCardItem("Secret chat") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Secret chat", modifier = Modifier.weight(1f))
                Button(onClick = {}, enabled = false) { Text("Enter") }
            }
        },
        ProfileCardItem("Groups in common") {
            Text("Feature coming soon...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        ProfileCardItem("Timeline/History") {
            Column {
                Text("Started chatting: Jan 2025", fontSize = 14.sp)
                Text("Major events will appear here soon...", fontSize = 13.sp, color = Color.Gray)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Edit profile */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { /* TODO: More options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { /* Handle block action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Block Contact")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Header section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp) // Increased height for better spacing
                ) {
                    // Cover photo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                    )

                    // Avatar and name section
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    CircleShape
                                )
                                .padding(4.dp)
                        ) {
                            Avatar(
                                name = name,
                                avatarUri = avatarUri,
                                size = 112.dp,
                                modifier = Modifier.clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color.Green, CircleShape)
                            )
                            Text(
                                text = "Online",
                                color = Color.Green,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Subjective experience line
                        if (subjectiveLine.isNotBlank()) {
                            Text(
                                text = subjectiveLine,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Cards section
            items(profileCards) { card ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = card.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        card.content()
                    }
                }
            }
        }
    }
}
