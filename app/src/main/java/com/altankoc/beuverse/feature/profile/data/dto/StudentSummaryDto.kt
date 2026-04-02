package com.altankoc.beuverse.feature.profile.data.dto

data class StudentSummaryDto(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val profilePhotoUrl: String?
)