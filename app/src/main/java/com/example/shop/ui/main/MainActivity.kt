package com.example.shop.ui.main

import android.app.Application
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shop.core.ServiceLocator
import com.example.shop.ui.auth.AccountScreen
import com.example.shop.ui.auth.AdminScreen
import com.example.shop.ui.auth.AuthViewModel
import com.example.shop.ui.auth.LoginScreen
import com.example.shop.ui.auth.MyPageScreen
import com.example.shop.ui.auth.SignUpScreen
import com.example.shop.ui.search.SearchScreen
import com.example.shop.ui.search.ShopViewModel
import com.example.shop.ui.search.ShopViewModelFactory
import com.example.shop.ui.theme.viewmodelTheme
import com.example.shop.ui.web.WebViewScreen

@Composable
private fun AppNav() {
    val nav = rememberNavController()
    val app = LocalContext.current.applicationContext as Application
    val authVm: AuthViewModel = viewModel(factory = AuthViewModel.factory(app))
    val shopVm: ShopViewModel = viewModel(factory = ShopViewModelFactory(ServiceLocator.repo))
    val user by authVm.currentUser.collectAsState()

    NavHost(navController = nav, startDestination = "login") {
        composable("login") { LoginScreen(nav = nav, vm = authVm) }
        composable("signup") { SignUpScreen(nav = nav, vm = authVm) }
        composable("search") { SearchScreen(nav = nav, vm = shopVm, authVm = authVm) }
        composable("mypage") { MyPageScreen(nav = nav, vm = authVm, shopVm = shopVm) }
        composable("admin") { AdminScreen(nav = nav, authVm = authVm) }
        composable("account") { AccountScreen(nav = nav, vm = authVm) }
        composable(
            route = "webview/{url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = Uri.decode(backStackEntry.arguments?.getString("url") ?: "")
            WebViewScreen(url = url)
        }
    }

    LaunchedEffect(user) {
        shopVm.setUserId(user?.id)
        if (user != null) {
            nav.navigate("search") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        } else {
            shopVm.clearSearch()
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
