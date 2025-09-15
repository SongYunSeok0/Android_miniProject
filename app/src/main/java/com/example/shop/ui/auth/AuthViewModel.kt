package com.example.shop.ui.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shop.data.db.ShopDatabase
import com.example.shop.data.db.entity.UserEntity
import com.example.shop.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val repo: UserRepository) : ViewModel() {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    val isLoggedIn: StateFlow<Boolean> =
        currentUser.map { it != null }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isAdmin: StateFlow<Boolean> =
        currentUser.map { it?.isAdmin == true }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val message = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch { _currentUser.value = repo.currentUser() }
    }

    fun bootstrap() {
        viewModelScope.launch { _currentUser.value = repo.currentUser() }
    }

    fun register(username: String, password: String, nickname: String, email: String) {
        viewModelScope.launch {
            message.value = null
            repo.register(username, password, nickname, email)
                .onSuccess { message.value = "회원가입 완료" }
                .onFailure { e -> message.value = e.message }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            message.value = null
            repo.login(username, password)
                .onSuccess { user ->
                    _currentUser.value = user
                    message.value = "로그인 성공"
                }
                .onFailure { e ->
                    message.value = e.message
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _currentUser.value = null
            message.value = "로그아웃 완료"
        }
    }

    fun updateProfile(newUsername: String, newPassword: String?, newNickname: String, newEmail: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repo.updateProfile(
                userId = user.id,
                newUsername = newUsername,
                newPassword = newPassword,
                newNickname = newNickname,
                newEmail = newEmail
            ).onSuccess {
                _currentUser.value = user.copy(username = newUsername, nickname = newNickname, email = newEmail)
                message.value = "저장되었습니다"
            }.onFailure { e -> message.value = e.message }
        }
    }

    fun refreshCurrentUserStatus() {
        viewModelScope.launch {
            val cur = _currentUser.value ?: return@launch
            val latest = repo.findUserById(cur.id)
            if (latest == null || latest.status == "DEACTIVATED") {
                logout()
            } else if (
                latest.status != cur.status ||
                latest.username != cur.username ||
                latest.nickname != cur.nickname ||
                latest.email != cur.email ||
                latest.isAdmin != cur.isAdmin
            ) {
                _currentUser.value = latest
            }
        }
    }

    fun refreshIfTargetAffected(targetUserId: Long) {
        val cur = _currentUser.value ?: return
        if (cur.id == targetUserId) {
            refreshCurrentUserStatus()
        }
    }

    fun observeAllUsers(): Flow<List<UserEntity>> = repo.observeAllUsers()

    suspend fun setUserStatus(userId: Long, status: String) {
        repo.setUserStatus(userId, status)
            .onSuccess { refreshIfTargetAffected(userId) }
            .onFailure { e -> message.value = e.message }
    }

    suspend fun deleteUser(user: UserEntity) {
        repo.deleteUser(user)
            .onSuccess { refreshIfTargetAffected(user.id) }
            .onFailure { e -> message.value = e.message }
    }

    companion object {
        fun factory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = ShopDatabase.get(app)
                    val repo = UserRepository(db.userDao())
                    if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return AuthViewModel(repo) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
                }
            }
    }
}
