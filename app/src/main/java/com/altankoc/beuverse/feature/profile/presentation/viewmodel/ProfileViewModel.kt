package com.altankoc.beuverse.feature.profile.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.beuverse.core.datastore.TokenManager
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.domain.model.Comment
import com.altankoc.beuverse.feature.home.domain.model.Post
import com.altankoc.beuverse.feature.home.domain.repository.LikeRepository
import com.altankoc.beuverse.feature.post.domain.usecase.DeletePostUseCase
import com.altankoc.beuverse.feature.profile.domain.model.Student
import com.altankoc.beuverse.feature.profile.domain.repository.StudentRepository
import com.altankoc.beuverse.feature.profile.domain.usecase.GetMeUseCase
import com.altankoc.beuverse.feature.profile.domain.usecase.GetStudentCommentsUseCase
import com.altankoc.beuverse.feature.profile.domain.usecase.GetStudentLikedPostsUseCase
import com.altankoc.beuverse.feature.profile.domain.usecase.GetStudentPostsUseCase
import com.altankoc.beuverse.feature.profile.domain.usecase.UpdateMeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMeUseCase: GetMeUseCase,
    private val updateMeUseCase: UpdateMeUseCase,
    private val getStudentPostsUseCase: GetStudentPostsUseCase,
    private val getStudentCommentsUseCase: GetStudentCommentsUseCase,
    private val getStudentLikedPostsUseCase: GetStudentLikedPostsUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val likeRepository: LikeRepository,
    private val tokenManager: TokenManager,
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<Resource<Student>>(Resource.Loading)
    val profileState: StateFlow<Resource<Student>> = _profileState.asStateFlow()

    private val _postsState = MutableStateFlow<Resource<List<Post>>?>(null)
    val postsState: StateFlow<Resource<List<Post>>?> = _postsState.asStateFlow()

    private val _commentsState = MutableStateFlow<Resource<List<Comment>>?>(null)
    val commentsState: StateFlow<Resource<List<Comment>>?> = _commentsState.asStateFlow()

    private val _likedPostsState = MutableStateFlow<Resource<List<Post>>?>(null)
    val likedPostsState: StateFlow<Resource<List<Post>>?> = _likedPostsState.asStateFlow()

    private val _updateState = MutableStateFlow<Resource<Student>?>(null)
    val updateState: StateFlow<Resource<Student>?> = _updateState.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _totalPosts = MutableStateFlow(0L)
    val totalPosts: StateFlow<Long> = _totalPosts.asStateFlow()

    private val _totalComments = MutableStateFlow(0L)
    val totalComments: StateFlow<Long> = _totalComments.asStateFlow()

    private val _totalLiked = MutableStateFlow(0L)
    val totalLiked: StateFlow<Long> = _totalLiked.asStateFlow()

    private val _deleteAccountState = MutableStateFlow<Resource<Unit>?>(null)
    val deleteAccountState: StateFlow<Resource<Unit>?> = _deleteAccountState.asStateFlow()

    private val _uploadPhotoState = MutableStateFlow<Resource<Student>?>(null)
    val uploadPhotoState: StateFlow<Resource<Student>?> = _uploadPhotoState.asStateFlow()


    private var currentStudentId: Long? = null
    private var postsPage = 0
    private var commentsPage = 0
    private var likedPage = 0
    private val posts = mutableListOf<Post>()
    private val comments = mutableListOf<Comment>()
    private val likedPosts = mutableListOf<Post>()

    init {
        refresh(showLoading = true)
    }

    fun refresh(showLoading: Boolean = false) {
        viewModelScope.launch {
            val userId = tokenManager.userId.first()
            currentStudentId = userId
            
            if (showLoading || _profileState.value !is Resource.Success) {
                _postsState.value = null
                _commentsState.value = null
                _likedPostsState.value = null
                postsPage = 0
                commentsPage = 0
                likedPage = 0
                posts.clear()
                comments.clear()
                likedPosts.clear()
            } else {
                postsPage = 0
                commentsPage = 0
                likedPage = 0
            }

            getMeUseCase().onEach { result ->
                if (!(result is Resource.Loading && _profileState.value is Resource.Success)) {
                    _profileState.value = result
                }
                
                if (result is Resource.Success) {
                    Log.d("Beuverse", "getMe success! URL: ${result.data.profilePhotoUrl}")
                    loadPosts()
                    loadComments()
                    loadLikedPosts()
                }
            }.launchIn(viewModelScope)
        }
    }

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    private fun loadPosts() {
        val studentId = currentStudentId ?: return
        getStudentPostsUseCase(studentId, postsPage).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    if (postsPage == 0 && _postsState.value == null) _postsState.value = Resource.Loading
                }
                is Resource.Success -> {
                    if (postsPage == 0) posts.clear()
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
                is Resource.Loading -> {
                    if (commentsPage == 0 && _commentsState.value == null) _commentsState.value = Resource.Loading
                }
                is Resource.Success -> {
                    if (commentsPage == 0) comments.clear()
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
                is Resource.Loading -> {
                    if (likedPage == 0 && _likedPostsState.value == null) _likedPostsState.value = Resource.Loading
                }
                is Resource.Success -> {
                    if (likedPage == 0) likedPosts.clear()
                    likedPosts.addAll(result.data.content)
                    _likedPostsState.value = Resource.Success(likedPosts.toList())
                    _totalLiked.value = result.data.totalElements
                    if (!result.data.last) likedPage++
                }
                is Resource.Error -> _likedPostsState.value = Resource.Error(result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun updateMe(username: String, bio: String?, profilePhotoUrl: String?) {
        updateMeUseCase(username, bio, profilePhotoUrl).onEach { result ->
            _updateState.value = result
            if (result is Resource.Success) {
                refresh(showLoading = true)
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

                val updatedLiked = likedPosts.map { post ->
                    if (post.id == postId) post.copy(
                        isLiked = result.data,
                        likeCount = if (result.data) post.likeCount + 1 else post.likeCount - 1
                    ) else post
                }
                likedPosts.clear()
                likedPosts.addAll(updatedLiked)
                _likedPostsState.value = Resource.Success(likedPosts.toList())
            }
        }.launchIn(viewModelScope)
    }

    fun deletePost(postId: Long) {
        deletePostUseCase(postId).onEach { result ->
            if (result is Resource.Success) {
                posts.removeAll { it.id == postId }
                likedPosts.removeAll { it.id == postId }
                _postsState.value = Resource.Success(posts.toList())
                _likedPostsState.value = Resource.Success(likedPosts.toList())
                _totalPosts.value = (totalPosts.value - 1).coerceAtLeast(0)
            }
        }.launchIn(viewModelScope)
    }

    fun deleteAccount() {
        studentRepository.deleteMe().onEach { result ->
            _deleteAccountState.value = result
            if (result is Resource.Success) {
                tokenManager.clearAll()
            }
        }.launchIn(viewModelScope)
    }

    fun uploadProfilePhoto(file: MultipartBody.Part) {
        Log.d("Beuverse", "uploadProfilePhoto called in ViewModel")
        studentRepository.uploadProfilePhoto(file).onEach { result ->
            _uploadPhotoState.value = result
            if (result is Resource.Success) {
                Log.d("Beuverse", "uploadProfilePhoto Success! New Photo URL: ${result.data.profilePhotoUrl}")
                _profileState.value = Resource.Success(result.data)
            } else if (result is Resource.Error) {
                Log.e("Beuverse", "uploadProfilePhoto Error: ${result.message}")
            }
        }.launchIn(viewModelScope)
    }
}
