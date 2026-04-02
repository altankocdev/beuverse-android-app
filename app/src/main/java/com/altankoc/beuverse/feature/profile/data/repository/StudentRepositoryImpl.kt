package com.altankoc.beuverse.feature.profile.data.repository

import android.util.Log
import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.profile.data.api.StudentApi
import com.altankoc.beuverse.feature.profile.data.dto.StudentUpdateDto
import com.altankoc.beuverse.feature.profile.data.mapper.toDomain
import com.altankoc.beuverse.feature.profile.domain.model.Student
import com.altankoc.beuverse.feature.profile.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val studentApi: StudentApi
) : StudentRepository {

    override fun getMe(): Flow<Resource<Student>> = flow {
        emit(Resource.Loading)
        try {
            val response = studentApi.getMe()
            Log.d("Beuverse", "getMe success: ${response.profilePhotoUrl}")
            emit(Resource.Success(response.toDomain()))
        } catch (e: Exception) {
            Log.e("Beuverse", "getMe error: ${e.message}")
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun getStudentById(id: Long): Flow<Resource<Student>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(studentApi.getStudentById(id).toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun getStudentByUsername(username: String): Flow<Resource<Student>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(studentApi.getStudentByUsername(username).toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun updateMe(request: StudentUpdateDto): Flow<Resource<Student>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(studentApi.updateMe(request).toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun deleteMe(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            studentApi.deleteMe()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun searchStudents(keyword: String, page: Int, size: Int): Flow<Resource<PagedResponseDto<Student>>> = flow {
        emit(Resource.Loading)
        try {
            val response = studentApi.searchStudents(keyword, page, size)
            emit(Resource.Success(PagedResponseDto(
                content = response.content.map { it.toDomain() },
                totalElements = response.totalElements,
                totalPages = response.totalPages,
                number = response.number,
                size = response.size,
                first = response.first,
                last = response.last
            )))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun uploadProfilePhoto(file: MultipartBody.Part): Flow<Resource<Student>> = flow {
        emit(Resource.Loading)
        try {
            Log.d("Beuverse", "Uploading profile photo...")
            val response = studentApi.uploadProfilePhoto(file)
            Log.d("Beuverse", "Upload success! New URL: ${response.profilePhotoUrl}")
            emit(Resource.Success(response.toDomain()))
        } catch (e: Exception) {
            Log.e("Beuverse", "Upload failed: ${e.message}")
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }
}