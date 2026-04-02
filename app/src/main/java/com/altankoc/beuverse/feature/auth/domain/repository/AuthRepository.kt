package com.altankoc.beuverse.feature.auth.domain.repository

import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.auth.data.dto.LoginRequestDto
import com.altankoc.beuverse.feature.auth.data.dto.RegisterRequestDto
import com.altankoc.beuverse.feature.auth.domain.model.AuthUser
import com.altankoc.beuverse.feature.profile.domain.model.Student
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(request: LoginRequestDto): Flow<Resource<AuthUser>>
    fun register(request: RegisterRequestDto): Flow<Resource<Student>>
}