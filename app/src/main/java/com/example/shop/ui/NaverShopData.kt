package com.example.shop.ui

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
    val mallName: String,
    val productId: String
)
