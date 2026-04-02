package com.altankoc.beuverse.feature.messages.data.api

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.feature.messages.data.dto.ConversationResponseDto
import com.altankoc.beuverse.feature.messages.data.dto.MessageRequestDto
import com.altankoc.beuverse.feature.messages.data.dto.MessageResponseDto
import retrofit2.http.*

interface MessagingApi {

    @POST("messaging/conversations/{otherStudentId}")
    suspend fun startConversation(
        @Path("otherStudentId") otherStudentId: Long
    ): ConversationResponseDto

    @PUT("messaging/conversations/{conversationId}/accept")
    suspend fun acceptConversation(
        @Path("conversationId") conversationId: Long
    ): ConversationResponseDto

    @GET("messaging/conversations")
    suspend fun getConversations(): List<ConversationResponseDto>

    @GET("messaging/conversations/{conversationId}/messages")
    suspend fun getMessages(
        @Path("conversationId") conversationId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PagedResponseDto<MessageResponseDto>

    @POST("messaging/conversations/{conversationId}/messages")
    suspend fun sendMessage(
        @Path("conversationId") conversationId: Long,
        @Body request: MessageRequestDto
    ): MessageResponseDto

    @DELETE("messaging/conversations/{conversationId}")
    suspend fun deleteConversation(
        @Path("conversationId") conversationId: Long
    )

    @PUT("messaging/conversations/{conversationId}/read")
    suspend fun markMessagesAsRead(
        @Path("conversationId") conversationId: Long
    )
}