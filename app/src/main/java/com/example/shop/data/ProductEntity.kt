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
    val hprice: String?,
    val mallName: String,
    val productType: Int?,
    val brand: String?,
    val maker: String?,
    val category1: String?,
    val category2: String?,
    val category3: String?,
    val category4: String?
)
