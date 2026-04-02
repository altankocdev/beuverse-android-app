package com.altankoc.beuverse.core.websocket

import android.util.Log
import com.altankoc.beuverse.core.datastore.TokenManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StompManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    private var stompClient: StompClient? = null
    private val compositeDisposable = CompositeDisposable()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _messageFlow = MutableSharedFlow<String>()
    val messageFlow: SharedFlow<String> = _messageFlow.asSharedFlow()

    private val _notificationFlow = MutableSharedFlow<String>()
    val notificationFlow: SharedFlow<String> = _notificationFlow.asSharedFlow()

    private val _conversationFlow = MutableSharedFlow<String>()
    val conversationFlow: SharedFlow<String> = _conversationFlow.asSharedFlow()

    private val _connectionState = MutableSharedFlow<Boolean>(replay = 1)
    val connectionState: SharedFlow<Boolean> = _connectionState.asSharedFlow()

    fun connect() {
        scope.launch {
            val token = tokenManager.accessToken.first() ?: run {
                Log.e("StompManager", "Token bulunamadı")
                return@launch
            }
            val userId = tokenManager.userId.first() ?: run {
                Log.e("StompManager", "UserId bulunamadı")
                return@launch
            }

            Log.d("StompManager", "WebSocket bağlantısı başlatılıyor... userId: $userId")

            val headers = mutableMapOf(
                "Authorization" to "Bearer $token"
            )

            stompClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                "wss://api.beuverse.fatihaktas.xyz/ws-native",
                headers
            )

            val lifecycleDisposable = stompClient!!.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { event ->
                    scope.launch {
                        when (event.type) {
                            LifecycleEvent.Type.OPENED -> {
                                Log.d("StompManager", "WebSocket CONNECTED ✓")
                                _connectionState.emit(true)
                                subscribeToTopics(userId)
                            }
                            LifecycleEvent.Type.CLOSED -> {
                                Log.d("StompManager", "WebSocket CLOSED")
                                _connectionState.emit(false)
                            }
                            LifecycleEvent.Type.ERROR -> {
                                Log.e("StompManager", "WebSocket ERROR: ${event.exception?.message}")
                                _connectionState.emit(false)
                            }
                            LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                                Log.w("StompManager", "WebSocket FAILED_SERVER_HEARTBEAT")
                            }
                            else -> {}
                        }
                    }
                }

            compositeDisposable.add(lifecycleDisposable)
            stompClient!!.connect()
        }
    }

    private fun subscribeToTopics(userId: Long) {
        Log.d("StompManager", "Topic'lere abone olunuyor... userId: $userId")

        val messageDisposable = stompClient!!
            .topic("/queue/messages-$userId")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { stompMessage ->
                    Log.d("StompManager", "Mesaj alındı: ${stompMessage.payload}")
                    scope.launch { _messageFlow.emit(stompMessage.payload) }
                },
                { error ->
                    Log.e("StompManager", "Mesaj topic hatası: ${error.message}")
                }
            )

        val notificationDisposable = stompClient!!
            .topic("/queue/notifications-$userId")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { stompMessage ->
                    Log.d("StompManager", "Bildirim alındı: ${stompMessage.payload}")
                    scope.launch { _notificationFlow.emit(stompMessage.payload) }
                },
                { error ->
                    Log.e("StompManager", "Bildirim topic hatası: ${error.message}")
                }
            )

        val conversationDisposable = stompClient!!
            .topic("/queue/conversations-$userId")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { stompMessage ->
                    Log.d("StompManager", "Konuşma güncellemesi alındı: ${stompMessage.payload}")
                    scope.launch { _conversationFlow.emit(stompMessage.payload) }
                },
                { error ->
                    Log.e("StompManager", "Konuşma topic hatası: ${error.message}")
                }
            )

        compositeDisposable.add(messageDisposable)
        compositeDisposable.add(notificationDisposable)
        compositeDisposable.add(conversationDisposable)
    }

    fun disconnect() {
        Log.d("StompManager", "WebSocket bağlantısı kesiliyor...")
        compositeDisposable.clear()
        stompClient?.disconnect()
        stompClient = null
    }

    fun isConnected(): Boolean = stompClient?.isConnected == true
}