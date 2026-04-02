package com.altankoc.beuverse.feature.messages.data.dto

import com.altankoc.beuverse.feature.profile.data.dto.StudentSummaryDto

data class MessageResponseDto(
    val id: Long,
    val content: String,
    val sender: StudentSummaryDto,
    val read: Boolean,
    val createdAt: String
)