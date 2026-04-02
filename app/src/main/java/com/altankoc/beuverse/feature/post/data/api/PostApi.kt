package com.altankoc.beuverse.feature.post.data.api

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.feature.post.data.dto.PostRequestDto
import com.altankoc.beuverse.feature.post.data.dto.PostResponseDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {

    @POST("posts")
    suspend fun createPost(
        @Body request: PostRequestDto
    ): PostResponseDto

    @GET("posts/{id}")
    suspend fun getPostById(
        @Path("id") id: Long
    ): PostResponseDto

    @GET("posts")
    suspend fun getAllPosts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedResponseDto<PostResponseDto>

    @GET("posts/student/{studentId}")
    suspend fun getPostsByStudentId(
        @Path("studentId") studentId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedResponseDto<PostResponseDto>

    @GET("posts/tag/{tag}")
    suspend fun getPostsByTag(
        @Path("tag") tag: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedResponseDto<PostResponseDto>

    @PUT("posts/{id}")
    suspend fun updatePost(
        @Path("id") id: Long,
        @Body request: PostRequestDto
    ): PostResponseDto

    @DELETE("posts/{id}")
    suspend fun deletePost(
        @Path("id") id: Long
    )

    @GET("posts/search")
    suspend fun searchPosts(
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedResponseDto<PostResponseDto>

    @Multipart
    @POST("posts/upload-images")
    suspend fun uploadImages(
        @Part files: List<MultipartBody.Part>
    ): List<String>
}