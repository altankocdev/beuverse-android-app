package com.altankoc.beuverse.feature.home.domain.usecase

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.domain.model.Comment
import com.altankoc.beuverse.feature.home.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCommentsByPostUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(postId: Long, page: Int = 0, size: Int = 10): Flow<Resource<PagedResponseDto<Comment>>> {
        return commentRepository.getCommentsByPostId(postId, page, size)
    }
}