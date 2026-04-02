package com.altankoc.beuverse.feature.profile.domain.usecase

import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.profile.data.dto.StudentUpdateDto
import com.altankoc.beuverse.feature.profile.domain.model.Student
import com.altankoc.beuverse.feature.profile.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateMeUseCase @Inject constructor(
    private val studentRepository: StudentRepository
) {
    operator fun invoke(
        username: String,
        bio: String?,
        profilePhotoUrl: String?
    ): Flow<Resource<Student>> {

        if (username.isBlank()) {
            return flow { emit(Resource.Error("error_field_required")) }
        }

        if (!username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            return flow { emit(Resource.Error("error_username_invalid")) }
        }

        if (username.length < 3 || username.length > 20) {
            return flow { emit(Resource.Error("error_username_invalid")) }
        }

        if (bio != null && bio.length > 160) {
            return flow { emit(Resource.Error("error_bio_too_long")) }
        }

        return studentRepository.updateMe(
            StudentUpdateDto(
                username = username,
                bio = bio,
                profilePhotoUrl = profilePhotoUrl
            )
        )
    }
}