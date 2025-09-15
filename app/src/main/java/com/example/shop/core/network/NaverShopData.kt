package com.example.shop.core.network

data class NaverShopDto(
    val lastBuildDate: String,
    val total: Int,
    val start: Int,
    val display: Int,
    val items: List<NaverShopItem>
)

data class NaverShopItem(
    val title: String,
    val link: String,
    val image: String,
    val lprice: String,
    val hprice: String?,
    val mallName: String,
    val productId: String,
    val productType: Int?,
    val brand: String?,
    val maker: String?,
    val category1: String?,
    val category2: String?,
    val category3: String?,
    val category4: String?
)