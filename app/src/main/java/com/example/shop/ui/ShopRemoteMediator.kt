package com.example.shop.ui

import androidx.paging.*
import androidx.room.withTransaction
import com.example.shop.data.*
import kotlinx.coroutines.delay
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class ShopRemoteMediator(
    private val query: String,
    private val sort: String,
    private val db: ShopDatabase,
    private val api: NaverShopApi
) : RemoteMediator<Int, ProductEntity>() {

    private var attempt = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        val pageSize = state.config.pageSize

        val start = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val last = state.lastItemOrNull() ?: return MediatorResult.Success(true)
                val key = db.remoteKeyDao().key(last.productId) ?: return MediatorResult.Success(true)
                key.nextKey ?: return MediatorResult.Success(true)
            }
        }

        if (start > 1000) return MediatorResult.Success(true)

        delay(150L)

        return try {
            val resp = api.searchShop(
                query = query.trim(),
                sort = sort,
                start = start,
                display = pageSize
            )
            val items = resp.items.map { it.toEntity() }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().clear()
                    db.productDao().deleteAllExceptLiked()
                }
                db.productDao().upsertAll(items)

                val received = items.size
                val next = when {
                    received == 0 -> null
                    (start + received) > 1000 -> null
                    else -> start + received
                }
                val keys = items.map { RemoteKey(productId = it.productId, nextKey = next) }
                db.remoteKeyDao().upsertAll(keys)
            }

            attempt = 0

            val endReached = items.isEmpty() || (start + items.size) > 1000
            MediatorResult.Success(endOfPaginationReached = endReached)

        } catch (e: HttpException) {
            if (e.code() == 429) {
                attempt++
                val waitMs = (500L * (1 shl (attempt - 1))).coerceAtMost(8_000L) // 0.5s,1s,2s,4s,8s
                delay(waitMs)
                return load(loadType, state)
            }
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
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
        hprice = hprice,
        mallName = mallName,
        productType = productType,
        brand = brand,
        maker = maker,
        category1 = category1,
        category2 = category2,
        category3 = category3,
        category4 = category4
    )