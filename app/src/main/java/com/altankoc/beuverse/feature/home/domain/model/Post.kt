package com.altankoc.beuverse.feature.home.domain.model

import com.altankoc.beuverse.feature.profile.domain.model.StudentSummary

data class Post(
    val id: Long,
    val content: String,
    val tag: String,
    val likeCount: Int,
    val commentCount: Int,
    val imageUrls: List<String>,
    val student: StudentSummary,
    val createdAt: String,
    val isLiked: Boolean = false
)