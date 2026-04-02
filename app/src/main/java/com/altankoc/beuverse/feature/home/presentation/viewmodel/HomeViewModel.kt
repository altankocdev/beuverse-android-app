package com.altankoc.beuverse.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.beuverse.core.datastore.TokenManager
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.domain.model.Post
import com.altankoc.beuverse.feature.home.domain.usecase.GetFeedUseCase
import com.altankoc.beuverse.feature.home.domain.usecase.GetPostsByTagUseCase
import com.altankoc.beuverse.feature.home.domain.usecase.SearchPostsUseCase
import com.altankoc.beuverse.feature.home.domain.usecase.ToggleLikeUseCase
import com.altankoc.beuverse.feature.post.domain.usecase.DeletePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase,
    private val getPostsByTagUseCase: GetPostsByTagUseCase,
    private val searchPostsUseCase: SearchPostsUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _feedState = MutableStateFlow<Resource<List<Post>>>(Resource.Loading)
    val feedState: StateFlow<Resource<List<Post>>> = _feedState.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _isLastPage = MutableStateFlow(false)
    val isLastPage: StateFlow<Boolean> = _isLastPage.asStateFlow()

    private val _selectedTag = MutableStateFlow("")
    val selectedTag: StateFlow<String> = _selectedTag.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    private var currentPage = 0
    private val posts = mutableListOf<Post>()

    init {
        viewModelScope.launch {
            _currentUserId.value = tokenManager.userId.first()
        }
        loadFeed()
        observeSearch()
    }

    fun loadFeed(refresh: Boolean = false) {
        if (refresh) {
            currentPage = 0
            posts.clear()
            _isLastPage.value = false
        }

        if (_isLastPage.value) return

        val tag = _selectedTag.value

        val flow = if (tag.isBlank() || tag == "All" || tag == "Tümü") {
            getFeedUseCase(currentPage)
        } else {
            getPostsByTagUseCase(tag, currentPage)
        }

        flow.onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    if (currentPage == 0) _feedState.value = Resource.Loading
                    else _isLoadingMore.value = true
                }
                is Resource.Success -> {
                    _isLoadingMore.value = false
                    posts.addAll(result.data.content)
                    _feedState.value = Resource.Success(posts.toList())
                    _isLastPage.value = result.data.last
                    if (!result.data.last) currentPage++
                }
                is Resource.Error -> {
                    _isLoadingMore.value = false
                    _feedState.value = Resource.Error(result.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun loadMore() {
        if (!_isLoadingMore.value && !_isLastPage.value) {
            loadFeed()
        }
    }

    fun selectTag(tag: String) {
        if (_selectedTag.value != tag) {
            _selectedTag.value = tag
            loadFeed(refresh = true)
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun observeSearch() {
        _searchQuery
            .debounce(2000)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .onEach { query ->
                currentPage = 0
                posts.clear()
                searchPostsUseCase(query, currentPage).onEach { result ->
                    when (result) {
                        is Resource.Loading -> _feedState.value = Resource.Loading
                        is Resource.Success -> {
                            posts.addAll(result.data.content)
                            _feedState.value = Resource.Success(posts.toList())
                            _isLastPage.value = result.data.last
                            if (!result.data.last) currentPage++
                        }
                        is Resource.Error -> _feedState.value = Resource.Error(result.message)
                    }
                }.launchIn(viewModelScope)
            }.launchIn(viewModelScope)
    }

    fun toggleLike(postId: Long) {
        toggleLikeUseCase(postId).onEach { result ->
            if (result is Resource.Success) {
                val updatedPosts = posts.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            isLiked = result.data,
                            likeCount = if (result.data) post.likeCount + 1 else post.likeCount - 1
                        )
                    } else post
                }
                posts.clear()
                posts.addAll(updatedPosts)
                _feedState.value = Resource.Success(posts.toList())
            }
        }.launchIn(viewModelScope)
    }

    fun deletePost(postId: Long) {
        deletePostUseCase(postId).onEach { result ->
            if (result is Resource.Success) {
                posts.removeAll { it.id == postId }
                _feedState.value = Resource.Success(posts.toList())
            }
        }.launchIn(viewModelScope)
    }
}