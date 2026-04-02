package com.altankoc.beuverse.feature.profile.domain.model

data class StudentSummary(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val profilePhotoUrl: String?
) {
    val fullName: String get() = "$firstName $lastName"
    val initials: String get() = "${firstName.first()}${lastName.first()}".uppercase()
}