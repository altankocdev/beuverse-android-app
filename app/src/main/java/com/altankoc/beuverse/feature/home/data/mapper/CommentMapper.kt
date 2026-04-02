package com.altankoc.beuverse.feature.home.data.mapper

import com.altankoc.beuverse.feature.home.data.dto.CommentResponseDto
import com.altankoc.beuverse.feature.home.domain.model.Comment
import com.altankoc.beuverse.feature.profile.data.mapper.toDomain

fun CommentResponseDto.toDomain(): Comment {
    return Comment(
        id = id,
        content = content,
        likeCount = likeCount,
        replyCount = replyCount,
        student = student.toDomain(),
        parentCommentId = parentCommentId,
        postId = postId,
        createdAt = createdAt,
        postOwnerUsername = postOwnerUsername,
        isLiked = isLiked
    )
}