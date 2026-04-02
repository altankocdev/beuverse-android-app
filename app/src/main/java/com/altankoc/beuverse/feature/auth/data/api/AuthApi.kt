package com.altankoc.beuverse.feature.auth.data.api

import com.altankoc.beuverse.feature.auth.data.dto.LoginRequestDto
import com.altankoc.beuverse.feature.auth.data.dto.LoginResponseDto
import com.altankoc.beuverse.feature.auth.data.dto.RegisterRequestDto
import com.altankoc.beuverse.feature.profile.data.dto.StudentResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): StudentResponseDto

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto
}