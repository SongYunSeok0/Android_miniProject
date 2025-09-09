package com.example.shop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shop.data.ProductEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortType(val apiValue: String, val label: String) {
    ACCURACY("sim", "정확도 순"),
    PRICE_ASC("asc", "가격 낮은순"),
    PRICE_DESC("dsc", "가격 높은순")
}

@OptIn(ExperimentalCoroutinesApi::class)
class ShopViewModel(
    private val repo: ShopRepository
) : ViewModel() {

    private val userId = MutableStateFlow<Long?>(null)
    fun setUserId(id: Long?) { userId.value = id }

    private val queryFlow = MutableStateFlow("")
    private val sortFlow = MutableStateFlow(SortType.ACCURACY)

    val query: StateFlow<String> = queryFlow

    fun updateQuery(q: String) { queryFlow.value = q }
    fun updateSort(type: SortType) { sortFlow.value = type }

    val pagingFlow: Flow<PagingData<ProductEntity>> =
        combine(queryFlow, sortFlow) { q, s -> q.trim() to s.apiValue }
            .filter { it.first.isNotEmpty() }
            .distinctUntilChanged()
            .flatMapLatest { (q, s) -> repo.pagingSearch(q, s) }
            .cachedIn(viewModelScope)

    val likedSet: StateFlow<Set<String>> =
        userId.flatMapLatest { id ->
            if (id == null) flowOf(emptySet()) else repo.observeUserLikeSet(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    fun toggleLike(product: ProductEntity) {
        val id = userId.value ?: return
        viewModelScope.launch {
            repo.toggleLike(id, product)
        }
    }
}

