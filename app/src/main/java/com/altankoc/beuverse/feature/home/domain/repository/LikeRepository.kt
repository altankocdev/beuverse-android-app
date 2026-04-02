package com.altankoc.beuverse.feature.home.domain.repository

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface LikeRepository {
    fun togglePostLike(postId: Long): Flow<Resource<Boolean>>
    fun toggleCommentLike(commentId: Long): Flow<Resource<Boolean>>
    fun isPostLiked(postId: Long): Flow<Resource<Boolean>>
    fun isCommentLiked(commentId: Long): Flow<Resource<Boolean>>
    fun getLikedPostsByStudent(studentId: Long, page: Int = 0, size: Int = 10): Flow<Resource<PagedResponseDto<Post>>>
}