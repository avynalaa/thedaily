package com.example.thedaily.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thedaily.data.*
import com.example.thedaily.data.repository.TheDailyRepository
import com.example.thedaily.data.network.ApiClient
import com.example.thedaily.utils.launchSafe
import com.example.thedaily.utils.mutableStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.io.IOException

class ChatViewModel(
    application: Application,
    val characterId: Long
) : AndroidViewModel(application) {

    private val repository = TheDailyRepository.getInstance(application)
    private val apiClient = ApiClient.getInstance()
    private val json = Json { ignoreUnknownKeys = true }

    private val _chatMessages = mutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _characterProfile = mutableStateFlow<CharacterProfile?>(null)
    val characterProfile: StateFlow<CharacterProfile?> = _characterProfile.asStateFlow()

    private val _isTyping = mutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _partialMessage = mutableStateFlow<String?>(null)
    val partialMessage: StateFlow<String?> = _partialMessage.asStateFlow()

    private val _errorState = mutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    private var currentEventSource: EventSource? = null

    init {
        loadCharacterAndMessages()
    }

    private fun loadCharacterAndMessages() {
        launchSafe(
            onError = { _errorState.value = "Failed to load character data: ${it.message}" }
        ) {
            // Load character profile
            val profile = repository.getCharacterProfile(characterId)
            if (profile == null) {
                // Try to get from settings (legacy support)
                val profiles = repository.characterProfilesFlow.first()
                val foundProfile = profiles.find { it.id == characterId }
                if (foundProfile != null) {
                    repository.insertCharacterProfile(foundProfile)
                    _characterProfile.value = foundProfile
                } else {
                    _errorState.value = "Character not found"
                    return@launchSafe
                }
            } else {
                _characterProfile.value = profile
            }

            // Load messages
            repository.getChatMessages(characterId).collect { messages ->
                _chatMessages.value = messages
            }
        }
    }

    fun sendMessage(text: String, messageToResend: ChatMessage? = null) {
        if (text.isBlank() && messageToResend == null) return

        launchSafe(
            onError = { 
                viewModelScope.launch {
                    handleError(null, null, "Failed to send message: ${it.message}")
                }
            }
        ) {
            val userMessage = messageToResend?.copy(
                status = MessageStatus.SENDING,
                errorMessage = null
            ) ?: ChatMessage(
                characterId = characterId,
                text = text,
                isFromUser = true,
                timestamp = System.currentTimeMillis(),
                status = MessageStatus.SENDING
            )

            val messageId = repository.insertChatMessage(userMessage)
            val updatedMessage = userMessage.copy(id = messageId)

            val (apiUrl, apiKey, modelId) = repository.settingsFlow.first()
            val currentCharacter = _characterProfile.value

            if (apiUrl.isBlank() || apiKey.isBlank() || modelId.isBlank() || currentCharacter == null) {
                handleError(messageId, updatedMessage, "API settings or character profile not configured.")
                return@launchSafe
            }

            try {
                // Update message status to SENT
                repository.updateChatMessage(updatedMessage.copy(status = MessageStatus.SENT))

                // Start typing indicator
                _isTyping.value = true
                _partialMessage.value = null

                // Update character affection
                updateCharacterAffection(text, isFromUser = true)

                // Determine if streaming is supported
                val supportsStreaming = apiUrl.contains("openai", ignoreCase = true) || 
                                      apiUrl.contains("stream", ignoreCase = true)

                Log.d("ChatViewModel", "Sending message to: $apiUrl")
                Log.d("ChatViewModel", "Model: $modelId")
                Log.d("ChatViewModel", "Streaming: $supportsStreaming")
                Log.d("ChatViewModel", "Character: ${currentCharacter.name}")

                if (supportsStreaming) {
                    handleStreamingResponse(apiUrl, apiKey, modelId, currentCharacter)
                } else {
                    handleNonStreamingResponse(apiUrl, apiKey, modelId, currentCharacter)
                }

            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error in sendMessage", e)
                viewModelScope.launch {
                    handleError(messageId, updatedMessage, "Network error: ${e.message}")
                }
            }
        }
    }

    private suspend fun handleStreamingResponse(
        apiUrl: String,
        apiKey: String,
        modelId: String,
        character: CharacterProfile
    ) {
        val systemPrompt = buildSystemPrompt(character)
        val history = repository.getChatMessages(characterId).first()
        val messagesToSend = listOf(Message("system", systemPrompt)) +
                history.filter { it.status != MessageStatus.ERROR }.map {
                    Message(
                        role = if (it.isFromUser) "user" else "assistant",
                        content = it.text,
                        timestamp = it.timestamp
                    )
                }

        val eventSourceListener = object : EventSourceListener() {
            private val fullMessage = StringBuilder()
            
            override fun onOpen(eventSource: EventSource, response: okhttp3.Response) {
                _isTyping.value = true
                _partialMessage.value = ""
            }
            
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                if (data == "[DONE]") {
                    viewModelScope.launch {
                        finishStreamingResponse(fullMessage.toString())
                    }
                    eventSource.cancel()
                    return
                }
                
                try {
                    val response = json.decodeFromString<ChatResponse>(data)
                    val choice = response.choices.firstOrNull()
                    val delta = choice?.delta?.content ?: choice?.message?.content ?: ""
                    if (delta.isNotEmpty()) {
                        fullMessage.append(delta)
                        _partialMessage.value = fullMessage.toString()
                    }
                } catch (_: Exception) { 
                    // Ignore parsing errors for partial responses
                }
            }
            
            override fun onClosed(eventSource: EventSource) {
                _isTyping.value = false
                _partialMessage.value = null
            }
            
            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: okhttp3.Response?
            ) {
                _isTyping.value = false
                _partialMessage.value = null
                _errorState.value = "Streaming failed: ${t?.message}"
            }
        }

        currentEventSource = apiClient.createEventSource(
            apiUrl, apiKey, modelId, messagesToSend, eventSourceListener
        )
    }

    private suspend fun handleNonStreamingResponse(
        apiUrl: String,
        apiKey: String,
        modelId: String,
        character: CharacterProfile
    ) {
        val systemPrompt = buildSystemPrompt(character)
        val history = repository.getChatMessages(characterId).first()
        val messagesToSend = listOf(Message("system", systemPrompt)) +
                history.filter { it.status != MessageStatus.ERROR && it.text.isNotBlank() }.map {
                    Message(
                        role = if (it.isFromUser) "user" else "assistant",
                        content = it.text,
                        timestamp = it.timestamp
                    )
                }

        val result = apiClient.sendChatRequest(apiUrl, apiKey, modelId, messagesToSend)
        
        result.fold(
            onSuccess = { responseText ->
                finishStreamingResponse(responseText)
            },
            onFailure = { error ->
                _isTyping.value = false
                _partialMessage.value = null
                _errorState.value = "API request failed: ${error.message}"
            }
        )
    }

    private suspend fun finishStreamingResponse(responseText: String) {
        _isTyping.value = false
        
        // Create AI message with natural timestamp - it will be later than user message
        // since this is called as a response to the user's message
        val aiMessage = ChatMessage(
            characterId = characterId,
            text = responseText,
            isFromUser = false,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.RECEIVED
        )
        
        repository.insertChatMessage(aiMessage)
        _partialMessage.value = null
        
        // Update character affection
        updateCharacterAffection(responseText, isFromUser = false)
    }

    fun resendMessage(message: ChatMessage) {
        sendMessage(message.text, message)
    }

    fun clearError() {
        _errorState.value = null
    }

    // Add missing methods for ChatScreen
    fun editMessage(messageId: Long, newText: String) {
        launchSafe(
            onError = { _errorState.value = "Failed to edit message: ${it.message}" }
        ) {
            val message = _chatMessages.value.find { it.id == messageId }
            if (message != null) {
                val updatedMessage = message.copy(
                    text = newText,
                    edited = true,
                    editTimestamp = System.currentTimeMillis()
                )
                repository.updateChatMessage(updatedMessage)
            }
        }
    }

    fun deleteMessageUserOnly(messageId: Long) {
        launchSafe(
            onError = { _errorState.value = "Failed to delete message: ${it.message}" }
        ) {
            val message = _chatMessages.value.find { it.id == messageId }
            if (message != null) {
                val updatedMessage = message.copy(
                    deleteType = DeleteType.USER_ONLY,
                    deleteTimestamp = System.currentTimeMillis()
                )
                repository.updateChatMessage(updatedMessage)
            }
        }
    }

    fun deleteMessageForEveryone(messageId: Long) {
        launchSafe(
            onError = { _errorState.value = "Failed to delete message: ${it.message}" }
        ) {
            val message = _chatMessages.value.find { it.id == messageId }
            if (message != null) {
                val updatedMessage = message.copy(
                    deleteType = DeleteType.FOR_EVERYONE,
                    deleteTimestamp = System.currentTimeMillis()
                )
                repository.updateChatMessage(updatedMessage)
            }
        }
    }

    private suspend fun handleError(messageId: Long?, message: ChatMessage?, error: String) {
        withContext(Dispatchers.Main) {
            _isTyping.value = false
            _partialMessage.value = null
            _errorState.value = error
        }
        
        if (messageId != null && message != null) {
            withContext(Dispatchers.IO) {
                repository.updateChatMessage(
                    message.copy(
                        status = MessageStatus.ERROR,
                        errorMessage = error
                    )
                )
            }
        }
    }

    private suspend fun buildSystemPrompt(character: CharacterProfile): String {
        // Get user profile for personalization
        val userProfile = repository.userProfileFlow.first()
        
        return buildString {
            append("You are ${character.name}. ")
            append(character.systemPrompt)
            
            character.personalityTags?.takeIf { it.isNotEmpty() }?.let { tags ->
                append("\n\nPersonality traits: ${tags.joinToString(", ")}")
            }
            character.interestTags?.takeIf { it.isNotEmpty() }?.let { tags ->
                append("\nInterests: ${tags.joinToString(", ")}")
            }
            character.dealbreakerTags?.takeIf { it.isNotEmpty() }?.let { tags ->
                append("\nDislikes: ${tags.joinToString(", ")}")
            }
            
            append("\nCurrent mood: ${character.mood}")
            append("\nRelationship context: ${character.relationshipContext.name}")
            
            if (userProfile.name.isNotBlank()) {
                append("\n\nYou're talking to ${userProfile.name}.")
                userProfile.personalityTags?.takeIf { it.isNotEmpty() }?.let { tags ->
                    append(" They are ${tags.joinToString(", ")}.")
                }
            }
            
            if (character.relationshipHistory.isNotBlank()) {
                append("\n\nRelationship history: ${character.relationshipHistory}")
            }
        }
    }

    private suspend fun updateCharacterAffection(message: String, isFromUser: Boolean) {
        val character = _characterProfile.value ?: return
        
        val newAffection = AffectionManager.adjustAffection(
            character = character,
            message = message,
            isFromUser = isFromUser
        )
        
        if (newAffection != character.affectionLevel) {
            val updatedCharacter = character.copy(affectionLevel = newAffection)
            repository.updateCharacterProfile(updatedCharacter)
            repository.saveCharacterProfile(updatedCharacter) // Also save to settings
            _characterProfile.value = updatedCharacter
        }
    }

    override fun onCleared() {
        super.onCleared()
        currentEventSource?.cancel()
    }
}