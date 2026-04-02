package com.altankoc.beuverse.feature.messages.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.beuverse.core.datastore.TokenManager
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.core.websocket.StompManager
import com.altankoc.beuverse.feature.messages.data.dto.ConversationResponseDto
import com.altankoc.beuverse.feature.messages.data.mapper.toDomain
import com.altankoc.beuverse.feature.messages.domain.model.Conversation
import com.altankoc.beuverse.feature.messages.domain.model.Message
import com.altankoc.beuverse.feature.messages.domain.repository.MessagingRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class MessagingViewModel @Inject constructor(
    private val messagingRepository: MessagingRepository,
    private val stompManager: StompManager,
    private val gson: Gson,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _conversationsState = MutableStateFlow<Resource<List<Conversation>>?>(null)
    val conversationsState: StateFlow<Resource<List<Conversation>>?> = _conversationsState.asStateFlow()

    private val _messagesState = MutableStateFlow<Resource<List<Message>>?>(null)
    val messagesState: StateFlow<Resource<List<Message>>?> = _messagesState.asStateFlow()

    private val _sendMessageState = MutableStateFlow<Resource<Message>?>(null)
    val sendMessageState: StateFlow<Resource<Message>?> = _sendMessageState.asStateFlow()

    private val _startConversationState = MutableStateFlow<Resource<Conversation>?>(null)
    val startConversationState: StateFlow<Resource<Conversation>?> = _startConversationState.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _isLastPage = MutableStateFlow(false)
    val isLastPage: StateFlow<Boolean> = _isLastPage.asStateFlow()

    private val _hasUnreadConversation = MutableStateFlow(false)
    val hasUnreadConversation: StateFlow<Boolean> = _hasUnreadConversation.asStateFlow()

    private val _remainingTime = MutableStateFlow<String?>(null)
    val remainingTime: StateFlow<String?> = _remainingTime.asStateFlow()

    private var currentPage = 0
    private val messages = mutableListOf<Message>()
    private var currentUserId: Long? = null
    private val conversations = mutableListOf<Conversation>()
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            currentUserId = tokenManager.userId.first()
        }
        loadConversations()
        observeWebSocket()
    }

    fun loadConversations() {
        messagingRepository.getConversations().onEach { result ->
            _conversationsState.value = result
            if (result is Resource.Success) {
                conversations.clear()
                conversations.addAll(result.data)
            }
        }.launchIn(viewModelScope)
    }

    fun clearUnreadBadge() {
        _hasUnreadConversation.value = false
    }

    fun loadMessages(conversationId: Long, refresh: Boolean = false) {
        if (refresh || currentPage == 0) {
            currentPage = 0
            messages.clear()
            _messagesState.value = null
            _isLastPage.value = false
        }

        if (_isLastPage.value) return

        messagingRepository.getMessages(conversationId, currentPage, 20).onEach { result ->
            when (result) {
                is Resource.Loading -> if (currentPage == 0) _messagesState.value = Resource.Loading
                is Resource.Success -> {
                    _isLoadingMore.value = false
                    val newMessages = result.data.content.filter { new -> messages.none { it.id == new.id } }
                    messages.addAll(newMessages)
                    _messagesState.value = Resource.Success(messages.toList())
                    _isLastPage.value = result.data.last
                    if (!result.data.last) currentPage++
                }
                is Resource.Error -> {
                    _isLoadingMore.value = false
                    _messagesState.value = Resource.Error(result.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun startCountdown(expiresAt: String?) {
        timerJob?.cancel()
        if (expiresAt == null) return

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        timerJob = viewModelScope.launch {
            while (true) {
                try {
                    val expiryDate = sdf.parse(expiresAt.take(19))
                    val now = System.currentTimeMillis()
                    val diff = (expiryDate?.time ?: 0) - now

                    if (diff <= 0) {
                        _remainingTime.value = "00:00:00"
                        break
                    }

                    val hours = diff / (1000 * 60 * 60)
                    val minutes = (diff / (1000 * 60)) % 60
                    val seconds = (diff / 1000) % 60

                    _remainingTime.value = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                } catch (e: Exception) {
                    _remainingTime.value = null
                }
                delay(1000)
            }
        }
    }

    fun stopCountdown() {
        timerJob?.cancel()
        _remainingTime.value = null
    }

    fun sendMessage(conversationId: Long, content: String) {
        if (content.isBlank()) return
        messagingRepository.sendMessage(conversationId, content).onEach { result ->
            _sendMessageState.value = result
            if (result is Resource.Success) {
                if (messages.none { it.id == result.data.id }) {
                    messages.add(0, result.data)
                    _messagesState.value = Resource.Success(messages.toList())
                }
            }
        }.launchIn(viewModelScope)
    }

    fun startConversation(otherStudentId: Long) {
        messagingRepository.startConversation(otherStudentId).onEach { result ->
            _startConversationState.value = result
        }.launchIn(viewModelScope)
    }

    fun acceptConversation(conversationId: Long) {
        messagingRepository.acceptConversation(conversationId).onEach { result ->
            if (result is Resource.Success) {
                loadConversations()
            }
        }.launchIn(viewModelScope)
    }

    fun deleteConversation(conversationId: Long) {
        messagingRepository.deleteConversation(conversationId).onEach { result ->
            if (result is Resource.Success) {
                loadConversations()
            }
        }.launchIn(viewModelScope)
    }

    private fun observeWebSocket() {
        viewModelScope.launch {
            stompManager.messageFlow.collect { payload ->
                try {
                    val message = gson.fromJson(payload, Message::class.java)
                    val isFromOther = message.sender.id != currentUserId
                    if (isFromOther && messages.none { it.id == message.id }) {
                        messages.add(0, message)
                        _messagesState.value = Resource.Success(messages.toList())
                        _hasUnreadConversation.value = true
                    }
                } catch (e: Exception) {
                }
            }
        }

        viewModelScope.launch {
            stompManager.conversationFlow.collect { payload ->
                try {
                    val dto = gson.fromJson(payload, ConversationResponseDto::class.java)
                    val updatedConversation = dto.toDomain()

                    val existingIndex = conversations.indexOfFirst { it.id == updatedConversation.id }
                    if (existingIndex >= 0) {
                        conversations[existingIndex] = updatedConversation
                    } else {
                        conversations.add(0, updatedConversation)
                    }
                    _conversationsState.value = Resource.Success(conversations.toList())
                    _hasUnreadConversation.value = true
                } catch (e: Exception) {
                }
            }
        }
    }

    fun connectWebSocket() {
        if (!stompManager.isConnected()) {
            stompManager.connect()
        }
    }

    fun disconnectWebSocket() {
        stompManager.disconnect()
    }

    override fun onCleared() {
        super.onCleared()
        stompManager.disconnect()
        timerJob?.cancel()
    }

    fun resetStartConversationState() {
        _startConversationState.value = null
    }

    fun markMessagesAsRead(conversationId: Long) {
        messagingRepository.markMessagesAsRead(conversationId).launchIn(viewModelScope)
    }
}