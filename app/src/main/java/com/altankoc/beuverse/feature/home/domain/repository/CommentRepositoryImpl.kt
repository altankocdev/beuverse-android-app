package com.altankoc.beuverse.feature.home.data.repository

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.data.api.CommentApi
import com.altankoc.beuverse.feature.home.data.dto.CommentRequestDto
import com.altankoc.beuverse.feature.home.data.mapper.toDomain
import com.altankoc.beuverse.feature.home.domain.model.Comment
import com.altankoc.beuverse.feature.home.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentApi: CommentApi
) : CommentRepository {

    override fun createComment(request: CommentRequestDto): Flow<Resource<Comment>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(commentApi.createComment(request).toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun getCommentsByPostId(postId: Long, page: Int, size: Int): Flow<Resource<PagedResponseDto<Comment>>> = flow {
        emit(Resource.Loading)
        try {
            val response = commentApi.getCommentsByPostId(postId, page, size)
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

    override fun getRepliesByCommentId(commentId: Long): Flow<Resource<List<Comment>>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(commentApi.getRepliesByCommentId(commentId).map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun updateComment(id: Long, request: CommentRequestDto): Flow<Resource<Comment>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(commentApi.updateComment(id, request).toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun deleteComment(id: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            commentApi.deleteComment(id)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun getCommentsByStudentId(studentId: Long, page: Int, size: Int): Flow<Resource<PagedResponseDto<Comment>>> = flow {
        emit(Resource.Loading)
        try {
            val response = commentApi.getCommentsByStudentId(studentId, page, size)
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
}