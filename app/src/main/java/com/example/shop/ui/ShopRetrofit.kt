package com.example.shop.ui

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ShopRetrofit {
    private const val BASE = "https://openapi.naver.com/"

    private val client = OkHttpClient.Builder().build()

    val api: NaverShopApi = Retrofit.Builder()
        .baseUrl(BASE)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NaverShopApi::class.java)
}
