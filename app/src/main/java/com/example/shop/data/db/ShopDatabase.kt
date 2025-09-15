package com.example.shop.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shop.data.db.dao.LikeDao
import com.example.shop.data.db.dao.ProductDao
import com.example.shop.data.db.dao.RemoteKeyDao
import com.example.shop.data.db.dao.UserDao
import com.example.shop.data.db.entity.LikeEntity
import com.example.shop.data.db.entity.ProductEntity
import com.example.shop.data.db.entity.UserEntity
import com.example.shop.data.db.paging.RemoteKey

@Database(
    entities = [ProductEntity::class, UserEntity::class, LikeEntity::class, RemoteKey::class],
    version = 1,
    exportSchema = false
)
abstract class ShopDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun likeDao(): LikeDao
    abstract fun userDao(): UserDao
    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        @Volatile
        private var INSTANCE: ShopDatabase? = null
        fun get(context: Context): ShopDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ShopDatabase::class.java,
                    "shop.db"
                ).build().also { INSTANCE = it }
            }
    }
}
