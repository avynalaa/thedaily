package com.example.thedaily.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.serialization.encodeToString
import androidx.lifecycle.viewModelScope
import com.example.thedaily.data.ChatMessage
import com.example.thedaily.data.ChatRequest
import com.example.thedaily.data.ChatResponse
import com.example.thedaily.data.Message
import com.example.thedaily.data.AppDatabase
import com.example.thedaily.data.CharacterProfile
import com.example.thedaily.data.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

// We change it to an AndroidViewModel to get access to the application context,
// which is needed to create our SettingsManager.
class ChatViewModel(
    application: Application,
    val characterId: Long
) : AndroidViewModel(application) {

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages = _chatMessages.asStateFlow()

    private val _characterProfile = MutableStateFlow<CharacterProfile?>(null)
    val characterProfile = _characterProfile.asStateFlow()

    private val settingsManager = SettingsManager(application)
    private val chatMessageDao = AppDatabase.getDatabase(application).chatMessageDao()
    private val characterDao = AppDatabase.getDatabase(application).characterProfileDao()

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Load character profile
                var profile = characterDao.getCharacterById(characterId)
                if (profile == null) {
                    profile = settingsManager.characterProfilesFlow.first().find { it.id == characterId }
                    profile?.let {
                        characterDao.insert(it)
                    }
                }
                _characterProfile.value = profile

                // Load chat messages
                chatMessageDao.getMessagesForCharacter(characterId).collect { messages ->
                    _chatMessages.value = messages
                }
            }
        }
    }

    fun sendMessage(text: String, messageToResend: ChatMessage? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val userMessage = messageToResend?.copy(
                status = com.example.thedaily.data.MessageStatus.SENDING,
                errorMessage = null
            ) ?: ChatMessage(
                characterId = characterId,
                text = text,
                isFromUser = true,
                timestamp = System.currentTimeMillis(),
                status = com.example.thedaily.data.MessageStatus.SENDING
            )

            val messageId = chatMessageDao.insert(userMessage)

            val (apiUrl, apiKey, modelId) = settingsManager.settingsFlow.first()
            val currentCharacter = _characterProfile.value

            if (apiUrl.isBlank() || apiKey.isBlank() || modelId.isBlank() || currentCharacter == null) {
                handleError(messageId, userMessage, "API settings or character profile not configured.")
                return@launch
            }

            try {
                val apiEndpoint = "$apiUrl/v1/chat/completions"
                val history = chatMessageDao.getMessagesForCharacterOnce(characterId)
                val messagesToSend = listOf(Message("system", currentCharacter.systemPrompt)) +
                        history.filter { it.status != com.example.thedaily.data.MessageStatus.ERROR }.map {
                            Message(
                                role = if (it.isFromUser) "user" else "assistant",
                                content = it.text,
                                timestamp = it.timestamp
                            )
                        }

                val requestBodyObject = ChatRequest(model = modelId, messages = messagesToSend)
                val requestBodyJson = json.encodeToString(requestBodyObject)

                val request = Request.Builder()
                    .url(apiEndpoint)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBodyJson.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }

                if (response.isSuccessful) {
                    chatMessageDao.insert(userMessage.copy(id = messageId, status = com.example.thedaily.data.MessageStatus.SENT))
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val chatResponse = json.decodeFromString<ChatResponse>(responseBody)
                        val aiText = chatResponse.choices.firstOrNull()?.message?.content ?: "No response"
                        val aiMessage = ChatMessage(
                            characterId = characterId,
                            text = aiText,
                            isFromUser = false,
                            timestamp = System.currentTimeMillis(),
                            status = com.example.thedaily.data.MessageStatus.SENT
                        )
                        chatMessageDao.insert(aiMessage)
                    } else {
                        handleError(messageId, userMessage, "Error: Empty response body")
                    }
                } else {
                    handleError(messageId, userMessage, "Error: ${response.code} ${response.message}")
                }
            } catch (e: Exception) {
                handleError(messageId, userMessage, "Network Error: ${e.message}")
            }
        }
    }

    fun resendMessage(message: ChatMessage) {
        if (message.status == com.example.thedaily.data.MessageStatus.ERROR) {
            sendMessage(message.text, messageToResend = message)
        }
    }

    private suspend fun handleError(messageId: Long, originalMessage: ChatMessage, errorMessage: String) {
        chatMessageDao.insert(
            originalMessage.copy(
                id = messageId,
                status = com.example.thedaily.data.MessageStatus.ERROR,
                errorMessage = errorMessage
            )
        )
    }
}