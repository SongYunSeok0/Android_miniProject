package com.example.shop.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shop.data.repository.ShopRepository

class ShopViewModelFactory(
    private val repo: ShopRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ShopViewModel(repo) as T
    }
}
