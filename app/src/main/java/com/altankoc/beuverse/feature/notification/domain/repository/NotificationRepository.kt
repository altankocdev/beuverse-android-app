package com.altankoc.beuverse.feature.notification.domain.repository

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.notification.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(page: Int, size: Int): Flow<Resource<PagedResponseDto<Notification>>>
    fun getUnreadCount(): Flow<Resource<Long>>
    fun markAsRead(notificationId: Long): Flow<Resource<Unit>>
    fun markAllAsRead(): Flow<Resource<Unit>>
}