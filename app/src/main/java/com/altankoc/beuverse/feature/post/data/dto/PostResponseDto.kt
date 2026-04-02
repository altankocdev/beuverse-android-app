package com.altankoc.beuverse.feature.post.data.dto

import com.altankoc.beuverse.feature.profile.data.dto.StudentSummaryDto

data class PostResponseDto(
    val id: Long,
    val content: String,
    val tag: String,
    val likeCount: Int,
    val commentCount: Int,
    val imageUrls: List<String>,
    val student: StudentSummaryDto,
    val isLiked: Boolean = false,
    val createdAt: String
)