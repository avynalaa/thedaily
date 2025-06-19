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

import android.content.Context
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterEditScreen(
    characterProfile: CharacterProfile?, // Null if creating new
    onSave: (CharacterProfile) -> Unit,
    onDelete: (() -> Unit)? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(characterProfile?.name ?: "") }
    var phoneNumber by remember {
        mutableStateOf(characterProfile?.phoneNumber ?: generateRandomPhoneNumber())
    }
    var systemPrompt by remember { mutableStateOf(characterProfile?.systemPrompt ?: "") }
    var personalityTags by remember { mutableStateOf(characterProfile?.personalityTags?.joinToString(", ") ?: "") }
    var interestTags by remember { mutableStateOf(characterProfile?.interestTags?.joinToString(", ") ?: "") }
    var dealbreakerTags by remember { mutableStateOf(characterProfile?.dealbreakerTags?.joinToString(", ") ?: "") }
    var avatarUri by remember { mutableStateOf(characterProfile?.avatarUri) }
    var relationshipContext by remember { mutableStateOf(characterProfile?.relationshipContext ?: com.example.thedaily.data.RelationshipContext.STRANGERS) }
    var relationshipHistory by remember { mutableStateOf(characterProfile?.relationshipHistory ?: "") }
    var isRelationshipDropdownExpanded by remember { mutableStateOf(false) }

    // Helper to copy image to app-private storage
    fun copyImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = "avatar_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream: OutputStream = file.outputStream()
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                val copiedPath = copyImageToInternalStorage(context, uri = uri)
                avatarUri = copiedPath
            }
        }
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
                                interestTags = interestTags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                dealbreakerTags = dealbreakerTags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                relationshipContext = relationshipContext,
                                relationshipHistory = relationshipHistory
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

            var isPersonalityDropdownExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = isPersonalityDropdownExpanded,
                onExpandedChange = { isPersonalityDropdownExpanded = !isPersonalityDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = personalityTags,
                    onValueChange = { personalityTags = it },
                    label = { Text("Personality Tags") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isPersonalityDropdownExpanded,
                    onDismissRequest = { isPersonalityDropdownExpanded = false }
                ) {
                    listOf("Friendly", "Witty", "Shy", "Outgoing", "Serious", "Spammy", "Introvert", "Extrovert", "Emotionally Unavailable", "Night Owl", "Early Bird", "Busy").forEach { tag ->
                        DropdownMenuItem(
                            text = { Text(tag) },
                            onClick = {
                                val tags = personalityTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
                                if (tags.contains(tag)) {
                                    tags.remove(tag)
                                } else {
                                    tags.add(tag)
                                }
                                personalityTags = tags.joinToString(", ")
                            }
                        )
                    }
                }
            }
            Button(
                onClick = {
                    // Placeholder for tag generation
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Auto-generate Tags from Description")
            }

            OutlinedTextField(
                value = interestTags,
                onValueChange = { interestTags = it },
                label = { Text("Interests (e.g., sci-fi, coffee)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dealbreakerTags,
                onValueChange = { dealbreakerTags = it },
                label = { Text("Dislikes / Dealbreakers (e.g., dishonesty)") },
                modifier = Modifier.fillMaxWidth()
            )

           ExposedDropdownMenuBox(
               expanded = isRelationshipDropdownExpanded,
               onExpandedChange = { isRelationshipDropdownExpanded = !isRelationshipDropdownExpanded },
           ) {
               OutlinedTextField(
                   value = relationshipContext.name,
                   onValueChange = {},
                   readOnly = true,
                   label = { Text("Relationship Context") },
                   trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRelationshipDropdownExpanded) },
                   modifier = Modifier.menuAnchor().fillMaxWidth()
               )
               ExposedDropdownMenu(
                   expanded = isRelationshipDropdownExpanded,
                   onDismissRequest = { isRelationshipDropdownExpanded = false }
               ) {
                   com.example.thedaily.data.RelationshipContext.values().forEach { context ->
                       DropdownMenuItem(
                           text = { Text(context.name) },
                           onClick = {
                               relationshipContext = context
                               isRelationshipDropdownExpanded = false
                           }
                       )
                   }
               }
           }

           OutlinedTextField(
               value = relationshipHistory,
               onValueChange = { relationshipHistory = it },
               label = { Text("How do you know each other?") },
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
                    interestTags = interestTags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    dealbreakerTags = dealbreakerTags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    relationshipContext = relationshipContext,
                    relationshipHistory = relationshipHistory
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
