package com.example.thedaily.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.thedaily.data.*
import com.example.thedaily.data.repository.TheDailyRepository
import com.example.thedaily.data.network.ApiClient
import com.example.thedaily.utils.launchSafe
import com.example.thedaily.utils.mutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TheDailyRepository.getInstance(application)
    private val apiClient = ApiClient.getInstance()

    private val _availableModels = mutableStateFlow<List<String>>(emptyList())
    val availableModels: StateFlow<List<String>> = _availableModels.asStateFlow()

    private val _isLoading = mutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorState = mutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    // Expose repository flows
    val settingsFlow = repository.settingsFlow
    val presets = repository.presetsFlow
    val characterProfiles = repository.characterProfilesFlow
    val userProfileFlow = repository.userProfileFlow

    fun fetchAvailableModels(apiUrl: String, apiKey: String) {
        launchSafe(
            onError = { 
                _errorState.value = "Failed to fetch models: ${it.message}"
                _isLoading.value = false
            }
        ) {
            _isLoading.value = true
            
            val result = apiClient.fetchAvailableModels(apiUrl, apiKey)
            result.fold(
                onSuccess = { models ->
                    _availableModels.value = models
                    _errorState.value = null
                },
                onFailure = { error ->
                    _errorState.value = "Failed to fetch models: ${error.message}"
                }
            )
            _isLoading.value = false
        }
    }

    fun saveSettings(apiUrl: String, apiKey: String, modelId: String) {
        launchSafe(
            onError = { _errorState.value = "Failed to save settings: ${it.message}" }
        ) {
            repository.saveSettings(apiUrl, apiKey, modelId)
        }
    }

    fun savePreset(name: String, apiUrl: String, apiKey: String, modelId: String) {
        launchSafe(
            onError = { _errorState.value = "Failed to save preset: ${it.message}" }
        ) {
            val preset = ApiPreset(name, apiUrl, apiKey, modelId)
            repository.savePreset(preset)
        }
    }

    fun deletePreset(presetName: String) {
        launchSafe(
            onError = { _errorState.value = "Failed to delete preset: ${it.message}" }
        ) {
            repository.deletePreset(presetName)
        }
    }

    fun loadPreset(presetName: String) {
        launchSafe(
            onError = { _errorState.value = "Failed to load preset: ${it.message}" }
        ) {
            repository.loadPreset(presetName)
        }
    }

    fun saveCharacterProfile(profile: CharacterProfile) {
        launchSafe(
            onError = { _errorState.value = "Failed to save character: ${it.message}" }
        ) {
            repository.saveCharacterProfile(profile)
        }
    }

    fun deleteCharacterProfile(profileId: Long) {
        launchSafe(
            onError = { _errorState.value = "Failed to delete character: ${it.message}" }
        ) {
            repository.deleteCharacterProfile(profileId)
        }
    }

    fun setCurrentCharacter(profileId: Long) {
        launchSafe(
            onError = { _errorState.value = "Failed to set current character: ${it.message}" }
        ) {
            repository.setCurrentCharacter(profileId)
        }
    }

    fun saveUserProfile(userProfile: UserProfile) {
        launchSafe(
            onError = { _errorState.value = "Failed to save user profile: ${it.message}" }
        ) {
            repository.saveUserProfile(userProfile)
        }
    }

    // Clear operations
    fun clearApiData() {
        launchSafe(
            onError = { _errorState.value = "Failed to clear API data: ${it.message}" }
        ) {
            repository.clearApiData()
        }
    }

    fun clearChats() {
        launchSafe(
            onError = { _errorState.value = "Failed to clear chats: ${it.message}" }
        ) {
            repository.clearChats()
        }
    }

    fun clearCharacters() {
        launchSafe(
            onError = { _errorState.value = "Failed to clear characters: ${it.message}" }
        ) {
            repository.clearCharacters()
        }
    }

    fun clearEverything() {
        launchSafe(
            onError = { _errorState.value = "Failed to clear everything: ${it.message}" }
        ) {
            repository.clearEverything()
        }
    }
    
    fun clearError() {
        _errorState.value = null
    }
}