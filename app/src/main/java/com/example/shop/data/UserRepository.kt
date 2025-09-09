package com.example.shop.data

class UserRepository(private val userDao: UserDao) {

    suspend fun register(
        username: String,
        password: String,
        nickname: String,
        email: String
    ): Result<Long> = runCatching {
        val u = username.trim()
        val e = email.trim()
        val n = nickname.trim()

        require(u.isNotBlank()) { "아이디를 입력하세요." }
        require(password.isNotBlank()) { "비밀번호를 입력하세요." }
        require(n.isNotBlank()) { "닉네임을 입력하세요." }
        require(e.isNotBlank()) { "이메일을 입력하세요." }

        require(userDao.findByUsername(u) == null) { "이미 존재하는 아이디입니다." }
        require(userDao.findByEmail(e) == null) { "이미 사용 중인 이메일입니다." }

        userDao.insert(
            UserEntity(
                username = u,
                password = password,
                nickname = n,
                email = e,
                status = "ACTIVE",
                isAdmin = false
            )
        )
    }

    suspend fun login(username: String, password: String): Result<UserEntity> = runCatching {
        val user = userDao.login(username.trim(), password)
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

    suspend fun updateProfile(
        userId: Long,
        newUsername: String,
        newPassword: String?,
        newNickname: String,
        newEmail: String
    ): Result<Unit> = runCatching {
        val u = newUsername.trim()
        val e = newEmail.trim()
        val n = newNickname.trim()

        require(u.isNotBlank()) { "아이디를 입력하세요." }
        require(n.isNotBlank()) { "닉네임을 입력하세요." }
        require(e.isNotBlank()) { "이메일을 입력하세요." }

        val existingByEmail = userDao.findByEmail(e)
        if (existingByEmail != null && existingByEmail.id != userId) {
            error("이미 사용 중인 이메일입니다.")
        }

        val existingByUsername = userDao.findByUsername(u)
        if (existingByUsername != null && existingByUsername.id != userId) {
            error("이미 존재하는 아이디입니다.")
        }

        userDao.updateProfile(
            id = userId,
            username = u,
            password = newPassword,
            nickname = n,
            email = e
        )
        Unit
    }
    suspend fun updateNickname(userId: Long, nickname: String): Result<Unit> = runCatching {
        val n = nickname.trim()
        require(n.isNotBlank()) { "닉네임을 입력하세요." }
        userDao.updateNickname(userId, n)
        Unit
    }

    suspend fun updateEmail(userId: Long, email: String): Result<Unit> = runCatching {
        val e = email.trim()
        require(e.isNotBlank()) { "이메일을 입력하세요." }
        val existing = userDao.findByEmail(e)
        if (existing != null && existing.id != userId) {
            error("이미 사용 중인 이메일입니다.")
        }
        userDao.updateEmail(userId, e)
        Unit
    }
}
