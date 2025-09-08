package com.example.shop.ui

import android.content.Context
import com.example.shop.data.ShopDatabase

object ServiceLocator {
    @Volatile private var repo: ShopRepository? = null

    fun provideRepository(context: Context): ShopRepository =
        repo ?: synchronized(this) {
            repo ?: ShopRepository(
                api = ShopRetrofit.api,
                dao = ShopDatabase.get(context).productDao()
            ).also { repo = it }
        }
}
