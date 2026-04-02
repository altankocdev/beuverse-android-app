package com.altankoc.beuverse.feature.auth.domain.model

data class AuthUser(
    val id: Long,
    val username: String,
    val email: String,
    val role: String
)