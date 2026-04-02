package com.altankoc.beuverse.core.di

import com.altankoc.beuverse.core.datastore.TokenManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TokenManagerEntryPoint {
    fun tokenManager(): TokenManager
}