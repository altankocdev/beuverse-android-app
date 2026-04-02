package com.altankoc.beuverse.feature.messages.domain.model

import com.altankoc.beuverse.feature.profile.domain.model.StudentSummary

data class Conversation(
    val id: Long,
    val otherStudent: StudentSummary,
    val accepted: Boolean,
    val expiresAt: String?,
    val createdAt: String,
    val unreadCount: Long,
    val requester: Boolean = false
)