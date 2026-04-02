package com.altankoc.beuverse.feature.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.beuverse.core.datastore.TokenManager
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.domain.model.Comment
import com.altankoc.beuverse.feature.home.domain.model.Post
import com.altankoc.beuverse.feature.home.domain.repository.LikeRepository
import com.altankoc.beuverse.feature.profile.domain.model.Student
import com.altankoc.beuverse.feature.profile.domain.usecase.GetStudentByIdUseCase
import com.altankoc.beuverse.feature.profile.domain.usecase.GetStudentCommentsUseCase
import com.altankoc.beuverse.feature.profile.domain.usecase.GetStudentLikedPostsUseCase
import com.altankoc.beuverse.feature.profile.domain.usecase.GetStudentPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getStudentByIdUseCase: GetStudentByIdUseCase,
    private val getStudentPostsUseCase: GetStudentPostsUseCase,
    private val getStudentCommentsUseCase: GetStudentCommentsUseCase,
    private val getStudentLikedPostsUseCase: GetStudentLikedPostsUseCase,
    private val likeRepository: LikeRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _profileState = MutableStateFlow<Resource<Student>>(Resource.Loading)
    val profileState: StateFlow<Resource<Student>> = _profileState.asStateFlow()

    private val _postsState = MutableStateFlow<Resource<List<Post>>?>(null)
    val postsState: StateFlow<Resource<List<Post>>?> = _postsState.asStateFlow()

    private val _commentsState = MutableStateFlow<Resource<List<Comment>>?>(null)
    val commentsState: StateFlow<Resource<List<Comment>>?> = _commentsState.asStateFlow()

    private val _likedPostsState = MutableStateFlow<Resource<List<Post>>?>(null)
    val likedPostsState: StateFlow<Resource<List<Post>>?> = _likedPostsState.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _totalPosts = MutableStateFlow(0L)
    val totalPosts: StateFlow<Long> = _totalPosts.asStateFlow()

    private val _totalComments = MutableStateFlow(0L)
    val totalComments: StateFlow<Long> = _totalComments.asStateFlow()

    private val _totalLiked = MutableStateFlow(0L)
    val totalLiked: StateFlow<Long> = _totalLiked.asStateFlow()

    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    private var currentStudentId: Long? = null
    private var postsPage = 0
    private var commentsPage = 0
    private var likedPage = 0
    private val posts = mutableListOf<Post>()
    private val comments = mutableListOf<Comment>()
    private val likedPosts = mutableListOf<Post>()

    init {
        viewModelScope.launch {
            _currentUserId.value = tokenManager.userId.first()
        }
    }

    fun loadProfile(studentId: Long) {
        if (currentStudentId == studentId) return
        currentStudentId = studentId
        posts.clear()
        comments.clear()
        likedPosts.clear()
        postsPage = 0
        commentsPage = 0
        likedPage = 0
        _postsState.value = null
        _commentsState.value = null
        _likedPostsState.value = null
        _selectedTab.value = 0

        getStudentByIdUseCase(studentId).onEach { result ->
            _profileState.value = result
            if (result is Resource.Success) {
                loadPosts()
                loadComments()
                loadLikedPosts()
            }
        }.launchIn(viewModelScope)
    }

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    private fun loadPosts() {
        val studentId = currentStudentId ?: return
        getStudentPostsUseCase(studentId, postsPage).onEach { result ->
            when (result) {
                is Resource.Loading -> if (postsPage == 0) _postsState.value = Resource.Loading
                is Resource.Success -> {
                    posts.addAll(result.data.content)
                    _postsState.value = Resource.Success(posts.toList())
                    _totalPosts.value = result.data.totalElements
                    if (!result.data.last) postsPage++
                }
                is Resource.Error -> _postsState.value = Resource.Error(result.message)
            }
        }.launchIn(viewModelScope)
    }

    private fun loadComments() {
        val studentId = currentStudentId ?: return
        getStudentCommentsUseCase(studentId, commentsPage).onEach { result ->
            when (result) {
                is Resource.Loading -> if (commentsPage == 0) _commentsState.value = Resource.Loading
                is Resource.Success -> {
                    comments.addAll(result.data.content)
                    _commentsState.value = Resource.Success(comments.toList())
                    _totalComments.value = result.data.totalElements
                    if (!result.data.last) commentsPage++
                }
                is Resource.Error -> _commentsState.value = Resource.Error(result.message)
            }
        }.launchIn(viewModelScope)
    }

    private fun loadLikedPosts() {
        val studentId = currentStudentId ?: return
        getStudentLikedPostsUseCase(studentId, likedPage).onEach { result ->
            when (result) {
                is Resource.Loading -> if (likedPage == 0) _likedPostsState.value = Resource.Loading
                is Resource.Success -> {
                    likedPosts.addAll(result.data.content)
                    _likedPostsState.value = Resource.Success(likedPosts.toList())
                    _totalLiked.value = result.data.totalElements
                    if (!result.data.last) likedPage++
                }
                is Resource.Error -> _likedPostsState.value = Resource.Error(result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun toggleLikeOnPost(postId: Long) {
        likeRepository.togglePostLike(postId).onEach { result ->
            if (result is Resource.Success) {
                val updatedPosts = posts.map { post ->
                    if (post.id == postId) post.copy(
                        isLiked = result.data,
                        likeCount = if (result.data) post.likeCount + 1 else post.likeCount - 1
                    ) else post
                }
                posts.clear()
                posts.addAll(updatedPosts)
                _postsState.value = Resource.Success(posts.toList())
            }
        }.launchIn(viewModelScope)
    }
}