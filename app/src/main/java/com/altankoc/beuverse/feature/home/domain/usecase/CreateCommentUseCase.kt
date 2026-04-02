package com.altankoc.beuverse.feature.home.domain.usecase

import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.data.dto.CommentRequestDto
import com.altankoc.beuverse.feature.home.domain.model.Comment
import com.altankoc.beuverse.feature.home.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(postId: Long, content: String, parentCommentId: Long? = null): Flow<Resource<Comment>> {
        if (content.isBlank()) {
            return flow { emit(Resource.Error("error_field_required")) }
        }
        if (content.length > 300) {
            return flow { emit(Resource.Error("error_comment_too_long")) }
        }
        return commentRepository.createComment(
            CommentRequestDto(
                postId = postId,
                content = content,
                parentCommentId = parentCommentId
            )
        )
    }
}