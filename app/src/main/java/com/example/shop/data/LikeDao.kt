package com.example.shop.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LikeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(like: LikeEntity): Long

    @Query("DELETE FROM likes WHERE userId = :userId AND productId = :productId")
    suspend fun delete(userId: Long, productId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE userId = :userId AND productId = :productId)")
    suspend fun isLiked(userId: Long, productId: String): Boolean

    @Query("""
        SELECT p.* 
        FROM products p 
        INNER JOIN likes l ON l.productId = p.productId 
        WHERE l.userId = :userId 
        ORDER BY l.createdAt DESC
    """)
    fun observeLikedProducts(userId: Long): Flow<List<ProductEntity>>
}