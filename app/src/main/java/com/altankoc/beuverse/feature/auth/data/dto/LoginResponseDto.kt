package com.altankoc.beuverse.feature.auth.data.dto

data class LoginResponseDto(
    val accessToken: String,
    val tokenType: String,
    val student: StudentInfoDto
) {
    data class StudentInfoDto(
        val id: Long,
        val username: String,
        val email: String,
        val role: String
    )
}