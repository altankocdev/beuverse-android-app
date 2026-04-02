package com.altankoc.beuverse.feature.home.data.dto

import com.altankoc.beuverse.feature.profile.data.dto.StudentSummaryDto

data class CommentResponseDto(
    val id: Long,
    val content: String,
    val likeCount: Int,
    val replyCount: Int,
    val student: StudentSummaryDto,
    val parentCommentId: Long?,
    val postId: Long?,
    val createdAt: String,
    val postOwnerUsername: String?,
    val isLiked: Boolean = false
)