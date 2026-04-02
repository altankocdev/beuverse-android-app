package com.altankoc.beuverse.feature.auth.domain.usecase

import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.auth.data.dto.LoginRequestDto
import com.altankoc.beuverse.feature.auth.domain.model.AuthUser
import com.altankoc.beuverse.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(usernameOrEmail: String, password: String): Flow<Resource<AuthUser>> {
        if (usernameOrEmail.isBlank()) {
            return flow { emit(Resource.Error("error_field_required")) }
        }
        if (password.isBlank()) {
            return flow { emit(Resource.Error("error_field_required")) }
        }
        return authRepository.login(LoginRequestDto(usernameOrEmail, password))
    }
}