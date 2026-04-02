package com.altankoc.beuverse.feature.notification.data.repository

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.notification.data.api.NotificationApi
import com.altankoc.beuverse.feature.notification.data.mapper.toDomain
import com.altankoc.beuverse.feature.notification.domain.model.Notification
import com.altankoc.beuverse.feature.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi
) : NotificationRepository {

    override fun getNotifications(page: Int, size: Int): Flow<Resource<PagedResponseDto<Notification>>> = flow {
        emit(Resource.Loading)
        try {
            val response = notificationApi.getNotifications(page, size)
            emit(Resource.Success(PagedResponseDto(
                content = response.content.map { it.toDomain() },
                totalElements = response.totalElements,
                totalPages = response.totalPages,
                number = response.number,
                size = response.size,
                first = response.first,
                last = response.last
            )))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun getUnreadCount(): Flow<Resource<Long>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(notificationApi.getUnreadCount()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun markAsRead(notificationId: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            notificationApi.markAsRead(notificationId)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun markAllAsRead(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            notificationApi.markAllAsRead()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }
}