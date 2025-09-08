package com.example.shop.ui

import android.app.Application

class ShopApplication : Application() {
    lateinit var repository: ShopRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = ServiceLocator.provideRepository(this)
    }
}
