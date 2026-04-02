package com.altankoc.beuverse.feature.messages.data.dto

import com.altankoc.beuverse.feature.profile.data.dto.StudentSummaryDto

data class ConversationResponseDto(
    val id: Long,
    val otherStudent: StudentSummaryDto,
    val accepted: Boolean,
    val expiresAt: String?,
    val createdAt: String,
    val unreadCount: Long,
    val requester: Boolean = false
)