package com.altankoc.beuverse.feature.notification.domain.usecase

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.notification.domain.model.Notification
import com.altankoc.beuverse.feature.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(page: Int = 0, size: Int = 20): Flow<Resource<PagedResponseDto<Notification>>> {
        return notificationRepository.getNotifications(page, size)
    }
}