package com.example.shop

import android.os.Bundle
import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import com.google.android.material.progressindicator.CircularProgressIndicator
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.LoadState
import androidx.compose.foundation.lazy.LazyColumn

import com.example.shop.ui.ShopViewModel
import com.example.shop.ui.ShopViewModelFactory
import com.example.shop.ui.ServiceLocator
import com.example.shop.ui.auth.AuthViewModel
import com.example.shop.ui.auth.LoginScreen
import com.example.shop.ui.auth.SignUpScreen
import com.example.shop.ui.auth.AccountScreen
import com.example.shop.ui.auth.MyPageScreen
import com.example.shop.ui.auth.AdminScreen
import com.example.shop.ui.components.ProductRow
import com.example.shop.ui.theme.viewmodelTheme
import com.example.shop.data.ProductEntity
import com.example.shop.ui.SortType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    nav: NavController,
    vm: ShopViewModel,
    authVm: AuthViewModel
) {
    val isAdmin by authVm.isAdmin.collectAsState()
    var sortMenuOpen by remember { mutableStateOf(false) }

    val lazyItems = vm.pagingFlow.collectAsLazyPagingItems()

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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF03C75A)
                )
            )
        }
    ) { pad ->
        Column(
            Modifier
                .padding(horizontal = 12.dp)
                .padding(top = pad.calculateTopPadding())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val query by vm.query.collectAsState()
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
                    onClick = { lazyItems.refresh() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF03C75A),
                        contentColor = Color.White
                    )
                ) { Text("검색") }

                Box {
                    IconButton(onClick = { sortMenuOpen = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "정렬",
                        )
                    }
                    DropdownMenu(
                        expanded = sortMenuOpen,
                        onDismissRequest = { sortMenuOpen = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(SortType.ACCURACY.label) },
                            onClick = { vm.updateSort(SortType.ACCURACY); sortMenuOpen = false; lazyItems.refresh() }
                        )
                        DropdownMenuItem(
                            text = { Text(SortType.PRICE_ASC.label) },
                            onClick = { vm.updateSort(SortType.PRICE_ASC); sortMenuOpen = false; lazyItems.refresh() }
                        )
                        DropdownMenuItem(
                            text = { Text(SortType.PRICE_DESC.label) },
                            onClick = { vm.updateSort(SortType.PRICE_DESC); sortMenuOpen = false; lazyItems.refresh() }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            val lazyItems = vm.pagingFlow.collectAsLazyPagingItems()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(lazyItems.itemCount) { index ->
                    val product = lazyItems[index]
                    if (product != null) {
                        ProductRow(product, vm)
                    }
                }
            
                lazyItems.apply {
                    when (loadState.refresh) {
                        is LoadState.Loading -> item {
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is LoadState.Error -> item {
                            val e = loadState.refresh as LoadState.Error
                            Column(
                                Modifier.fillMaxWidth().padding(16.dp),
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
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is LoadState.Error -> item {
                            val e = loadState.append as LoadState.Error
                            Column(
                                Modifier.fillMaxWidth().padding(16.dp),
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


@Composable
private fun AppNav() {
    val nav = rememberNavController()
    val app = LocalContext.current.applicationContext as Application

    val authVm: AuthViewModel = viewModel(factory = AuthViewModel.factory(app))

    val shopVm: ShopViewModel = viewModel(
        factory = com.example.shop.ui.ShopViewModelFactory(
            ServiceLocator.repo
        )
    )

    val user by authVm.currentUser.collectAsState()

    NavHost(navController = nav, startDestination = "login") {
        composable("login")  { LoginScreen(nav = nav, vm = authVm) }
        composable("signup") { SignUpScreen(nav = nav, vm = authVm) }
        composable("search") { SearchScreen(nav = nav, vm = shopVm, authVm = authVm) }
        composable("mypage") { MyPageScreen(nav = nav, vm = authVm, shopVm = shopVm) }

        composable("admin")  { AdminScreen(nav = nav, authVm = authVm) }
        composable("account"){ AccountScreen(nav = nav, vm = authVm) }
    }

    LaunchedEffect(user) {
        shopVm.setUserId(user?.id)
        if (user != null) {
            nav.navigate("search") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewmodelTheme {
                AppNav()
            }
        }
    }
}
