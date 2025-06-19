package com.example.thedaily.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(context: Context) {

    private val dataStore = context.dataStore
    private val gson = Gson()

    companion object {
        val API_URL = stringPreferencesKey("api_url")
        val API_KEY = stringPreferencesKey("api_key")
        val MODEL_ID = stringPreferencesKey("model_id")
        val PRESETS = stringPreferencesKey("presets")
        val CHARACTER_PROFILES = stringPreferencesKey("character_profiles")
        val USER_PROFILE = stringPreferencesKey("user_profile")
    }

    suspend fun saveSettings(apiUrl: String, apiKey: String, modelId: String) {
        dataStore.edit { settings ->
            settings[API_URL] = apiUrl
            settings[API_KEY] = apiKey
            settings[MODEL_ID] = modelId
        }
    }

    val settingsFlow = dataStore.data.map { preferences ->
        Triple(
            preferences[API_URL] ?: "",
            preferences[API_KEY] ?: "",
            preferences[MODEL_ID] ?: ""
        )
    }

    suspend fun savePreset(preset: ApiPreset) {
        val currentPresets = presetsFlow.first().toMutableList()
        val index = currentPresets.indexOfFirst { it.name == preset.name }
        if (index != -1) {
            currentPresets[index] = preset
        } else {
            currentPresets.add(preset)
        }
        dataStore.edit { settings ->
            settings[PRESETS] = gson.toJson(currentPresets)
        }
    }

    suspend fun deletePreset(presetName: String) {
        val currentPresets = presetsFlow.first().toMutableList()
        currentPresets.removeAll { it.name == presetName }
        dataStore.edit { settings ->
            settings[PRESETS] = gson.toJson(currentPresets)
        }
    }

    val presetsFlow = dataStore.data.map { preferences ->
        preferences[PRESETS]?.let {
            try {
                val type = object : TypeToken<List<ApiPreset>>() {}.type
                gson.fromJson<List<ApiPreset>>(it, type)
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }

    suspend fun loadPreset(presetName: String) {
        val preset = presetsFlow.first().find { it.name == presetName }
        if (preset != null) {
            saveSettings(preset.apiUrl, preset.apiKey, preset.modelId)
        }
    }

    val characterProfilesFlow = dataStore.data.map { preferences ->
        preferences[CHARACTER_PROFILES]?.let {
            try {
                val type = object : TypeToken<List<CharacterProfile>>() {}.type
                gson.fromJson<List<CharacterProfile>>(it, type)
            } catch (e: Exception) {
                listOf(CharacterProfile(id = 0L, name = "Default", systemPrompt = "You are a helpful assistant.", isCurrent = true))
            }
        } ?: listOf(CharacterProfile(id = 0L, name = "Default", systemPrompt = "You are a helpful assistant.", isCurrent = true))
    }

    suspend fun saveCharacterProfile(profile: CharacterProfile) {
        val profiles = characterProfilesFlow.first().toMutableList()
        val index = profiles.indexOfFirst { it.id == profile.id }
        if (index != -1) {
            profiles[index] = profile
        } else {
            profiles.add(profile)
        }
        dataStore.edit {
            it[CHARACTER_PROFILES] = gson.toJson(profiles)
        }
    }

    suspend fun deleteCharacterProfile(profileId: Long) {
        val profiles = characterProfilesFlow.first().toMutableList()
        profiles.removeAll { it.id == profileId }
        dataStore.edit {
            it[CHARACTER_PROFILES] = gson.toJson(profiles)
        }
    }

    suspend fun setCurrentCharacter(profileId: Long) {
        val profiles = characterProfilesFlow.first().map {
            it.copy(isCurrent = it.id == profileId)
        }
        dataStore.edit {
            it[CHARACTER_PROFILES] = gson.toJson(profiles)
        }
    }

    // --- Clear Data Methods ---

    suspend fun clearApiData() {
        dataStore.edit { it.remove(PRESETS) }
    }

    suspend fun clearChats() {
        // This should be implemented in the database layer, but for now, just clear character profiles and reset affection/context
        val profiles = characterProfilesFlow.first().map {
            it.copy(
                affectionLevel = 50.0,
                mood = "Neutral",
                relationshipContext = RelationshipContext.STRANGERS,
                relationshipHistory = ""
            )
        }
        dataStore.edit {
            it[CHARACTER_PROFILES] = gson.toJson(profiles)
        }
        // You should also clear all chat messages in the database (not handled here)
    }

    suspend fun clearCharacters() {
        dataStore.edit {
            it.remove(CHARACTER_PROFILES)
        }
        // You should also clear all chat messages in the database (not handled here)
    }

    suspend fun clearEverything() {
        dataStore.edit { it.clear() }
        // You should also clear all chat messages in the database (not handled here)
    }

    val userProfileFlow = dataStore.data.map { preferences ->
        preferences[USER_PROFILE]?.let {
            try {
                gson.fromJson(it, UserProfile::class.java)
            } catch (e: Exception) {
                UserProfile()
            }
        } ?: UserProfile()
    }

    suspend fun saveUserProfile(userProfile: UserProfile) {
        dataStore.edit { settings ->
            settings[USER_PROFILE] = gson.toJson(userProfile)
        }
    }
}