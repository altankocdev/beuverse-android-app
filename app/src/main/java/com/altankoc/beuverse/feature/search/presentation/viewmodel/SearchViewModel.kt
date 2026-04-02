package com.altankoc.beuverse.feature.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.beuverse.core.datastore.SearchHistoryItem
import com.altankoc.beuverse.core.datastore.SearchHistoryManager
import com.altankoc.beuverse.core.datastore.TokenManager
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.profile.domain.model.Student
import com.altankoc.beuverse.feature.profile.domain.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val searchHistoryManager: SearchHistoryManager,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchState = MutableStateFlow<Resource<List<Student>>?>(null)
    val searchState: StateFlow<Resource<List<Student>>?> = _searchState.asStateFlow()

    val searchHistory = tokenManager.userId.flatMapLatest { userId ->
        if (userId != null) {
            searchHistoryManager.getHistory(userId)
        } else {
            emptyFlow()
        }
    }

    init {
        observeSearch()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchState.value = null
        }
    }

    fun onStudentClick(student: Student) {
        viewModelScope.launch {
            val userId = tokenManager.userId.first() ?: return@launch
            searchHistoryManager.addToHistory(
                userId = userId,
                item = SearchHistoryItem(
                    id = student.id,
                    firstName = student.firstName,
                    lastName = student.lastName,
                    username = student.username,
                    bio = student.bio
                )
            )
        }
    }

    fun removeFromHistory(id: Long) {
        viewModelScope.launch {
            val userId = tokenManager.userId.first() ?: return@launch
            searchHistoryManager.removeFromHistory(userId, id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            val userId = tokenManager.userId.first() ?: return@launch
            searchHistoryManager.clearHistory(userId)
        }
    }

    private fun observeSearch() {
        _searchQuery
            .debounce(500)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .onEach { query ->
                studentRepository.searchStudents(query, 0, 20).onEach { result ->
                    _searchState.value = when (result) {
                        is Resource.Loading -> Resource.Loading
                        is Resource.Success -> Resource.Success(result.data.content)
                        is Resource.Error -> Resource.Error(result.message)
                    }
                }.launchIn(viewModelScope)
            }.launchIn(viewModelScope)
    }
}