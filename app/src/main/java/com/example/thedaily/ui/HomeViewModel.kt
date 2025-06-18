package com.example.thedaily.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thedaily.data.CharacterProfile
import com.example.thedaily.data.ChatMessage
import com.example.thedaily.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.Dispatchers

data class RecentChat(
    val character: CharacterProfile,
    val lastMessage: ChatMessage?,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _recentChats = MutableStateFlow<List<RecentChat>>(emptyList())
    val recentChats: StateFlow<List<RecentChat>> = _recentChats.asStateFlow()

    private val characterDao = AppDatabase.getDatabase(application).characterProfileDao()
    private val chatMessageDao = AppDatabase.getDatabase(application).chatMessageDao()

    init {
        loadRecentChats()
    }

    fun loadRecentChats() {
        viewModelScope.launch(Dispatchers.IO) {
            // Observe all messages and characters
            combine(
                characterDao.getAllCharacters(),
                chatMessageDao.getAllMessages()
            ) { characters, allMessages ->
                // Create a map of character IDs to their latest message
                val latestMessagesByCharacter = allMessages
                    .groupBy { it.characterId }
                    .mapValues { (_, messages) -> messages.maxByOrNull { it.timestamp } }

                // Create RecentChat objects only for characters with messages
                characters
                    .filter { character -> latestMessagesByCharacter.containsKey(character.id.toLong()) }
                    .map { character ->
                        RecentChat(
                            character = character,
                            lastMessage = latestMessagesByCharacter[character.id.toLong()],
                            unreadCount = 0, // TODO: Implement unread count
                            isOnline = character.isOnline
                        )
                    }
                    .sortedByDescending { it.lastMessage?.timestamp ?: 0L }
            }
            .distinctUntilChanged() // Only update when the data actually changes
            .collect { chats ->
                _recentChats.value = chats
            }
        }
    }

    // Function to refresh the chat list
    fun refreshChats() {
        viewModelScope.launch(Dispatchers.IO) {
            loadRecentChats()
        }
    }
}
