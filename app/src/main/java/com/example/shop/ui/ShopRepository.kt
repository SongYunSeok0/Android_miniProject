package com.example.shop.ui

import androidx.room.withTransaction
import com.example.shop.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShopRepository(
    private val api: NaverShopApi = ShopRetrofit.api,
    private val db: ShopDatabase, 
    private val productDao: ProductDao,
    private val likeDao: LikeDao
) {
    fun observeProducts(): Flow<List<ProductEntity>> = productDao.observeAll()

    fun observeUserLikedProducts(userId: Long): Flow<List<ProductEntity>> =
        productDao.observeLikedProducts(userId)

    fun observeUserLikeSet(userId: Long): Flow<Set<String>> =
        likeDao.observeProductIds(userId).map { it.toSet() }

    suspend fun searchAndCache(query: String, sort: String = "sim"): List<String> {
        val q = query.trim()
        if (q.isEmpty()) return emptyList()
        val resp = api.searchShop(query = q, sort = sort)
        val entities = resp.items.map { it.toEntity() }
        productDao.upsertAll(entities)
        
        return entities.map { it.productId }
    }

    suspend fun toggleLike(userId: Long, product: ProductEntity) {
        db.withTransaction{
            productDao.upsert(product)
            if (likeDao.isLiked(userId, product.productId)) {
                likeDao.delete(userId, product.productId)
            } else {
                likeDao.insert(LikeEntity(userId = userId, productId = product.productId))
            }
        }
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
