package com.altankoc.beuverse.feature.messages.domain.repository

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.messages.domain.model.Conversation
import com.altankoc.beuverse.feature.messages.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessagingRepository {
    fun startConversation(otherStudentId: Long): Flow<Resource<Conversation>>
    fun acceptConversation(conversationId: Long): Flow<Resource<Conversation>>
    fun getConversations(): Flow<Resource<List<Conversation>>>
    fun getMessages(conversationId: Long, page: Int, size: Int): Flow<Resource<PagedResponseDto<Message>>>
    fun sendMessage(conversationId: Long, content: String): Flow<Resource<Message>>
    fun deleteConversation(conversationId: Long): Flow<Resource<Unit>>
    fun markMessagesAsRead(conversationId: Long): Flow<Resource<Unit>>
}