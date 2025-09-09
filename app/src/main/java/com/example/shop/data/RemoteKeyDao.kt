package com.example.shop.data

import androidx.room.*

@Dao
interface RemoteKeyDao {
    @Query("SELECT * FROM remote_keys WHERE productId = :id LIMIT 1")
    suspend fun key(id: String): RemoteKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(keys: List<RemoteKey>)

    @Query("DELETE FROM remote_keys")
    suspend fun clear()
}
