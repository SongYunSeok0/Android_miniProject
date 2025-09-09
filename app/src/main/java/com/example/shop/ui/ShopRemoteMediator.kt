package com.example.shop.ui

import androidx.paging.*
import androidx.room.withTransaction
import com.example.shop.data.*

@OptIn(ExperimentalPagingApi::class)
class ShopRemoteMediator(
    private val query: String,
    private val sort: String,
    private val db: ShopDatabase,
    private val api: NaverShopApi
) : RemoteMediator<Int, ProductEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        // Paging이 요청한 실제 pageSize 사용
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

        if (start > 1000) return MediatorResult.Success(true) // 네이버 제한

        return try {
            val resp = api.searchShop(
                query = query.trim(),
                sort = sort,
                start = start,
                display = pageSize           // ★ 여기 중요: 20개씩 요청
            )
            val items = resp.items.map { it.toEntity() }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().clear()
                    db.productDao().clearAll()
                }
                db.productDao().upsertAll(items)

                // 다음 start는 "이번에 실제로 받은 개수"만큼 증가
                val received = items.size
                val next = when {
                    received == 0 -> null
                    (start + received) > 1000 -> null
                    else -> start + received   // ★ items.size 기준
                }

                val keys = items.map { RemoteKey(productId = it.productId, nextKey = next) }
                db.remoteKeyDao().upsertAll(keys)
            }

            val endReached = items.isEmpty() || (start + items.size) > 1000
            MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}


// dto -> entity 변환이 여기 없으면 기존 위치 그대로 사용
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
