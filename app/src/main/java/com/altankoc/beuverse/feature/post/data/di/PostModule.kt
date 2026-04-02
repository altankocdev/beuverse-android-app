package com.altankoc.beuverse.feature.post.data.di

import com.altankoc.beuverse.feature.post.data.api.PostApi
import com.altankoc.beuverse.feature.post.data.repository.PostRepositoryImpl
import com.altankoc.beuverse.feature.post.domain.repository.PostRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PostModule {

    @Binds
    @Singleton
    abstract fun bindPostRepository(
        postRepositoryImpl: PostRepositoryImpl
    ): PostRepository

    companion object {
        @Provides
        @Singleton
        fun providePostApi(retrofit: Retrofit): PostApi {
            return retrofit.create(PostApi::class.java)
        }
    }
}