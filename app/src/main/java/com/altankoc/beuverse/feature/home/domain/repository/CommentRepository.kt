package com.altankoc.beuverse.feature.home.domain.repository

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.data.dto.CommentRequestDto
import com.altankoc.beuverse.feature.home.domain.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    fun createComment(request: CommentRequestDto): Flow<Resource<Comment>>
    fun getCommentsByPostId(postId: Long, page: Int, size: Int): Flow<Resource<PagedResponseDto<Comment>>>
    fun getRepliesByCommentId(commentId: Long): Flow<Resource<List<Comment>>>
    fun updateComment(id: Long, request: CommentRequestDto): Flow<Resource<Comment>>
    fun deleteComment(id: Long): Flow<Resource<Unit>>
    fun getCommentsByStudentId(studentId: Long, page: Int = 0, size: Int = 10): Flow<Resource<PagedResponseDto<Comment>>>
}