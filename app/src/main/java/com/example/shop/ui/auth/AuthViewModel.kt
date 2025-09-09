package com.example.shop.ui.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shop.data.ShopDatabase
import com.example.shop.data.UserEntity
import com.example.shop.data.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(private val repo: UserRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    val isLoggedIn: StateFlow<Boolean> =
        _currentUser.map { it != null }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isAdmin: StateFlow<Boolean> =
        _currentUser.map { it?.isAdmin == true }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun bootstrap() = viewModelScope.launch {
        _currentUser.value = repo.currentUser()
    }

    fun register(username: String, password: String, nickname: String, email: String) = viewModelScope.launch {
        _message.value = null
        val result = repo.register(username, password, nickname, email)
        _message.value = result.exceptionOrNull()?.message ?: "회원가입 완료!"
    }

    fun login(username: String, password: String) = viewModelScope.launch {
        _message.value = null
        val result = repo.login(username, password)
        result.onSuccess { user ->
            _currentUser.value = user.copy(status = "LOGGED_IN")
            _message.value = "로그인 성공"
        }.onFailure { e ->
            _message.value = e.message
        }
    }

    fun logout() = viewModelScope.launch {
        repo.logout()
        _currentUser.value = null
        _message.value = "로그아웃 완료"
    }

    fun updateProfile(newUsername: String, newPassword: String?, newNickname: String, newEmail: String) =
        viewModelScope.launch {
            val user = currentUser.value ?: run {
                _message.value = "로그인이 필요합니다"
                return@launch
            }
            repo.updateProfile(
                userId = user.id,
                newUsername = newUsername,
                newPassword = newPassword,
                newNickname = newNickname,
                newEmail = newEmail
            )
                .onSuccess {
                    _currentUser.value = user.copy(
                        username = newUsername,
                        nickname = newNickname,
                        email = newEmail
                    )
                    _message.value = "저장되었습니다"
                }
                .onFailure { _message.value = it.message }
        }

    companion object {
        fun factory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = ShopDatabase.get(app)
                    val repo = UserRepository(db.userDao())
                    return AuthViewModel(repo) as T
                }
            }
    }
}
