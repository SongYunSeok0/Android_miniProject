package com.example.shop.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shop.data.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("""
        SELECT * FROM users
        WHERE username = :username AND password = :password
          AND status != 'DEACTIVATED'
        LIMIT 1
    """)
    suspend fun loginActivate(username: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE status = 'LOGGED_IN' LIMIT 1")
    suspend fun getLoggedIn(): UserEntity?

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun observeAll(): Flow<List<UserEntity>>

    @Query("UPDATE users SET status = :status WHERE id = :userId")
    suspend fun updateStatus(userId: Long, status: String)

    @Query("UPDATE users SET status = 'ACTIVE' WHERE status = 'LOGGED_IN'")
    suspend fun resetLoggedInToActive()

    @Query("UPDATE users SET username = :username WHERE id = :id")
    suspend fun updateUsername(id: Long, username: String): Int

    @Query("UPDATE users SET password = :password WHERE id = :id")
    suspend fun updatePassword(id: Long, password: String): Int

    @Query("UPDATE users SET nickname = :nickname WHERE id = :id")
    suspend fun updateNickname(id: Long, nickname: String): Int

    @Query("UPDATE users SET email = :email WHERE id = :id")
    suspend fun updateEmail(id: Long, email: String): Int

    @Query("""
        UPDATE users
        SET username = :username,
            password = COALESCE(:password, password),
            nickname = :nickname,
            email = :email
        WHERE id = :id
    """)
    suspend fun updateProfile(
        id: Long,
        username: String,
        password: String?,
        nickname: String,
        email: String
    ): Int

    @Query("SELECT COUNT(*) FROM users WHERE isAdmin = 1")
    suspend fun countAdmins(): Int

    @Query("UPDATE users SET status = 'LOGGED_IN' WHERE id = :id")
    suspend fun setLoggedIn(id: Long): Int

    @Query("UPDATE users SET status = 'ACTIVE' WHERE id = :id")
    suspend fun activate(id: Long): Int

    @Query("UPDATE users SET status = 'DEACTIVATED' WHERE id = :id")
    suspend fun deactivate(id: Long): Int

    @Delete
    suspend fun delete(user: UserEntity)
}