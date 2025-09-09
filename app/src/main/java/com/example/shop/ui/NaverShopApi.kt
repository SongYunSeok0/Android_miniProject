package com.example.shop.ui

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NaverShopApi {
    @Headers(
        "X-Naver-Client-Id: OJZfWV6hLr_nkbJv5zCr",
        "X-Naver-Client-Secret: b4STaau4ei"
    )
    @GET("v1/search/shop.json")
    suspend fun searchShop(
        @Query("query") query: String,
        @Query("display") display: Int = 30,
        @Query("start") start: Int = 1,
        @Query("sort") sort: String = "sim",
    ): NaverShopDto
}
