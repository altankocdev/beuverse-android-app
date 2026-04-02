package com.altankoc.beuverse.feature.messages.domain.model

import com.altankoc.beuverse.feature.profile.domain.model.StudentSummary

data class Message(
    val id: Long,
    val content: String,
    val sender: StudentSummary,
    val read: Boolean,
    val createdAt: String
)