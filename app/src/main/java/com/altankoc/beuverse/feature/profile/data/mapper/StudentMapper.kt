package com.altankoc.beuverse.feature.profile.data.mapper

import com.altankoc.beuverse.feature.profile.data.dto.StudentResponseDto
import com.altankoc.beuverse.feature.profile.data.dto.StudentSummaryDto
import com.altankoc.beuverse.feature.profile.domain.model.Student
import com.altankoc.beuverse.feature.profile.domain.model.StudentSummary

fun StudentResponseDto.toDomain(): Student {
    return Student(
        id = id,
        firstName = firstName,
        lastName = lastName,
        username = username,
        email = email,
        bio = bio,
        profilePhotoUrl = profilePhotoUrl,
        department = department,
        createdAt = createdAt
    )
}

fun StudentSummaryDto.toDomain(): StudentSummary {
    return StudentSummary(
        id = id,
        firstName = firstName,
        lastName = lastName,
        username = username,
        profilePhotoUrl = profilePhotoUrl
    )
}