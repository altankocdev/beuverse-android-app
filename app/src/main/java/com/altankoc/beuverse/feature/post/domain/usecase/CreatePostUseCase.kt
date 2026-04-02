package com.altankoc.beuverse.feature.post.domain.usecase

import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.domain.model.Post
import com.altankoc.beuverse.feature.post.data.dto.PostRequestDto
import com.altankoc.beuverse.feature.post.domain.model.PostConstants
import com.altankoc.beuverse.feature.post.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(
        content: String,
        tag: String,
        imageUrls: List<String> = emptyList()
    ): Flow<Resource<Post>> {

        if (content.isBlank() && imageUrls.isEmpty()) {
            return flow { emit(Resource.Error("error_field_required")) }
        }

        if (content.length > PostConstants.MAX_CONTENT_LENGTH) {
            return flow { emit(Resource.Error("error_content_too_long")) }
        }

        if (tag.isBlank()) {
            return flow { emit(Resource.Error("error_field_required")) }
        }

        if (imageUrls.size > PostConstants.MAX_IMAGE_COUNT) {
            return flow { emit(Resource.Error("error_max_images")) }
        }

        return postRepository.createPost(
            PostRequestDto(
                content = content,
                tag = tag,
                imageUrls = imageUrls
            )
        )
    }
}