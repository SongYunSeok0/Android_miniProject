package com.example.shop.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shop.data.db.entity.ProductEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    nav: NavController,
    vm: ShopViewModel,
    authVm: com.example.shop.ui.auth.AuthViewModel
) {
    val isAdmin by authVm.isAdmin.collectAsState()
    var sortMenuOpen by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<ProductEntity?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    val query by vm.query.collectAsState()
    var refreshTick by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("네이버 쇼핑 검색", color = Color.White) },
                actions = {
                    if (isAdmin) {
                        TextButton(onClick = { nav.navigate("admin") }) {
                            Text("사용자 관리", color = Color.White)
                        }
                    }
                    IconButton(onClick = { nav.navigate("mypage") }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "내 정보",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF03C75A))
            )
        }
    ) { pad: PaddingValues ->
        Column(
            Modifier
                .padding(pad)
                .padding(horizontal = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = vm::updateQuery,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("검색어") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF03C75A),
                        unfocusedBorderColor = Color(0xFF03C75A),
                        cursorColor = Color(0xFF03C75A)
                    )
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { if (query.isNotBlank()) refreshTick++ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF03C75A),
                        contentColor = Color.White
                    )
                ) { Text("검색") }

                Box {
                    IconButton(onClick = { sortMenuOpen = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "정렬"
                        )
                    }
                    DropdownMenu(
                        expanded = sortMenuOpen,
                        onDismissRequest = { sortMenuOpen = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(SortType.ACCURACY.label) },
                            onClick = {
                                vm.updateSort(SortType.ACCURACY)
                                sortMenuOpen = false
                                if (query.isNotBlank()) refreshTick++
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(SortType.PRICE_ASC.label) },
                            onClick = {
                                vm.updateSort(SortType.PRICE_ASC)
                                sortMenuOpen = false
                                if (query.isNotBlank()) refreshTick++
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(SortType.PRICE_DESC.label) },
                            onClick = {
                                vm.updateSort(SortType.PRICE_DESC)
                                sortMenuOpen = false
                                if (query.isNotBlank()) refreshTick++
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (query.isBlank()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("검색어를 입력해 주세요.")
                }
            } else {
                val items = vm.pagingFlow.collectAsLazyPagingItems()
                LaunchedEffect(refreshTick) { items.refresh() }

                androidx.compose.foundation.lazy.LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items.itemCount) { index ->
                        val product = items[index]
                        if (product != null) {
                            ProductRow(
                                product = product,
                                vm = vm,
                                onClick = {
                                    selected = product
                                    showSheet = true
                                }
                            )
                        }
                    }

                    items.apply {
                        when (loadState.refresh) {
                            is LoadState.Loading -> item {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                            }
                            is LoadState.Error -> item {
                                val e = loadState.refresh as LoadState.Error
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("로딩 실패: ${e.error.message}")
                                    Spacer(Modifier.height(8.dp))
                                    OutlinedButton(onClick = { retry() }) { Text("다시 시도") }
                                }
                            }
                            else -> Unit
                        }
                        when (loadState.append) {
                            is LoadState.Loading -> item {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                            }
                            is LoadState.Error -> item {
                                val e = loadState.append as LoadState.Error
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("추가 로딩 실패: ${e.error.message}")
                                    Spacer(Modifier.height(8.dp))
                                    OutlinedButton(onClick = { retry() }) { Text("다시 시도") }
                                }
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    if (showSheet && selected != null) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }) {
            ProductDetailSheet(
                product = selected!!,
                vm = vm,
                onClose = { showSheet = false }
            )
        }
    }
}
