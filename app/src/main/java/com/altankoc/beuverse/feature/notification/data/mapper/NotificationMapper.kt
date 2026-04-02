package com.altankoc.beuverse.feature.notification.data.mapper

import com.altankoc.beuverse.feature.notification.data.dto.NotificationResponseDto
import com.altankoc.beuverse.feature.notification.domain.model.Notification
import com.altankoc.beuverse.feature.profile.data.mapper.toDomain

fun NotificationResponseDto.toDomain(): Notification {
    return Notification(
        id = id,
        type = type,
        sender = sender.toDomain(),
        postId = postId,
        commentId = commentId,
        read = read,
        createdAt = createdAt
    )
}