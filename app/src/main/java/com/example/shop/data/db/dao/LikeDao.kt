package com.example.shop.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shop.data.db.entity.LikeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LikeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(like: LikeEntity)

    @Query("DELETE FROM likes WHERE userId = :userId AND productId = :productId")
    suspend fun delete(userId: Long, productId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE userId = :userId AND productId = :productId)")
    suspend fun isLiked(userId: Long, productId: String): Boolean

    @Query("SELECT productId FROM likes WHERE userId = :userId")
    fun observeProductIds(userId: Long): Flow<List<String>>
}
