package com.altankoc.beuverse.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.beuverse.core.datastore.TokenManager
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.auth.domain.usecase.LoginUseCase
import com.altankoc.beuverse.feature.auth.domain.usecase.LogoutUseCase
import com.altankoc.beuverse.feature.auth.domain.usecase.RegisterUseCase
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
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<Unit>?>(null)
    val loginState: StateFlow<Resource<Unit>?> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<Resource<Unit>?>(null)
    val registerState: StateFlow<Resource<Unit>?> = _registerState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isAuthChecked = MutableStateFlow(false)
    val isAuthChecked: StateFlow<Boolean> = _isAuthChecked.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val token = tokenManager.accessToken.first()
            _isLoggedIn.value = token != null
            _isAuthChecked.value = true
        }
    }

    fun login(usernameOrEmail: String, password: String) {
        loginUseCase(usernameOrEmail, password).onEach { result ->
            when (result) {
                is Resource.Loading -> _loginState.value = Resource.Loading
                is Resource.Success -> _loginState.value = Resource.Success(Unit)
                is Resource.Error -> _loginState.value = Resource.Error(result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String,
        passwordAgain: String,
        department: String
    ) {
        registerUseCase(firstName, lastName, username, email, password, passwordAgain, department)
            .onEach { result ->
                when (result) {
                    is Resource.Loading -> _registerState.value = Resource.Loading
                    is Resource.Success -> _registerState.value = Resource.Success(Unit)
                    is Resource.Error -> _registerState.value = Resource.Error(result.message)
                }
            }.launchIn(viewModelScope)
    }

    fun logout() {
        logoutUseCase().onEach {
            _isLoggedIn.value = false
        }.launchIn(viewModelScope)
    }
}