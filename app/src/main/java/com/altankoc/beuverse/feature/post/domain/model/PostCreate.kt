package com.altankoc.beuverse.feature.post.domain.model

data class PostCreate(
    val content: String,
    val tag: String,
    val imageUrls: List<String> = emptyList()
)