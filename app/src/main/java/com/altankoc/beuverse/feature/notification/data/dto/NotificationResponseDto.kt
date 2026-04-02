package com.altankoc.beuverse.feature.notification.data.dto

import com.altankoc.beuverse.feature.profile.data.dto.StudentSummaryDto

data class NotificationResponseDto(
    val id: Long,
    val type: String,
    val sender: StudentSummaryDto,
    val postId: Long?,
    val commentId: Long?,
    val read: Boolean,
    val createdAt: String
)