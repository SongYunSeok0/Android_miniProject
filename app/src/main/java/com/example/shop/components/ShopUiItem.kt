package com.example.shop.ui.components

import com.example.shop.data.ProductEntity
import com.example.shop.ui.NaverShopItem

data class ShopUiItem(
    val title: String,
    val image: String,
    val lprice: String,
    val mallName: String
)

fun ProductEntity.toUi(): ShopUiItem =
    ShopUiItem(
        title = title,
        image = image,
        lprice = lprice,
        mallName = mallName
    )

fun NaverShopItem.toUi(): ShopUiItem =
    ShopUiItem(
        title = title,
        image = image,
        lprice = lprice,
        mallName = mallName
    )
