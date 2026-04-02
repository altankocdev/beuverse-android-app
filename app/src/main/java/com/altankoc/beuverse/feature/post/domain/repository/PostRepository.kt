package com.altankoc.beuverse.feature.post.domain.repository

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.domain.model.Post
import com.altankoc.beuverse.feature.post.data.dto.PostRequestDto
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface PostRepository {
    fun getFeed(page: Int, size: Int): Flow<Resource<PagedResponseDto<Post>>>
    fun getPostById(id: Long): Flow<Resource<Post>>
    fun getPostsByStudentId(studentId: Long, page: Int, size: Int): Flow<Resource<PagedResponseDto<Post>>>
    fun getPostsByTag(tag: String, page: Int, size: Int): Flow<Resource<PagedResponseDto<Post>>>
    fun searchPosts(keyword: String, page: Int, size: Int): Flow<Resource<PagedResponseDto<Post>>>
    fun createPost(request: PostRequestDto): Flow<Resource<Post>>
    fun updatePost(id: Long, request: PostRequestDto): Flow<Resource<Post>>
    fun deletePost(id: Long): Flow<Resource<Unit>>
    fun uploadImages(files: List<MultipartBody.Part>): Flow<Resource<List<String>>>
}