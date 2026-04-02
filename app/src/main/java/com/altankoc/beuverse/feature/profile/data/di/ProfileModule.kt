package com.altankoc.beuverse.feature.profile.data.di

import com.altankoc.beuverse.feature.profile.data.api.StudentApi
import com.altankoc.beuverse.feature.profile.data.repository.StudentRepositoryImpl
import com.altankoc.beuverse.feature.profile.domain.repository.StudentRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindStudentRepository(
        studentRepositoryImpl: StudentRepositoryImpl
    ): StudentRepository

    companion object {
        @Provides
        @Singleton
        fun provideStudentApi(retrofit: Retrofit): StudentApi {
            return retrofit.create(StudentApi::class.java)
        }
    }
}