package com.example.shop.data

class UserRepository(private val userDao: UserDao) {

    suspend fun register(username: String, password: String): Result<Long> {
        val exists = userDao.findByUsername(username)
        if (exists != null) return Result.failure(IllegalStateException("이미 존재하는 아이디입니다."))
        val id = userDao.insert(UserEntity(username = username, password = password))
        return Result.success(id)
    }

    suspend fun login(username: String, password: String): Result<UserEntity> {
        val user = userDao.login(username, password)
        return if (user != null) {
            userDao.logoutAll()
            userDao.updateStatus(user.id, true)
            Result.success(user.copy(status = true))
        } else {
            Result.failure(IllegalStateException("아이디 또는 비밀번호가 올바르지 않습니다."))
        }
    }

    suspend fun logout() {
        userDao.logoutAll()
    }

    suspend fun currentUser(): UserEntity? = userDao.getLoggedIn()
}