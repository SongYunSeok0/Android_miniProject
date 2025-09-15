package com.example.shop.data.db.paging

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey val productId: String,
    val nextKey: Int?
)
