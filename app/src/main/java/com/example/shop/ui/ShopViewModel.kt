package com.example.shop.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shop.data.ProductEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


enum class SortType(val apiValue: String, val label: String) {
    ACCURACY("sim", "정확도 순"),
    PRICE_ASC("asc", "가격 낮은순"),
    PRICE_DESC("dsc", "가격 높은순")
}

class ShopViewModel(
    private val repo: ShopRepository, private val userId: Long
) : ViewModel() {

    var query by mutableStateOf("")
        private set

    var loading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var sort by mutableStateOf(SortType.ACCURACY)

    fun updateSort(type: SortType) {
        sort = type
        search()
    }

    private val allProducts = repo.observeProducts()

    private val lastSearchIds = MutableStateFlow<List<String>>(emptyList())

    val searchResults: StateFlow<List<ProductEntity>> =
        combine(allProducts, lastSearchIds) { products, ids ->
            if (ids.isEmpty()) emptyList() else {
                val map = products.associateBy { it.productId }
                ids.mapNotNull { map[it] }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())


    val products: StateFlow<List<ProductEntity>> = 
        repo.observeProducts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val likedSet: StateFlow<Set<String>> =
        repo.observeUserLikeSet(userId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val likedProducts: StateFlow<List<ProductEntity>> =
        repo.observeUserLikedProducts(userId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateQuery(q: String) { query = q }

    fun search() {
        val q = query.trim()
        if (q.isEmpty()) return
        viewModelScope.launch {
            loading = true; error = null
            runCatching { repo.searchAndCache(q, sort.apiValue) }
                .onSuccess { ids: List<String> -> lastSearchIds.value = ids }
                .onFailure { e -> error = e.message }
            loading = false
        }
    }

    fun toggleLike(product: ProductEntity) = viewModelScope.launch {
        repo.toggleLike(userId, product)
    }
}
