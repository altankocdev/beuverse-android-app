package com.altankoc.beuverse.feature.profile.domain.repository

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.profile.data.dto.StudentUpdateDto
import com.altankoc.beuverse.feature.profile.domain.model.Student
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface StudentRepository {
    fun getMe(): Flow<Resource<Student>>
    fun getStudentById(id: Long): Flow<Resource<Student>>
    fun getStudentByUsername(username: String): Flow<Resource<Student>>
    fun updateMe(request: StudentUpdateDto): Flow<Resource<Student>>
    fun deleteMe(): Flow<Resource<Unit>>
    fun searchStudents(keyword: String, page: Int, size: Int): Flow<Resource<PagedResponseDto<Student>>>
    fun uploadProfilePhoto(file: MultipartBody.Part): Flow<Resource<Student>>
}