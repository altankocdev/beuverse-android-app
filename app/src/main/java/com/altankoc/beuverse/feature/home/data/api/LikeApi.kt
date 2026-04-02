package com.altankoc.beuverse.feature.home.data.api

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.feature.home.data.dto.LikeResponseDto
import com.altankoc.beuverse.feature.post.data.dto.PostResponseDto
import retrofit2.http.*

interface LikeApi {

    @POST("likes/post/{postId}")
    suspend fun togglePostLike(
        @Path("postId") postId: Long
    ): LikeResponseDto

    @POST("likes/comment/{commentId}")
    suspend fun toggleCommentLike(
        @Path("commentId") commentId: Long
    ): LikeResponseDto

    @GET("likes/post/{postId}")
    suspend fun isPostLiked(
        @Path("postId") postId: Long
    ): Boolean

    @GET("likes/comment/{commentId}")
    suspend fun isCommentLiked(
        @Path("commentId") commentId: Long
    ): Boolean

    @GET("likes/student/{studentId}/posts")
    suspend fun getLikedPostsByStudent(
        @Path("studentId") studentId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedResponseDto<PostResponseDto>
}