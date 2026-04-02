package com.altankoc.beuverse.feature.profile.domain.usecase

import com.altankoc.beuverse.core.utils.PagedResponseDto
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.domain.model.Post
import com.altankoc.beuverse.feature.home.domain.repository.LikeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStudentLikedPostsUseCase @Inject constructor(
    private val likeRepository: LikeRepository
) {
    operator fun invoke(studentId: Long, page: Int = 0, size: Int = 10): Flow<Resource<PagedResponseDto<Post>>> {
        return likeRepository.getLikedPostsByStudent(studentId, page, size)
    }
}