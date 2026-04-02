package com.altankoc.beuverse.feature.home.domain.model

import com.altankoc.beuverse.feature.profile.domain.model.StudentSummary

data class Comment(
    val id: Long,
    val content: String,
    val likeCount: Int,
    val replyCount: Int,
    val student: StudentSummary,
    val parentCommentId: Long?,
    val postId: Long?,
    val createdAt: String,
    val postOwnerUsername: String?,
    val isLiked: Boolean = false
)


