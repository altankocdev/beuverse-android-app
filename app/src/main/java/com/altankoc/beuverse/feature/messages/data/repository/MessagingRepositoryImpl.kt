package com.altankoc.beuverse.feature.messages.data.repository

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.messages.data.api.MessagingApi
import com.altankoc.beuverse.feature.messages.data.dto.MessageRequestDto
import com.altankoc.beuverse.feature.messages.data.mapper.toDomain
import com.altankoc.beuverse.feature.messages.domain.model.Conversation
import com.altankoc.beuverse.feature.messages.domain.model.Message
import com.altankoc.beuverse.feature.messages.domain.repository.MessagingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class MessagingRepositoryImpl @Inject constructor(
    private val messagingApi: MessagingApi
) : MessagingRepository {

    override fun startConversation(otherStudentId: Long): Flow<Resource<Conversation>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(messagingApi.startConversation(otherStudentId).toDomain()))
        } catch (e: HttpException) {
            emit(Resource.Error(e.code().toString()))
        } catch (e: Exception) {
            emit(Resource.Error("error_network"))
        }
    }

    override fun acceptConversation(conversationId: Long): Flow<Resource<Conversation>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(messagingApi.acceptConversation(conversationId).toDomain()))
        } catch (e: HttpException) {
            emit(Resource.Error(e.code().toString()))
        } catch (e: Exception) {
            emit(Resource.Error("error_network"))
        }
    }

    override fun getConversations(): Flow<Resource<List<Conversation>>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(messagingApi.getConversations().map { it.toDomain() }))
        } catch (e: HttpException) {
            emit(Resource.Error(e.code().toString()))
        } catch (e: Exception) {
            emit(Resource.Error("error_network"))
        }
    }

    override fun getMessages(conversationId: Long, page: Int, size: Int): Flow<Resource<PagedResponseDto<Message>>> = flow {
        emit(Resource.Loading)
        try {
            val response = messagingApi.getMessages(conversationId, page, size)
            emit(Resource.Success(PagedResponseDto(
                content = response.content.map { it.toDomain() },
                totalElements = response.totalElements,
                totalPages = response.totalPages,
                number = response.number,
                size = response.size,
                first = response.first,
                last = response.last
            )))
        } catch (e: HttpException) {
            emit(Resource.Error(e.code().toString()))
        } catch (e: Exception) {
            emit(Resource.Error("error_network"))
        }
    }

    override fun sendMessage(conversationId: Long, content: String): Flow<Resource<Message>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(messagingApi.sendMessage(conversationId, MessageRequestDto(content)).toDomain()))
        } catch (e: HttpException) {
            emit(Resource.Error(e.code().toString()))
        } catch (e: Exception) {
            emit(Resource.Error("error_network"))
        }
    }

    override fun deleteConversation(conversationId: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            messagingApi.deleteConversation(conversationId)
            emit(Resource.Success(Unit))
        } catch (e: HttpException) {
            emit(Resource.Error(e.code().toString()))
        } catch (e: Exception) {
            emit(Resource.Error("error_network"))
        }
    }

    override fun markMessagesAsRead(conversationId: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            messagingApi.markMessagesAsRead(conversationId)
            emit(Resource.Success(Unit))
        } catch (e: HttpException) {
            emit(Resource.Error(e.code().toString()))
        } catch (e: Exception) {
            emit(Resource.Error("error_network"))
        }
    }
}