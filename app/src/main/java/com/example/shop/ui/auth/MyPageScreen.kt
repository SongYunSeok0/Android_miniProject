package com.example.shop.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shop.data.ProductEntity
import com.example.shop.data.ShopDatabase
import com.example.shop.ui.components.ShopRow
import com.example.shop.ui.components.toUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(nav: NavController, vm: AuthViewModel) {
    val context = LocalContext.current
    val db = remember(context) { ShopDatabase.get(context) }

    val user by vm.currentUser.collectAsState()

    val likedProductsFlow: Flow<List<ProductEntity>> = remember(user?.id) {
        user?.let { db.likeDao().observeLikedProducts(it.id) } ?: emptyFlow()
    }
    val liked by likedProductsFlow.collectAsState(initial = emptyList())

    val likedUi = remember(liked) { liked.map { it.toUi() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("마이페이지") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로"
                        )
                    }
                }
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
                modifier = Modifier.fillMaxWidth()
            ) { Text("내 정보 수정") }

            HorizontalDivider()
            Text("찜 리스트", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            when {
                user == null -> Text("로그인이 필요합니다.")
                likedUi.isEmpty() -> Text("아직 찜한 상품이 없어요.")
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(likedUi) { uiItem ->
                            ShopRow(uiItem)
                        }
                    }
                }
            }
        }
    }
}
