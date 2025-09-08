package com.example.shop.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ProductEntity::class, UserEntity::class, LikeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ShopDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun likeDao(): LikeDao
    abstract fun userDao() : UserDao

    companion object {
        @Volatile private var INSTANCE: ShopDatabase? = null
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
