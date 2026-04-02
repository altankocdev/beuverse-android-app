package com.altankoc.beuverse.feature.auth.data.repository

import com.altankoc.beuverse.core.datastore.TokenManager
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.auth.data.api.AuthApi
import com.altankoc.beuverse.feature.auth.data.dto.LoginRequestDto
import com.altankoc.beuverse.feature.auth.data.dto.RegisterRequestDto
import com.altankoc.beuverse.feature.auth.domain.model.AuthUser
import com.altankoc.beuverse.feature.auth.domain.repository.AuthRepository
import com.altankoc.beuverse.feature.profile.data.mapper.toDomain
import com.altankoc.beuverse.feature.profile.domain.model.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override fun login(request: LoginRequestDto): Flow<Resource<AuthUser>> = flow {
        emit(Resource.Loading)
        try {
            val response = authApi.login(request)
            tokenManager.saveToken(response.accessToken)
            tokenManager.saveUserInfo(
                id = response.student.id,
                username = response.student.username,
                email = response.student.email,
                role = response.student.role
            )
            emit(Resource.Success(
                AuthUser(
                    id = response.student.id,
                    username = response.student.username,
                    email = response.student.email,
                    role = response.student.role
                )
            ))
        } catch (e: HttpException) {
            val message = when (e.code()) {
                400, 401, 404 -> "error_invalid_credentials"
                409 -> "error_already_exists"
                500 -> "error_unknown"
                else -> "error_unknown"
            }
            emit(Resource.Error(message))
        } catch (e: Exception) {
            val message = when {
                e.message?.contains("Unable to resolve host") == true -> "error_network"
                e.message?.contains("Failed to connect") == true -> "error_network"
                else -> "error_unknown"
            }
            emit(Resource.Error(message))
        }
    }

    override fun register(request: RegisterRequestDto): Flow<Resource<Student>> = flow {
        emit(Resource.Loading)
        try {
            val response = authApi.register(request)
            emit(Resource.Success(response.toDomain()))
        } catch (e: HttpException) {
            val message = when (e.code()) {
                400, 409 -> "error_already_exists"
                500 -> "error_unknown"
                else -> "error_unknown"
            }
            emit(Resource.Error(message))
        } catch (e: Exception) {
            val message = when {
                e.message?.contains("Unable to resolve host") == true -> "error_network"
                e.message?.contains("Failed to connect") == true -> "error_network"
                else -> "error_unknown"
            }
            emit(Resource.Error(message))
        }
    }
}