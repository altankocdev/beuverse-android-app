package com.altankoc.beuverse.feature.auth.data.dto

data class LoginRequestDto(
    val usernameOrEmail: String,
    val password: String
)