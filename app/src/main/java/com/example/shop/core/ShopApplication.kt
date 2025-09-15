package com.example.shop.core

import android.app.Application

class ShopApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}