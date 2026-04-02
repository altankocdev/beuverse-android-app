package com.altankoc.beuverse.feature.notification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.core.websocket.StompManager
import com.altankoc.beuverse.feature.notification.domain.model.Notification
import com.altankoc.beuverse.feature.notification.domain.usecase.GetNotificationsUseCase
import com.altankoc.beuverse.feature.notification.domain.usecase.GetUnreadCountUseCase
import com.altankoc.beuverse.feature.notification.domain.usecase.MarkAsReadUseCase
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val getUnreadCountUseCase: GetUnreadCountUseCase,
    private val markAsReadUseCase: MarkAsReadUseCase,
    private val stompManager: StompManager,
    private val gson: Gson
) : ViewModel() {

    private val _notificationsState = MutableStateFlow<Resource<List<Notification>>>(Resource.Loading)
    val notificationsState: StateFlow<Resource<List<Notification>>> = _notificationsState.asStateFlow()

    private val _unreadCount = MutableStateFlow(0L)
    val unreadCount: StateFlow<Long> = _unreadCount.asStateFlow()

    private var currentPage = 0
    private val notifications = mutableListOf<Notification>()

    init {
        loadNotifications()
        loadUnreadCount()
        observeWebSocket()
    }

    fun connectWebSocket() {
        if (!stompManager.isConnected()) {
            stompManager.connect()
        }
    }

    fun loadNotifications(refresh: Boolean = false) {
        if (refresh) {
            currentPage = 0
            notifications.clear()
        }

        getNotificationsUseCase(currentPage).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    if (currentPage == 0) _notificationsState.value = Resource.Loading
                }
                is Resource.Success -> {
                    notifications.addAll(result.data.content)
                    _notificationsState.value = Resource.Success(notifications.toList())
                    if (!result.data.last) currentPage++
                }
                is Resource.Error -> {
                    _notificationsState.value = Resource.Error(result.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadUnreadCount() {
        getUnreadCountUseCase().onEach { result ->
            if (result is Resource.Success) {
                _unreadCount.value = result.data
            }
        }.launchIn(viewModelScope)
    }

    private fun observeWebSocket() {
        viewModelScope.launch {
            stompManager.notificationFlow.collect { payload ->
                try {
                    val notification = gson.fromJson(payload, Notification::class.java)
                    if (notifications.none { it.id == notification.id }) {
                        notifications.add(0, notification)
                        _notificationsState.value = Resource.Success(notifications.toList())
                        _unreadCount.value++
                    }
                } catch (e: Exception) {
                    // parse hatası
                }
            }
        }
    }

    fun markAsRead(notificationId: Long) {
        markAsReadUseCase(notificationId).onEach { result ->
            if (result is Resource.Success) {
                val updated = notifications.map { notification ->
                    if (notification.id == notificationId) notification.copy(read = true)
                    else notification
                }
                notifications.clear()
                notifications.addAll(updated)
                _notificationsState.value = Resource.Success(notifications.toList())
                if (_unreadCount.value > 0) _unreadCount.value--
            }
        }.launchIn(viewModelScope)
    }

    fun markAllAsRead() {
        markAsReadUseCase().onEach { result ->
            if (result is Resource.Success) {
                val updated = notifications.map { it.copy(read = true) }
                notifications.clear()
                notifications.addAll(updated)
                _notificationsState.value = Resource.Success(notifications.toList())
                _unreadCount.value = 0
            }
        }.launchIn(viewModelScope)
    }
}