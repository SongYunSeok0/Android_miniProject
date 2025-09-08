package com.example.shop.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shop.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(nav: NavController, vm: AuthViewModel) {
    val user by vm.currentUser.collectAsState()
    val message by vm.message.collectAsState(initial = null)

    var username by rememberSaveable { mutableStateOf("") }
    var newPw by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.bootstrap() }
    LaunchedEffect(user?.username) {
        username = user?.username.orEmpty()
    }

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
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("아이디(닉네임)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = newPw,
                onValueChange = { newPw = it },
                label = { Text("새 비밀번호 (선택)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                label = { Text("새 비밀번호 확인") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    vm.updateProfile(
                        newUsername = username.trim(),
                        newPassword = newPw.takeIf { it.isNotBlank() && it == confirm }
                    )
                },
                enabled = username.isNotBlank() && (newPw.isBlank() || newPw == confirm),
                modifier = Modifier.fillMaxWidth()
            ) { Text("저장") }

            OutlinedButton(
                onClick = {
                    vm.logout()
                    // 스택 정리 후 로그인으로
                    nav.navigate("login") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) { Text("로그아웃") }

            message?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
