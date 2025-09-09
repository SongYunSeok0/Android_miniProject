package com.example.shop.ui

import androidx.paging.*
import androidx.room.withTransaction
import com.example.shop.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
class ShopRepository(
    private val api: NaverShopApi = ShopRetrofit.api,
    private val db: ShopDatabase,
    private val productDao: ProductDao,
    private val likeDao: LikeDao
) {
    fun pagingSearch(query: String, sort: String): Flow<PagingData<ProductEntity>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 3,
                enablePlaceholders = false
            ),
            remoteMediator = ShopRemoteMediator(
                query = query,
                sort = sort,
                db = db,
                api = api
            ),
            pagingSourceFactory = { productDao.pagingSource() }
        ).flow

    fun observeProducts() = productDao.observeAll()
    fun observeUserLikedProducts(userId: Long) = productDao.observeLikedProducts(userId)
    fun observeUserLikeSet(userId: Long) = likeDao.observeProductIds(userId).map { it.toSet() }

    suspend fun toggleLike(userId: Long, product: ProductEntity) {
        db.withTransaction {
            productDao.upsert(product)
            if (likeDao.isLiked(userId, product.productId)) {
                likeDao.delete(userId, product.productId)
            } else {
                likeDao.insert(LikeEntity(userId = userId, productId = product.productId))
            }
        }
    }
}