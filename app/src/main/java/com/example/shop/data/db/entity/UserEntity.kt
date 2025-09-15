package com.example.shop.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true) 
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val username: String,
    val password: String = "",
    val status: String = "ACTIVE",
    val isAdmin: Boolean = false,
    val nickname: String = "",
    val email: String  
)