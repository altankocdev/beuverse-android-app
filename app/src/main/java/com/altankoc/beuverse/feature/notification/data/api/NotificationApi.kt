package com.altankoc.beuverse.feature.notification.data.api

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.feature.notification.data.dto.NotificationResponseDto
import retrofit2.http.*

interface NotificationApi {

    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PagedResponseDto<NotificationResponseDto>

    @GET("notifications/unread-count")
    suspend fun getUnreadCount(): Long

    @PUT("notifications/{notificationId}/read")
    suspend fun markAsRead(
        @Path("notificationId") notificationId: Long
    )

    @PUT("notifications/read-all")
    suspend fun markAllAsRead()
}