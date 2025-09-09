package com.example.shop.ui

import android.app.Application

class ShopApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}