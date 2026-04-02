package com.altankoc.beuverse.feature.auth.data.dto

data class RegisterRequestDto(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String,
    val department: String
)