@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.shop.ui.auth

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(nav: NavController, authVm: AuthViewModel) {
    val users by authVm.observeAllUsers().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("관리자: 사용자 관리", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF03C75A)),
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                    headlineContent = {
                        Text("${u.username}${if (u.isAdmin) " (admin)" else ""}")
                    },
                    supportingContent = { Text("status: ${u.status}") },
                    trailingContent = {
                        Row {
                            TextButton(
                                onClick = {
                                    val next = if (u.status == "ACTIVE") "DEACTIVATED" else "ACTIVE"
                                    scope.launch {
                                        authVm.setUserStatus(u.id, next)
                                    }
                                }
                            ) { Text("상태") }

                            Spacer(Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    scope.launch { authVm.deleteUser(u) }
                                }
                            ) { Text("삭제") }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
                HorizontalDivider()
            }
        }
    }
}
