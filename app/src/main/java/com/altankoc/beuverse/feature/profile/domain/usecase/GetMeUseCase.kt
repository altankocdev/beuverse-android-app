package com.altankoc.beuverse.feature.profile.domain.usecase

import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.profile.domain.model.Student
import com.altankoc.beuverse.feature.profile.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMeUseCase @Inject constructor(
    private val studentRepository: StudentRepository
) {
    operator fun invoke(): Flow<Resource<Student>> {
        return studentRepository.getMe()
    }
}