package com.example.thedaily.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.thedaily.data.CharacterProfile
import com.example.thedaily.ui.components.Avatar
import java.util.UUID
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterEditScreen(
    characterProfile: CharacterProfile?, // Null if creating new
    onSave: (CharacterProfile) -> Unit,
    onDelete: (() -> Unit)? = null,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf(characterProfile?.name ?: "") }
    var phoneNumber by remember {
        mutableStateOf(characterProfile?.phoneNumber ?: generateRandomPhoneNumber())
    }
    var systemPrompt by remember { mutableStateOf(characterProfile?.systemPrompt ?: "") }
    var personalityTags by remember { mutableStateOf(characterProfile?.personalityTags?.joinToString(", ") ?: "") }
    var preferenceTags by remember { mutableStateOf(characterProfile?.preferenceTags?.joinToString(", ") ?: "") }
    var dealbreakerTags by remember { mutableStateOf(characterProfile?.dealbreakerTags?.joinToString(", ") ?: "") }
    var avatarUri by remember { mutableStateOf(characterProfile?.avatarUri) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> avatarUri = uri?.toString() }
    )

    val isFormValid = name.isNotBlank() && phoneNumber.isNotBlank() && systemPrompt.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (characterProfile == null) "New Character" else "Edit Character") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val updatedProfile = (characterProfile ?: CharacterProfile(id = System.currentTimeMillis(), name = "", systemPrompt = "")).copy(
                                name = name,
                                phoneNumber = phoneNumber,
                                avatarUri = avatarUri,
                                systemPrompt = systemPrompt,
                                personalityTags = personalityTags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                preferenceTags = preferenceTags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                dealbreakerTags = dealbreakerTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            )
                            onSave(updatedProfile)
                        },
                        enabled = isFormValid
                    ) {
                        Icon(Icons.Default.Done, contentDescription = "Save Character")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Avatar(
                    name = name,
                    avatarUri = avatarUri,
                    size = 120.dp
                )
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Change Picture")
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Contact ID / Phone Number") },
                placeholder = { Text("Auto-generated, or enter your own") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = systemPrompt,
                onValueChange = { systemPrompt = it },
                label = { Text("Personality / System Prompt") },
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )

            Text("Tags (comma-separated)", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = personalityTags,
                onValueChange = { personalityTags = it },
                label = { Text("Personality Tags (e.g., cheerful, sarcastic)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = preferenceTags,
                onValueChange = { preferenceTags = it },
                label = { Text("Likes / Preferences (e.g., sci-fi, coffee)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dealbreakerTags,
                onValueChange = { dealbreakerTags = it },
                label = { Text("Dislikes / Dealbreakers (e.g., dishonesty)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        // Save Contact Button
        Button(
            onClick = {
                val updatedProfile = (characterProfile ?: CharacterProfile(id = System.currentTimeMillis(), name = "", systemPrompt = "")).copy(
                    name = name,
                    phoneNumber = phoneNumber,
                    avatarUri = avatarUri,
                    systemPrompt = systemPrompt,
                    personalityTags = personalityTags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    preferenceTags = preferenceTags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    dealbreakerTags = dealbreakerTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                )
                onSave(updatedProfile)
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Contact")
        }
        // Delete Character Button (only if editing)
        if (characterProfile != null && onDelete != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onDelete() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Character", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}

// Helper to generate a random phone number
private fun generateRandomPhoneNumber(): String {
    val countryCode = "+1"
    val number = (100_000_0000..999_999_9999).random()
    return "$countryCode $number"
}

