package com.example.thedaily

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.thedaily.ui.CharacterEditScreen
import com.example.thedaily.ui.ChatScreen
import com.example.thedaily.ui.ContactsScreen
import com.example.thedaily.ui.SettingsScreen
import com.example.thedaily.ui.HomeViewModel
import com.example.thedaily.ui.HomeScreen
import com.example.thedaily.ui.theme.TheDailyTheme
import com.example.thedaily.viewmodel.ChatViewModel
import com.example.thedaily.viewmodel.ChatViewModelFactory
import com.example.thedaily.viewmodel.SettingsViewModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
// TEMP: Clear DataStore to fix JSON migration crash
kotlinx.coroutines.GlobalScope.launch {
    com.example.thedaily.data.SettingsManager(applicationContext).clearAllPreferences()
}
        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent {
            TheDailyTheme {
                val navController = rememberNavController()
                val characterProfiles by settingsViewModel.characterProfiles.collectAsState()
                val recentChats by homeViewModel.recentChats.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable(
                            route = "profile/{characterId}",
                            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
                            com.example.thedaily.ui.ContactProfileScreen(
                                characterId = characterId.toLong(),
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("contacts") {
                            ContactsScreen(
                                characterProfiles = characterProfiles,
                                onNavigateToChat = { characterId ->
                                    navController.navigate("chat/$characterId")
                                },
                                onNavigateToEditCharacter = { characterId ->
                                    val route = characterId?.let { "editCharacter/$it" } ?: "editCharacter"
                                    navController.navigate(route)
                                },
                                onNavigateToSettings = { navController.navigate("settings") },
                                onSetCurrentCharacter = { settingsViewModel.setCurrentCharacter(it) },
                                onNavigateToProfile = { characterId ->
                                    navController.navigate("profile/$characterId")
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                recentChats = recentChats,
                                onChatClick = { characterId: Long ->
                                    navController.navigate("chat/$characterId")
                                    homeViewModel.refreshChats()
                                },
                                onContactsClick = { navController.navigate("contacts") },
                                onSettingsClick = { navController.navigate("settings") }
                            )
                        }

                        composable(
                            route = "editCharacter/{characterId}",
                            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
                        ) {
                            backStackEntry ->
                            val characterId = backStackEntry.arguments?.getString("characterId")
                            val characterToEdit = characterId?.let { id ->
                                characterProfiles.find { it.id == id.toLong() }
                            }
                            CharacterEditScreen(
                                characterProfile = characterToEdit,
                                onSave = {
                                    settingsViewModel.saveCharacterProfile(it)
                                    navController.popBackStack()
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(route = "editCharacter") {
                            CharacterEditScreen(
                                characterProfile = null, // Creating a new one
                                onSave = {
                                    settingsViewModel.saveCharacterProfile(it)
                                    navController.popBackStack()
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("settings") {
                            val availableModels by settingsViewModel.availableModels.collectAsState()
                            val isLoading by settingsViewModel.isLoading.collectAsState()
                            val presets by settingsViewModel.presets.collectAsState()
                            val savedSettings by settingsViewModel.settingsFlow.collectAsState(initial = Triple("", "", ""))
                            val (savedApiUrl, savedApiKey, _) = savedSettings
                            val context = LocalContext.current

                            SettingsScreen(
                                savedApiUrl = savedApiUrl,
                                savedApiKey = savedApiKey,
                                availableModels = availableModels,
                                isLoading = isLoading,
                                presets = presets,
                                // Pass empty list and no-op lambdas for character profiles
                                characterProfiles = emptyList(),
                                onFetchModels = { apiUrl, apiKey ->
                                    settingsViewModel.fetchAvailableModels(apiUrl, apiKey)
                                },
                                onSave = { apiUrl, apiKey, modelId ->
                                    if (modelId.isNotBlank()) {
                                        settingsViewModel.saveSettings(apiUrl, apiKey, modelId)
                                        navController.popBackStack()
                                    } else {
                                        Toast.makeText(context, "Please select a model first", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onSavePreset = { name, apiUrl, apiKey, modelId ->
                                    settingsViewModel.savePreset(name, apiUrl, apiKey, modelId)
                                },
                                onDeletePreset = { presetName ->
                                    settingsViewModel.deletePreset(presetName)
                                },
                                onLoadPreset = { presetName ->
                                    settingsViewModel.loadPreset(presetName)
                                },
                                onSaveCharacterProfile = {},
                                onDeleteCharacterProfile = {},
                                onSetCurrentCharacter = {}
                            )
                        }

                        composable(
                            route = "chat/{characterId}",
                            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val characterId = backStackEntry.arguments?.getString("characterId") ?: return@composable
                            ChatScreenWrapper(
                                characterId = characterId.toLong(),
                                onNavigateBack = {
                                    navController.popBackStack()
                                    homeViewModel.refreshChats()
                                },
                                onNavigateToProfile = { navController.navigate("profile/$characterId") },
                                onNavigateToSettings = { navController.navigate("settings") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatScreenWrapper(
    characterId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val factory = ChatViewModelFactory(LocalContext.current.applicationContext as Application, characterId.toLong())
    val chatViewModel: ChatViewModel = viewModel(factory = factory)
    ChatScreen(
        characterId = characterId,
        onNavigateBack = onNavigateBack,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToSettings = onNavigateToSettings,
        viewModel = chatViewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsUI(
    // --- NEW: Pass in the currently saved settings ---
    savedApiUrl: String,
    savedApiKey: String,
    // --- The rest of the parameters are the same ---
    availableModels: List<String>,
    isLoading: Boolean,
    onFetchModels: (String, String) -> Unit,
    onSave: (String, String, String) -> Unit
) {
    // --- UPDATED: The text fields now start with the saved values ---
    var apiUrl by remember { mutableStateOf(savedApiUrl) }
    var apiKey by remember { mutableStateOf(savedApiKey) }
    var selectedModel by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("API Settings")

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
            enabled = apiUrl.isNotBlank() && apiKey.isNotBlank() && !isLoading
        ) {
            Text("Fetch Available Models")
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
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

        Button(
            onClick = { onSave(apiUrl, apiKey, selectedModel) },
            enabled = selectedModel.isNotBlank()
        ) {
            Text("Save Settings & Start Chatting")
        }
    }
}