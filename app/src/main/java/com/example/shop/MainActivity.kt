package com.example.shop

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shop.ui.auth.AuthViewModel
import com.example.shop.ui.auth.LoginScreen
import com.example.shop.ui.auth.SignUpScreen
import com.example.shop.ui.web.WebViewScreen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavController

import android.app.Application
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle

import com.example.shop.ui.NaverShopItem
import com.example.shop.ui.ShopViewModel
import com.example.shop.ui.theme.viewmodelTheme
import com.example.shop.ui.auth.LoginScreen
import com.example.shop.ui.auth.AccountScreen
import com.example.shop.ui.auth.SignUpScreen
import com.example.shop.ui.auth.MyPageScreen
import com.example.shop.ui.components.ShopRow
import com.example.shop.ui.components.toUi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { 
            viewmodelTheme{
                AppNav()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    nav: NavController,
    vm: ShopViewModel = viewModel()
) {
    val itemsUi = vm.items.map { it.toUi() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("네이버 쇼핑 검색") },
                actions = {
                    IconButton(onClick = { nav.navigate("mypage") }) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "마이페이지")
                    }
                }
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(12.dp)) {
            Row {
                OutlinedTextField(
                    value = vm.query,
                    onValueChange = { vm.updateQuery(it) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("검색어") }
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { vm.search() }) { Text("검색") }
            }

            Spacer(Modifier.height(12.dp))

            when {
                vm.loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                vm.error != null -> Text("에러: ${vm.error}", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(itemsUi) { uiItem ->
                    ShopRow(uiItem)
                }
            }
        }
    }
}

@Composable
fun ShopWebView() {

}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val app = LocalContext.current.applicationContext as Application
    val vm: AuthViewModel = viewModel(factory = AuthViewModel.factory(app))

    NavHost(navController = nav, startDestination = "login") {
        composable("login") { LoginScreen(nav = nav, vm = vm) }
        composable("signup") { SignUpScreen(nav = nav, vm = vm) }
        composable("search") { SearchScreen(nav=nav) }
        composable("mypage")  { MyPageScreen(nav = nav, vm = vm) }
        composable("account") { AccountScreen(nav = nav, vm = vm) }
    }
}