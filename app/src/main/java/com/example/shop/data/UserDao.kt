package com.example.shop.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): UserEntity?

    @Query("UPDATE users SET status = :status WHERE id = :userId")
    suspend fun updateStatus(userId: Long, status: String)

    @Query("UPDATE users SET status = 'ACTIVE'")
    suspend fun logoutAll()

    @Query("SELECT * FROM users WHERE status = 'LOGGED_IN' LIMIT 1")
    suspend fun getLoggedIn(): UserEntity?

    @Query("UPDATE users SET username = :username WHERE id = :id")
    suspend fun updateUsername(id: Long, username: String): Int
    
    @Query("UPDATE users SET password = :password WHERE id = :id")
    suspend fun updatePassword(id: Long, password: String): Int

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun observeAll(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users WHERE isAdmin = 1")
    suspend fun countAdmins(): Int

    @Delete
    suspend fun delete(user: UserEntity)
}
