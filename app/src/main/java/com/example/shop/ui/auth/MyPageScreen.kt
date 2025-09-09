package com.example.shop.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shop.data.ProductEntity
import com.example.shop.data.ShopDatabase
import com.example.shop.ui.ShopViewModel
import com.example.shop.ui.components.ProductRow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    nav: NavController,
    vm: AuthViewModel,
    shopVm: ShopViewModel
) {
    val context = LocalContext.current
    val db = remember(context) { ShopDatabase.get(context) }

    val user by vm.currentUser.collectAsState()

    val likedProductsFlow: Flow<List<ProductEntity>> = remember(user?.id) {
        user?.let { db.productDao().observeLikedProducts(it.id) } ?: emptyFlow()
    }
    val liked by likedProductsFlow.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("마이페이지", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF03C75A)
                )
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { nav.navigate("account") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF03C75A),
                    contentColor = Color.White 
                )
            ) {
                Text("내 정보 수정")
            }

            HorizontalDivider()
            Text("찜 리스트", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            when {
                user == null -> Text("로그인이 필요합니다.")
                liked.isEmpty() -> Text("아직 찜한 상품이 없어요.")
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(liked) { product ->
                            ProductRow(product = product, vm = shopVm)
                        }
                    }
                }
            }
        }
    }
}
