package com.altankoc.beuverse.feature.home.data.dto

data class LikeResponseDto(
    val id: Long,
    val postId: Long?,
    val commentId: Long?,
    val studentId: Long,
    val liked: Boolean
)