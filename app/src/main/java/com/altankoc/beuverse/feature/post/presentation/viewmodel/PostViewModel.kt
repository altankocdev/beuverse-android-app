package com.altankoc.beuverse.feature.post.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.beuverse.core.datastore.TokenManager
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.domain.model.Comment
import com.altankoc.beuverse.feature.home.domain.model.Post
import com.altankoc.beuverse.feature.home.domain.repository.LikeRepository
import com.altankoc.beuverse.feature.home.domain.usecase.CreateCommentUseCase
import com.altankoc.beuverse.feature.home.domain.usecase.GetCommentsByPostUseCase
import com.altankoc.beuverse.feature.home.domain.usecase.ToggleLikeUseCase
import com.altankoc.beuverse.feature.post.domain.repository.PostRepository
import com.altankoc.beuverse.feature.post.domain.usecase.CreatePostUseCase
import com.altankoc.beuverse.feature.post.domain.usecase.DeletePostUseCase
import com.altankoc.beuverse.feature.post.domain.usecase.UploadImagesUseCase
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
class PostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    private val getCommentsByPostUseCase: GetCommentsByPostUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val postRepository: PostRepository,
    private val uploadImagesUseCase: UploadImagesUseCase,
    private val likeRepository: LikeRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _createPostState = MutableStateFlow<Resource<Post>?>(null)
    val createPostState: StateFlow<Resource<Post>?> = _createPostState.asStateFlow()

    private val _postState = MutableStateFlow<Resource<Post>?>(null)
    val postState: StateFlow<Resource<Post>?> = _postState.asStateFlow()

    private val _deletePostState = MutableStateFlow<Resource<Unit>?>(null)
    val deletePostState: StateFlow<Resource<Unit>?> = _deletePostState.asStateFlow()

    private val _commentsState = MutableStateFlow<Resource<List<Comment>>?>(null)
    val commentsState: StateFlow<Resource<List<Comment>>?> = _commentsState.asStateFlow()

    private val _createCommentState = MutableStateFlow<Resource<Comment>?>(null)
    val createCommentState: StateFlow<Resource<Comment>?> = _createCommentState.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _isLastPage = MutableStateFlow(false)
    val isLastPage: StateFlow<Boolean> = _isLastPage.asStateFlow()

    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    private val _uploadImagesState = MutableStateFlow<Resource<List<String>>?>(null)
    val uploadImagesState: StateFlow<Resource<List<String>>?> = _uploadImagesState.asStateFlow()

    private var currentPage = 0
    private val comments = mutableListOf<Comment>()

    init {
        viewModelScope.launch {
            _currentUserId.value = tokenManager.userId.first()
        }
    }

    fun loadPost(postId: Long) {
        postRepository.getPostById(postId).onEach { result ->
            _postState.value = result
        }.launchIn(viewModelScope)
    }

    fun createPost(content: String, tag: String, images: List<MultipartBody.Part> = emptyList()) {
        if (images.isEmpty()) {
            createPostUseCase(content, tag, emptyList()).onEach { result ->
                _createPostState.value = result
            }.launchIn(viewModelScope)
        } else {
            uploadImagesUseCase(images).onEach { result ->
                when (result) {
                    is Resource.Loading -> _createPostState.value = Resource.Loading
                    is Resource.Success -> {
                        createPostUseCase(content, tag, result.data).onEach { postResult ->
                            _createPostState.value = postResult
                        }.launchIn(viewModelScope)
                    }
                    is Resource.Error -> {
                        _createPostState.value = Resource.Error(result.message)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun deletePost(postId: Long) {
        deletePostUseCase(postId).onEach { result ->
            _deletePostState.value = result
        }.launchIn(viewModelScope)
    }

    fun loadComments(postId: Long, refresh: Boolean = false) {
        if (refresh) {
            currentPage = 0
            comments.clear()
            _isLastPage.value = false
        }

        if (_isLastPage.value) return

        getCommentsByPostUseCase(postId, currentPage).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    if (currentPage == 0) _commentsState.value = Resource.Loading
                    else _isLoadingMore.value = true
                }
                is Resource.Success -> {
                    _isLoadingMore.value = false
                    comments.addAll(result.data.content)
                    _commentsState.value = Resource.Success(comments.toList())
                    _isLastPage.value = result.data.last
                    if (!result.data.last) currentPage++
                }
                is Resource.Error -> {
                    _isLoadingMore.value = false
                    _commentsState.value = Resource.Error(result.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun loadMoreComments(postId: Long) {
        if (!_isLoadingMore.value && !_isLastPage.value) {
            loadComments(postId)
        }
    }

    fun createComment(postId: Long, content: String, parentCommentId: Long? = null) {
        createCommentUseCase(postId, content, parentCommentId).onEach { result ->
            if (result is Resource.Success) {
                comments.add(0, result.data)
                _commentsState.value = Resource.Success(comments.toList())
            }
            _createCommentState.value = result
        }.launchIn(viewModelScope)
    }

    fun toggleLike(postId: Long) {
        toggleLikeUseCase(postId).onEach { result ->
            if (result is Resource.Success) {
                val currentPost = (_postState.value as? Resource.Success)?.data
                if (currentPost != null) {
                    _postState.value = Resource.Success(
                        currentPost.copy(
                            isLiked = result.data,
                            likeCount = if (result.data) currentPost.likeCount + 1 else currentPost.likeCount - 1
                        )
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun toggleCommentLike(commentId: Long) {
        likeRepository.toggleCommentLike(commentId).onEach { result ->
            if (result is Resource.Success) {
                val updated = comments.map { comment ->
                    if (comment.id == commentId) comment.copy(
                        isLiked = result.data,
                        likeCount = if (result.data) comment.likeCount + 1 else comment.likeCount - 1
                    ) else comment
                }
                comments.clear()
                comments.addAll(updated)
                _commentsState.value = Resource.Success(comments.toList())
            }
        }.launchIn(viewModelScope)
    }

    fun uploadImages(files: List<MultipartBody.Part>) {
        uploadImagesUseCase(files).onEach { result ->
            _uploadImagesState.value = result
        }.launchIn(viewModelScope)
    }

    fun resetUploadImagesState() {
        _uploadImagesState.value = null
    }
}
