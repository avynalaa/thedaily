package com.example.thedaily.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thedaily.data.CharacterProfile
import com.example.thedaily.data.repository.RecentChat
import com.example.thedaily.data.repository.TheDailyRepository
import com.example.thedaily.utils.launchSafe
import com.example.thedaily.utils.mutableStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TheDailyRepository.getInstance(application)
    
    private val _recentChats = mutableStateFlow<List<RecentChat>>(emptyList())
    val recentChats: StateFlow<List<RecentChat>> = _recentChats.asStateFlow()

    private val _isLoading = mutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorState = mutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    init {
        initializeData()
    }

    private fun initializeData() {
        launchSafe(
            onError = { error ->
                Log.e("HomeViewModel", "Error initializing data", error)
                _errorState.value = "Failed to load data: ${error.message}"
                _isLoading.value = false
            }
        ) {
            _isLoading.value = true
            _errorState.value = null
            
            // First, try to migrate data from settings to database
            try {
                repository.migrateDataFromSettings()
                Log.d("HomeViewModel", "Data migration completed")
                
                // Debug: Log character profiles
                val dbProfiles = repository.getAllCharacterProfiles().first()
                val settingsProfiles = repository.characterProfilesFlow.first()
                Log.d("HomeViewModel", "DB Profiles: ${dbProfiles.size}, Settings Profiles: ${settingsProfiles.size}")
                
                dbProfiles.forEach { profile ->
                    Log.d("HomeViewModel", "DB Profile: ${profile.name}, Avatar: ${profile.avatarUri}")
                }
                
                settingsProfiles.forEach { profile ->
                    Log.d("HomeViewModel", "Settings Profile: ${profile.name}, Avatar: ${profile.avatarUri}")
                }
                
            } catch (e: Exception) {
                Log.w("HomeViewModel", "Data migration failed, continuing...", e)
            }
            
            // Load recent chats
            repository.getRecentChats().collect { chats ->
                _recentChats.value = chats
                _isLoading.value = false
                Log.d("HomeViewModel", "Loaded ${chats.size} recent chats")
                
                // Debug: Log recent chat data
                chats.forEach { chat ->
                    Log.d("HomeViewModel", "Recent Chat: ${chat.character.name}, Avatar: ${chat.character.avatarUri}")
                }
            }
        }
    }

    fun refreshData() {
        initializeData()
    }

    fun clearError() {
        _errorState.value = null
    }

    // Helper method to get character profiles for migration/debugging
    fun getCharacterProfiles() = repository.getAllCharacterProfiles()
    
    // Helper method to check if we have any data
    suspend fun hasAnyData(): Boolean {
        return try {
            val profiles = repository.getAllCharacterProfiles().first()
            val settingsProfiles = repository.characterProfilesFlow.first()
            profiles.isNotEmpty() || settingsProfiles.isNotEmpty()
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error checking for data", e)
            false
        }
    }
}
