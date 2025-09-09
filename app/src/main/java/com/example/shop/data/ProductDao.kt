package com.example.shop.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Upsert
    suspend fun upsertAll(items: List<ProductEntity>)

    @Upsert
    suspend fun upsert(item: ProductEntity)

    @Query("SELECT * FROM products ORDER BY rowid DESC")
    fun observeAll(): Flow<List<ProductEntity>>

    @Query("""
        SELECT p.* FROM likes l
        JOIN products p ON p.productId = l.productId
        WHERE l.userId = :userId
        ORDER BY l.createdAt DESC
    """)
    fun observeLikedProducts(userId: Long): Flow<List<ProductEntity>>
}
