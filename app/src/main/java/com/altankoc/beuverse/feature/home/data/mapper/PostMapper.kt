package com.altankoc.beuverse.feature.home.data.mapper

import com.altankoc.beuverse.feature.home.domain.model.Post
import com.altankoc.beuverse.feature.post.data.dto.PostResponseDto
import com.altankoc.beuverse.feature.profile.data.mapper.toDomain

fun PostResponseDto.toDomain(): Post {
    return Post(
        id = id,
        content = content,
        tag = tag,
        likeCount = likeCount,
        commentCount = commentCount,
        imageUrls = imageUrls,
        student = student.toDomain(),
        createdAt = createdAt,
        isLiked = isLiked
    )
}