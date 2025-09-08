package com.example.shop.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shop.data.ShopDatabase
import com.example.shop.data.UserEntity
import com.example.shop.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repo: UserRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun bootstrap() = viewModelScope.launch {
        _currentUser.value = repo.currentUser()
    }

    fun register(username: String, password: String) = viewModelScope.launch {
        _message.value = null
        val result = repo.register(username, password)
        _message.value = result.exceptionOrNull()?.message ?: "회원가입 완료!"
    }

    fun login(username: String, password: String) = viewModelScope.launch {
        _message.value = null
        val result = repo.login(username, password)
        result.onSuccess {
            _currentUser.value = it
            _message.value = "로그인 성공"
        }.onFailure {
            _message.value = it.message
        }
    }

    fun logout() = viewModelScope.launch {
        repo.logout()
        _currentUser.value = null
        _message.value = "로그아웃 완료"
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