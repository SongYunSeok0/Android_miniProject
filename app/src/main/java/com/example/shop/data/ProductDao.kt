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

    @Query("SELECT * FROM products ORDER BY productId ASC")
    fun pagingSource(): androidx.paging.PagingSource<Int, ProductEntity>
    
    @Query("DELETE FROM products")
    suspend fun clearAll()
}
