package com.altankoc.beuverse.feature.messages.data.di

import com.altankoc.beuverse.feature.messages.data.api.MessagingApi
import com.altankoc.beuverse.feature.messages.data.repository.MessagingRepositoryImpl
import com.altankoc.beuverse.feature.messages.domain.repository.MessagingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MessagingModule {

    @Binds
    @Singleton
    abstract fun bindMessagingRepository(
        impl: MessagingRepositoryImpl
    ): MessagingRepository

    companion object {
        @Provides
        @Singleton
        fun provideMessagingApi(retrofit: Retrofit): MessagingApi {
            return retrofit.create(MessagingApi::class.java)
        }
    }
}