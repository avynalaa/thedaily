package com.example.thedaily.data
import com.example.thedaily.data.ApiPreset
import com.example.thedaily.data.CharacterProfile

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.serialization.builtins.ListSerializer
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// This line creates the actual settings file on the device.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Serializable
data class ApiPreset(
    val name: String,
    val apiUrl: String,
    val apiKey: String,
    val modelId: String
)

class SettingsManager(context: Context) {

    private val dataStore = context.dataStore
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        val API_URL = stringPreferencesKey("api_url")
        val API_KEY = stringPreferencesKey("api_key")
        val MODEL_ID = stringPreferencesKey("model_id")
        val PRESETS = stringPreferencesKey("presets")
        val CHARACTER_PROFILES = stringPreferencesKey("character_profiles")
    }

    // This function saves the settings.
    suspend fun saveSettings(apiUrl: String, apiKey: String, modelId: String) {
        dataStore.edit { settings ->
            settings[API_URL] = apiUrl
            settings[API_KEY] = apiKey
            settings[MODEL_ID] = modelId
        }
    }

    // This "Flow" lets other parts of our app automatically get updates
    // whenever the settings are changed.
    val settingsFlow = dataStore.data.map { preferences ->
        // This Triple holds the url, key, and model in that order.
        Triple(
            preferences[API_URL] ?: "",
            preferences[API_KEY] ?: "",
            preferences[MODEL_ID] ?: ""
        )
    }

    // Save a new preset
    suspend fun savePreset(preset: ApiPreset) {
        dataStore.edit { settings ->
            val currentPresets = settings[PRESETS]?.let {
                json.decodeFromString(ListSerializer(ApiPreset.serializer()), it)
            } ?: emptyList()
            
            val updatedPresets = currentPresets.filter { it.name != preset.name } + preset
            settings[PRESETS] = json.encodeToString(ListSerializer(ApiPreset.serializer()), updatedPresets)
        }
    }

    // Delete a preset by name
    suspend fun deletePreset(presetName: String) {
        dataStore.edit { settings ->
            val currentPresets = settings[PRESETS]?.let {
                json.decodeFromString(ListSerializer(ApiPreset.serializer()), it)
            } ?: emptyList()
            
            val updatedPresets = currentPresets.filter { it.name != presetName }
            settings[PRESETS] = json.encodeToString(ListSerializer(ApiPreset.serializer()), updatedPresets)
        }
    }

    // Get all saved presets
    val presetsFlow = dataStore.data.map { preferences ->
        preferences[PRESETS]?.let {
            json.decodeFromString(ListSerializer(ApiPreset.serializer()), it)
        } ?: emptyList()
    }

    // Load a preset into the current settings
    suspend fun loadPreset(presetName: String) {
        val presets = presetsFlow.first()
        val preset = presets.find { it.name == presetName } ?: return
        saveSettings(preset.apiUrl, preset.apiKey, preset.modelId)
    }

    // --- Character Profile Management ---

    val characterProfilesFlow = dataStore.data.map {
        it[CHARACTER_PROFILES]?.let {
            json.decodeFromString(ListSerializer(CharacterProfile.serializer()), it)
        } ?: listOf(CharacterProfile(id = 0L, name = "Default", systemPrompt = "You are a helpful assistant.", isCurrent = true))
    }

    suspend fun saveCharacterProfile(profile: CharacterProfile) {
        dataStore.edit {
            val profiles = characterProfilesFlow.first().toMutableList()
            val index = profiles.indexOfFirst { it.id == profile.id }
            if (index >= 0) {
                profiles[index] = profile
            } else {
                profiles.add(profile)
            }
            it[CHARACTER_PROFILES] = json.encodeToString(ListSerializer(CharacterProfile.serializer()), profiles)
        }
    }

    suspend fun deleteCharacterProfile(profileId: Long) {
        dataStore.edit {
            val profiles = characterProfilesFlow.first().filter { it.id != profileId }
            it[CHARACTER_PROFILES] = json.encodeToString(ListSerializer(CharacterProfile.serializer()), profiles)
        }
    }

    suspend fun setCurrentCharacter(profileId: Long) {
        dataStore.edit {
            val profiles = characterProfilesFlow.first().map {
                it.copy(isCurrent = it.id == profileId)
            }
            it[CHARACTER_PROFILES] = json.encodeToString(ListSerializer(CharacterProfile.serializer()), profiles)
        }
    }

    // TEMPORARY: Call this once to clear all DataStore preferences and fix JSON migration crash
    suspend fun clearAllPreferences() {
        dataStore.edit { it.clear() }
    }
}