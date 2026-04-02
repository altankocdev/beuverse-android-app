package com.altankoc.beuverse.feature.profile.data.dto

data class StudentResponseDto(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val bio: String?,
    val profilePhotoUrl: String?,
    val department: String,
    val createdAt: String
)