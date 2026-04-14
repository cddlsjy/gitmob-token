package com.gitmob.android.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gitmob.android.api.ApiClient
import com.gitmob.android.auth.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle    : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val login: String) : LoginUiState()
    data class Error(val msg: String)     : LoginUiState()
}

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val tokenStorage = TokenStorage(app)

    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun loginWithToken(token: String) {
        viewModelScope.launch {
            _state.value = LoginUiState.Loading
            try {
                // 临时保存 token 用于验证
                tokenStorage.saveToken(token)
                ApiClient.rebuild()
                val user = ApiClient.api.getCurrentUser()
                // 保存用户信息到 TokenStorage
                tokenStorage.saveUser(user.login, user.name, user.email, user.avatarUrl)
                _state.value = LoginUiState.Success(user.login)
            } catch (e: Exception) {
                tokenStorage.clear()
                _state.value = LoginUiState.Error(e.message ?: "Token 无效")
            }
        }
    }

    fun clearError() {
        if (_state.value is LoginUiState.Error) {
            _state.value = LoginUiState.Idle
        }
    }
}