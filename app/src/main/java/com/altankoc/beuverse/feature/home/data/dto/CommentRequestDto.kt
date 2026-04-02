package com.altankoc.beuverse.feature.home.data.dto

data class CommentRequestDto(
    val postId: Long,
    val content: String,
    val parentCommentId: Long? = null
)