package com.altankoc.beuverse.feature.home.data.repository

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.data.api.LikeApi
import com.altankoc.beuverse.feature.home.data.mapper.toDomain
import com.altankoc.beuverse.feature.home.domain.model.Post
import com.altankoc.beuverse.feature.home.domain.repository.LikeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LikeRepositoryImpl @Inject constructor(
    private val likeApi: LikeApi
) : LikeRepository {

    override fun togglePostLike(postId: Long): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            val response = likeApi.togglePostLike(postId)
            emit(Resource.Success(response.liked))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun toggleCommentLike(commentId: Long): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            val response = likeApi.toggleCommentLike(commentId)
            emit(Resource.Success(response.liked))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun isPostLiked(postId: Long): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(likeApi.isPostLiked(postId)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun isCommentLiked(commentId: Long): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(likeApi.isCommentLiked(commentId)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun getLikedPostsByStudent(studentId: Long, page: Int, size: Int): Flow<Resource<PagedResponseDto<Post>>> = flow {
        emit(Resource.Loading)
        try {
            val response = likeApi.getLikedPostsByStudent(studentId, page, size)
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