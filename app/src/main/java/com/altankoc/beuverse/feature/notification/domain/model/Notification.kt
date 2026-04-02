package com.altankoc.beuverse.feature.notification.domain.model

import com.altankoc.beuverse.feature.profile.domain.model.StudentSummary

data class Notification(
    val id: Long,
    val type: String,
    val sender: StudentSummary,
    val postId: Long?,
    val commentId: Long?,
    val read: Boolean,
    val createdAt: String
)