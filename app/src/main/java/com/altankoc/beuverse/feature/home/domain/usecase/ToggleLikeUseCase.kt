package com.altankoc.beuverse.feature.home.domain.usecase

import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.domain.repository.LikeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleLikeUseCase @Inject constructor(
    private val likeRepository: LikeRepository
) {
    operator fun invoke(postId: Long): Flow<Resource<Boolean>> {
        return likeRepository.togglePostLike(postId)
    }
}