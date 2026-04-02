package com.altankoc.beuverse.feature.home.data.di

import com.altankoc.beuverse.feature.home.data.api.CommentApi
import com.altankoc.beuverse.feature.home.data.api.LikeApi
import com.altankoc.beuverse.feature.home.data.repository.CommentRepositoryImpl
import com.altankoc.beuverse.feature.home.data.repository.LikeRepositoryImpl
import com.altankoc.beuverse.feature.home.domain.repository.CommentRepository
import com.altankoc.beuverse.feature.home.domain.repository.LikeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds
    @Singleton
    abstract fun bindCommentRepository(
        commentRepositoryImpl: CommentRepositoryImpl
    ): CommentRepository

    @Binds
    @Singleton
    abstract fun bindLikeRepository(
        likeRepositoryImpl: LikeRepositoryImpl
    ): LikeRepository

    companion object {
        @Provides
        @Singleton
        fun provideCommentApi(retrofit: Retrofit): CommentApi {
            return retrofit.create(CommentApi::class.java)
        }

        @Provides
        @Singleton
        fun provideLikeApi(retrofit: Retrofit): LikeApi {
            return retrofit.create(LikeApi::class.java)
        }
    }
}