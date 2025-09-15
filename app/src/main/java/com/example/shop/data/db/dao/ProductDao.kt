package com.example.shop.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.shop.data.db.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Upsert
    suspend fun upsertAll(items: List<ProductEntity>)

    @Upsert
    suspend fun upsert(item: ProductEntity)

    @Query("SELECT * FROM products ORDER BY rowid DESC")
    fun observeAll(): Flow<List<ProductEntity>>

    @Query(
        """
        SELECT p.* FROM likes l
        JOIN products p ON p.productId = l.productId
        WHERE l.userId = :userId
        ORDER BY l.createdAt DESC
    """
    )
    fun observeLikedProducts(userId: Long): Flow<List<ProductEntity>>

    @Query(
        """
        SELECT * FROM products 
        WHERE title LIKE :kw ESCAPE '\'
           OR mallName LIKE :kw ESCAPE '\'
        ORDER BY rowid
    """
    )
    fun pagingSourceByKeyword(kw: String): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM products ORDER BY productId ASC")
    fun pagingSource(): PagingSource<Int, ProductEntity>

    @Query("DELETE FROM products WHERE productId NOT IN (SELECT productId FROM likes)")
    suspend fun deleteAllExceptLiked()

    @Query("DELETE FROM products")
    suspend fun clearAll()
}
