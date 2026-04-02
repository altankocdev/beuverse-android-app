package com.altankoc.beuverse.feature.post.data.repository

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.data.mapper.toDomain
import com.altankoc.beuverse.feature.home.domain.model.Post
import com.altankoc.beuverse.feature.post.data.api.PostApi
import com.altankoc.beuverse.feature.post.data.dto.PostRequestDto
import com.altankoc.beuverse.feature.post.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi
) : PostRepository {

    override fun getFeed(page: Int, size: Int): Flow<Resource<PagedResponseDto<Post>>> = flow {
        emit(Resource.Loading)
        try {
            val response = postApi.getAllPosts(page, size)
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

    override fun getPostById(id: Long): Flow<Resource<Post>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(postApi.getPostById(id).toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun getPostsByStudentId(studentId: Long, page: Int, size: Int): Flow<Resource<PagedResponseDto<Post>>> = flow {
        emit(Resource.Loading)
        try {
            val response = postApi.getPostsByStudentId(studentId, page, size)
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

    override fun getPostsByTag(tag: String, page: Int, size: Int): Flow<Resource<PagedResponseDto<Post>>> = flow {
        emit(Resource.Loading)
        try {
            val response = postApi.getPostsByTag(tag, page, size)
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

    override fun searchPosts(keyword: String, page: Int, size: Int): Flow<Resource<PagedResponseDto<Post>>> = flow {
        emit(Resource.Loading)
        try {
            val response = postApi.searchPosts(keyword, page, size)
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

    override fun createPost(request: PostRequestDto): Flow<Resource<Post>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(postApi.createPost(request).toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun updatePost(id: Long, request: PostRequestDto): Flow<Resource<Post>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(postApi.updatePost(id, request).toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }

    override fun deletePost(id: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            postApi.deletePost(id)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }
    override fun uploadImages(files: List<MultipartBody.Part>): Flow<Resource<List<String>>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(postApi.uploadImages(files)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "error_unknown"))
        }
    }
}