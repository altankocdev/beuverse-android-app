package com.altankoc.beuverse.feature.home.domain.usecase

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.domain.model.Post
import com.altankoc.beuverse.feature.post.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsByTagUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(tag: String, page: Int = 0, size: Int = 10): Flow<Resource<PagedResponseDto<Post>>> {
        return postRepository.getPostsByTag(tag, page, size)
    }
}