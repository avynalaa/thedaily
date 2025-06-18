package com.example.thedaily.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thedaily.data.ApiPreset
import com.example.thedaily.data.CharacterProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    savedApiUrl: String,
    savedApiKey: String,
    availableModels: List<String>,
    isLoading: Boolean,
    presets: List<ApiPreset>,
    onFetchModels: (String, String) -> Unit,
    onSave: (String, String, String) -> Unit,
    onSavePreset: (String, String, String, String) -> Unit,
    onDeletePreset: (String) -> Unit,
    onLoadPreset: (String) -> Unit,
    characterProfiles: List<CharacterProfile>,
    onSaveCharacterProfile: (CharacterProfile) -> Unit,
    onDeleteCharacterProfile: (Long) -> Unit,
    onSetCurrentCharacter: (Long) -> Unit
) {
    var apiUrl by remember { mutableStateOf(savedApiUrl) }
    var apiKey by remember { mutableStateOf(savedApiKey) }
    var selectedModel by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var presetName by remember { mutableStateOf("") }
    var showSavePresetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.background
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 16.dp, start = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Settings",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Text("API Settings", style = MaterialTheme.typography.headlineMedium) }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = apiUrl,
                            onValueChange = { apiUrl = it },
                            label = { Text("API Base URL") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = apiKey,
                            onValueChange = { apiKey = it },
                            label = { Text("API Key") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { onFetchModels(apiUrl, apiKey) },
                            enabled = apiUrl.isNotBlank() && apiKey.isNotBlank() && !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Fetch Available Models")
                        }

                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }

                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedModel,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select Model") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                availableModels.forEach { modelId ->
                                    DropdownMenuItem(
                                        text = { Text(modelId) },
                                        onClick = {
                                            selectedModel = modelId
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onSave(apiUrl, apiKey, selectedModel) },
                                enabled = selectedModel.isNotBlank(),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Save Settings")
                            }

                            Button(
                                onClick = { showSavePresetDialog = true },
                                enabled = selectedModel.isNotBlank(),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = "Save Preset")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save as Preset")
                            }
                        }
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Saved Presets", style = MaterialTheme.typography.titleMedium)
                        if (presets.isEmpty()) {
                            Text(
                                "No presets saved yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            presets.forEach { preset ->
                                PresetItem(
                                    preset = preset,
                                    onLoad = {
                                        apiUrl = preset.apiUrl
                                        apiKey = preset.apiKey
                                        selectedModel = preset.modelId
                                        onLoadPreset(preset.name)
                                    },
                                    onDelete = { onDeletePreset(preset.name) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSavePresetDialog) {
        AlertDialog(
            onDismissRequest = { showSavePresetDialog = false },
            title = { Text("Save Preset") },
            text = {
                OutlinedTextField(
                    value = presetName,
                    onValueChange = { presetName = it },
                    label = { Text("Preset Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (presetName.isNotBlank()) {
                            onSavePreset(presetName, apiUrl, apiKey, selectedModel)
                            showSavePresetDialog = false
                            presetName = ""
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showSavePresetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PresetItem(preset: ApiPreset, onLoad: () -> Unit, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                preset.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Row {
                Button(
                    onClick = onLoad,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("Load", color = Color.White) }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Preset", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}