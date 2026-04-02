package com.altankoc.beuverse.feature.post.domain.usecase

import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.post.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(postId: Long): Flow<Resource<Unit>> {
        return postRepository.deletePost(postId)
    }
}