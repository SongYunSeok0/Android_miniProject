package com.example.shop.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val productId: String,
    val title: String,
    val link: String,
    val image: String,
    val lprice: String,
    val mallName: String
)
