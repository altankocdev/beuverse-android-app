package com.altankoc.beuverse.feature.post.data.dto

data class PostRequestDto(
    val content: String,
    val tag: String,
    val imageUrls: List<String> = emptyList()
)