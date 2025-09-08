package com.example.shop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class ShopViewModel : ViewModel() {
    private val api = ShopRetrofit.api

    var query by mutableStateOf("")
        private set
    var items by mutableStateOf<List<NaverShopItem>>(emptyList())
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun updateQuery(q: String) { query = q }

    fun search() {
        val q = query.trim()
        if (q.isEmpty()) return
        viewModelScope.launch {
            loading = true; error = null
            runCatching { api.searchShop(query = q, display = 20) }
                .onSuccess { resp -> items = resp.items }
                .onFailure { e -> error = e.message }
            loading = false
        }
    }
}
