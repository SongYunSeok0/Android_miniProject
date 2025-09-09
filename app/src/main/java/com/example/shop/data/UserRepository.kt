package com.example.shop.data

class UserRepository(private val userDao: UserDao) {

    suspend fun register(username: String, password: String): Result<Long> = runCatching {
        val exists = userDao.findByUsername(username)
        require(exists == null) { "이미 존재하는 아이디입니다." }
        userDao.insert(
            UserEntity(
                username = username,
                password = password,
                status = "ACTIVE",
                isAdmin = false
            )
        )
    }

    suspend fun login(username: String, password: String): Result<UserEntity> = runCatching {
        val user = userDao.login(username, password)
        ?: error("아이디 또는 비밀번호가 올바르지 않습니다.")
        userDao.logoutAll()
        userDao.updateStatus(user.id, "LOGGED_IN")
        user.copy(status = "LOGGED_IN")
    }

    suspend fun logout(): Result<Unit> = runCatching {
        userDao.getLoggedIn()?.let { loggedIn ->
            userDao.updateStatus(loggedIn.id, "ACTIVE")
        }
    }

    suspend fun currentUser(): UserEntity? = userDao.getLoggedIn()

    suspend fun updateProfile(userId: Long, newUsername: String, newPassword: String?): Result<Unit> = runCatching {
        if (newUsername.isNotBlank()) userDao.updateUsername(userId, newUsername)
        if (!newPassword.isNullOrBlank()) userDao.updatePassword(userId, newPassword)
    }
}