package com.example.shop.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shop.data.db.entity.ProductEntity
import com.example.shop.data.repository.ShopRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.filter

enum class SortType(val apiValue: String, val label: String) {
    ACCURACY("sim", "정확도 순"),
    PRICE_ASC("asc", "가격 낮은순"),
    PRICE_DESC("dsc", "가격 높은순")
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
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

    val likedSet: StateFlow<Set<String>> =
        userId.flatMapLatest { id ->
            if (id == null) flowOf(emptySet()) else repo.observeUserLikeSet(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val pagingFlow: kotlinx.coroutines.flow.Flow<PagingData<ProductEntity>> =
        combine(queryFlow, sortFlow) { q, s -> q.trim() to s.apiValue }
            .filter { it.first.isNotEmpty() }
            .debounce(300)
            .distinctUntilChanged()
            .flatMapLatest { (q, s) -> repo.pagingSearch(q, s) }
            .cachedIn(viewModelScope)

    fun toggleLike(product: ProductEntity) {
        val id = userId.value ?: return
        viewModelScope.launch {
            repo.toggleLike(id, product)
        }
    }

    fun clearSearch() {
        updateQuery("")
        updateSort(SortType.ACCURACY)
    }
}
