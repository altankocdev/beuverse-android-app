package com.altankoc.beuverse.feature.profile.data.dto

data class StudentUpdateDto(
    val username: String,
    val bio: String?,
    val profilePhotoUrl: String?
)