package com.altankoc.beuverse.feature.auth.domain.usecase

import com.altankoc.beuverse.core.datastore.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val tokenManager: TokenManager
) {
    operator fun invoke(): Flow<Unit> = flow {
        tokenManager.clearAll()
        emit(Unit)
    }
}