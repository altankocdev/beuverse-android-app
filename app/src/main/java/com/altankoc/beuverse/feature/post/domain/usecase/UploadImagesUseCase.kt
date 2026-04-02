package com.altankoc.beuverse.feature.post.domain.usecase

import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.post.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class UploadImagesUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(files: List<MultipartBody.Part>): Flow<Resource<List<String>>> {
        if (files.isEmpty()) {
            return flow { emit(Resource.Error("error_no_images")) }
        }
        if (files.size > 4) {
            return flow { emit(Resource.Error("error_max_images")) }
        }
        return postRepository.uploadImages(files)
    }
}