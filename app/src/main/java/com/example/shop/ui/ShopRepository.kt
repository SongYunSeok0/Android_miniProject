package com.example.shop.ui

import com.example.shop.data.ProductDao
import com.example.shop.data.ProductEntity

class ShopRepository(
    private val api: NaverShopApi = ShopRetrofit.api,
    private val dao: ProductDao
) {
    fun observeProducts() = dao.observeAll()

    suspend fun searchAndCache(query: String) {
        val resp = api.searchShop(query = query)
        val entities = resp.items.map { it.toEntity() }
        dao.upsertAll(entities)
    }
}

private fun String.stripBold() = replace("<b>", "").replace("</b>", "")

private fun NaverShopItem.toEntity(): ProductEntity =
    ProductEntity(
        productId = productId,
        title = title.stripBold(),
        link = link,
        image = image,
        lprice = lprice,
        mallName = mallName
    )
