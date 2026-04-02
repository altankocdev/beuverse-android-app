package com.altankoc.beuverse.feature.home.data.api

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.feature.home.data.dto.CommentRequestDto
import com.altankoc.beuverse.feature.home.data.dto.CommentResponseDto
import retrofit2.http.*

interface CommentApi {

    @POST("comments")
    suspend fun createComment(
        @Body request: CommentRequestDto
    ): CommentResponseDto

    @GET("comments/{id}")
    suspend fun getCommentById(
        @Path("id") id: Long
    ): CommentResponseDto

    @GET("comments/post/{postId}")
    suspend fun getCommentsByPostId(
        @Path("postId") postId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedResponseDto<CommentResponseDto>

    @GET("comments/{commentId}/replies")
    suspend fun getRepliesByCommentId(
        @Path("commentId") commentId: Long
    ): List<CommentResponseDto>

    @GET("comments/student/{studentId}")
    suspend fun getCommentsByStudentId(
        @Path("studentId") studentId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedResponseDto<CommentResponseDto>

    @PUT("comments/{id}")
    suspend fun updateComment(
        @Path("id") id: Long,
        @Body request: CommentRequestDto
    ): CommentResponseDto

    @DELETE("comments/{id}")
    suspend fun deleteComment(
        @Path("id") id: Long
    )
}