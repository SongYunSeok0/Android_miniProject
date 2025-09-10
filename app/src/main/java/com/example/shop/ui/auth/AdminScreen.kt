package com.example.shop.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

import com.example.shop.data.ShopDatabase
import com.example.shop.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(nav: NavController, authVm: AuthViewModel) {
    val context = LocalContext.current
    val db = remember(context) { ShopDatabase.get(context) }
    val users by db.userDao().observeAll().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("관리자: 사용자 관리", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF03C75A)),
                navigationIcon = {
                    IconButton(onClick = { nav.navigate("search") }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "뒤로", 
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            authVm.logout()
                            nav.navigate("login") {
                                popUpTo("search") { inclusive = true }
                            }
                        }
                    ) {
                        Text("로그아웃", color = Color.White)
                    }
                }
            )
        }
    ) { pad ->
        LazyColumn(contentPadding = pad) {
            items(users) { u ->
                ListItem(
                    headlineContent = { Text("${u.username}${if (u.isAdmin) " (admin)" else ""}") },
                    supportingContent = { Text("status: ${u.status}") },
                    trailingContent = {
                        Row {
                            TextButton(onClick = {
                                val next = if (u.status == "DISABLED") "ACTIVE" else "DISABLED"
                                CoroutineScope(Dispatchers.IO).launch { db.userDao().updateStatus(u.id, next) }
                            }) { Text("상태") }
                            TextButton(onClick = {
                                CoroutineScope(Dispatchers.IO).launch { db.userDao().delete(u) }
                            }) { Text("삭제") }
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}
