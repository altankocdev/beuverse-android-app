package com.altankoc.beuverse.feature.notification.domain.usecase

import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MarkAsReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(notificationId: Long? = null): Flow<Resource<Unit>> {
        return if (notificationId != null) {
            notificationRepository.markAsRead(notificationId)
        } else {
            notificationRepository.markAllAsRead()
        }
    }
}