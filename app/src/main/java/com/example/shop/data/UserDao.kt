package com.example.shop.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): UserEntity?

    @Query("UPDATE users SET status = :status WHERE id = :userId")
    suspend fun updateStatus(userId: Long, status: Boolean)

    @Query("UPDATE users SET status = 0")
    suspend fun logoutAll()

    @Query("SELECT * FROM users WHERE status = 1 LIMIT 1")
    suspend fun getLoggedIn(): UserEntity?

    @Query("UPDATE users SET username = :username WHERE id = :id")
    suspend fun updateUsername(id: Long, username: String): Int
    
    @Query("UPDATE users SET password = :password WHERE id = :id")
    suspend fun updatePassword(id: Long, password: String): Int
}
