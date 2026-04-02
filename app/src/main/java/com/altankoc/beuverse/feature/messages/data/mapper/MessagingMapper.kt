package com.altankoc.beuverse.feature.messages.data.mapper

import com.altankoc.beuverse.feature.messages.data.dto.ConversationResponseDto
import com.altankoc.beuverse.feature.messages.data.dto.MessageResponseDto
import com.altankoc.beuverse.feature.messages.domain.model.Conversation
import com.altankoc.beuverse.feature.messages.domain.model.Message
import com.altankoc.beuverse.feature.profile.data.mapper.toDomain

fun ConversationResponseDto.toDomain(): Conversation {
    return Conversation(
        id = id,
        otherStudent = otherStudent.toDomain(),
        accepted = accepted,
        expiresAt = expiresAt,
        createdAt = createdAt,
        unreadCount = unreadCount,
        requester = requester
    )
}

fun MessageResponseDto.toDomain(): Message {
    return Message(
        id = id,
        content = content,
        sender = sender.toDomain(),
        read = read,
        createdAt = createdAt
    )
}