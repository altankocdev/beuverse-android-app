package com.altankoc.beuverse.feature.notification.data.di

import com.altankoc.beuverse.feature.notification.data.api.NotificationApi
import com.altankoc.beuverse.feature.notification.data.repository.NotificationRepositoryImpl
import com.altankoc.beuverse.feature.notification.domain.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository

    companion object {
        @Provides
        @Singleton
        fun provideNotificationApi(retrofit: Retrofit): NotificationApi {
            return retrofit.create(NotificationApi::class.java)
        }
    }
}