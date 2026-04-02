package com.altankoc.beuverse.feature.profile.data.api

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.feature.profile.data.dto.StudentResponseDto
import com.altankoc.beuverse.feature.profile.data.dto.StudentUpdateDto
import okhttp3.MultipartBody
import retrofit2.http.*

interface StudentApi {

    @GET("students")
    suspend fun getAllStudents(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedResponseDto<StudentResponseDto>

    @GET("students/{id}")
    suspend fun getStudentById(
        @Path("id") id: Long
    ): StudentResponseDto

    @GET("students/username/{username}")
    suspend fun getStudentByUsername(
        @Path("username") username: String
    ): StudentResponseDto

    @GET("students/me")
    suspend fun getMe(): StudentResponseDto

    @PUT("students/me")
    suspend fun updateMe(
        @Body request: StudentUpdateDto
    ): StudentResponseDto

    @DELETE("students/me")
    suspend fun deleteMe()

    @GET("students/search")
    suspend fun searchStudents(
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedResponseDto<StudentResponseDto>

    @Multipart
    @PUT("students/me/profile-photo")
    suspend fun uploadProfilePhoto(
        @Part file: MultipartBody.Part
    ): StudentResponseDto
}