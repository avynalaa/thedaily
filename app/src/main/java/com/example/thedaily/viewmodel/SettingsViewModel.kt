package com.example.thedaily.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thedaily.data.ApiPreset
import com.example.thedaily.data.CharacterProfile
import com.example.thedaily.data.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

// --- NEW: A data class to help us parse the server's response for the model list ---
@Serializable
data class ModelListResponse(
    val data: List<ModelData>
)
@Serializable
data class ModelData(
    val id: String
)
// -----------------------------------------------------------------------------------

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsManager = SettingsManager(application)
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    // --- NEW: A "StateFlow" to hold our list of model IDs for the UI ---
    private val _availableModels = MutableStateFlow<List<String>>(emptyList())
    val availableModels = _availableModels.asStateFlow()

    // --- NEW: A StateFlow to tell the UI when we are loading ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val settingsFlow = settingsManager.settingsFlow

    // Add state flow for presets
    private val _presets = MutableStateFlow<List<ApiPreset>>(emptyList())
    val presets = _presets.asStateFlow()

    // --- NEW: A StateFlow to hold our character profiles for the UI ---
    private val _characterProfiles = MutableStateFlow<List<CharacterProfile>>(emptyList())
    val characterProfiles = _characterProfiles.asStateFlow()

    init {
        // Collect presets from SettingsManager
        viewModelScope.launch {
            settingsManager.presetsFlow.collect { presetList ->
                _presets.value = presetList
            }
        }

        // --- NEW: Collect character profiles from SettingsManager ---
        viewModelScope.launch {
            settingsManager.characterProfilesFlow.collect { profileList ->
                _characterProfiles.value = profileList
            }
        }
    }

    // --- NEW: The function that connects to the server and gets the model list ---
    fun fetchAvailableModels(apiUrl: String, apiKey: String) {
        // Set loading state to true to show the spinner
        _isLoading.value = true

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                // The standard endpoint for listing models
                val endpoint = "$apiUrl/v1/models"

                val request = Request.Builder()
                    .url(endpoint)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .get()
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val modelListResponse = json.decodeFromString(ModelListResponse.serializer(), responseBody)
                        // We extract just the "id" string from each model object
                        _availableModels.value = modelListResponse.data.map { it.id }
                    }
                } else {
                    Log.e("SettingsViewModel", "Failed to fetch models: ${response.code}")
                    // You could add error handling here to show a message to the user
                }
            } catch (e: IOException) {
                Log.e("SettingsViewModel", "Network error fetching models", e)
            } finally {
                // Always set loading state back to false when done
                _isLoading.value = false
            }
        }
    }

    // This function is still used to save the final choice
    fun saveSettings(apiUrl: String, apiKey: String, modelId: String) {
        viewModelScope.launch {
            settingsManager.saveSettings(apiUrl, apiKey, modelId)
        }
    }

    // Add preset management functions
    fun savePreset(name: String, apiUrl: String, apiKey: String, modelId: String) {
        viewModelScope.launch {
            val preset = ApiPreset(name, apiUrl, apiKey, modelId)
            settingsManager.savePreset(preset)
        }
    }

    fun deletePreset(presetName: String) {
        viewModelScope.launch {
            settingsManager.deletePreset(presetName)
        }
    }

    fun loadPreset(presetName: String) {
        // Find the preset from the list we already have in our state
        val presetToLoad = _presets.value.find { it.name == presetName }

        // If we found it, call saveSettings to make it the active configuration
        presetToLoad?.let { preset ->
            saveSettings(preset.apiUrl, preset.apiKey, preset.modelId)
        }
    }

    // --- NEW: Character Profile Management ---
    fun saveCharacterProfile(profile: CharacterProfile) {
        viewModelScope.launch {
            settingsManager.saveCharacterProfile(profile)
        }
    }

    fun deleteCharacterProfile(profileId: Long) {
        viewModelScope.launch {
            settingsManager.deleteCharacterProfile(profileId)
        }
    }

    fun setCurrentCharacter(profileId: Long) {
        viewModelScope.launch {
            settingsManager.setCurrentCharacter(profileId)
        }
    }
}