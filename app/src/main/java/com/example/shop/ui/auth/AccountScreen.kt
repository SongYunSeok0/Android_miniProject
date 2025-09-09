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
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import android.util.Patterns

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(nav: NavController, vm: AuthViewModel) {
    val user by vm.currentUser.collectAsState()
    val message by vm.message.collectAsState(initial = null)

    var username by rememberSaveable { mutableStateOf("") }
    var nickname by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var newPw by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.bootstrap() }
    LaunchedEffect(user) {
        username = user?.username.orEmpty()
        nickname = user?.nickname.orEmpty()
        email = user?.email.orEmpty()
        newPw = ""
        confirm = ""
    }

    val isEmailValid = remember(email) { Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() }
    val isPwOk = remember(newPw, confirm) { newPw.isBlank() || newPw == confirm }
    val isSaveEnabled = username.isNotBlank() && nickname.isNotBlank() && email.isNotBlank() && isEmailValid && isPwOk

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("내 정보") },
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
                label = { Text("아이디") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("닉네임") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("이메일") },
                singleLine = true,
                isError = email.isNotBlank() && !isEmailValid,
                supportingText = {
                    if (email.isNotBlank() && !isEmailValid) {
                        Text("올바른 이메일 형식을 입력하세요.")
                    }
                },
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
                isError = newPw.isNotBlank() && confirm.isNotBlank() && newPw != confirm,
                supportingText = {
                    if (newPw.isNotBlank() && confirm.isNotBlank() && newPw != confirm) {
                        Text("비밀번호가 일치하지 않습니다.")
                    }
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    vm.updateProfile(
                        newUsername = username.trim(),
                        newPassword = newPw.takeIf { it.isNotBlank() && it == confirm },
                        newNickname = nickname.trim(),
                        newEmail = email.trim()
                    )
                },
                enabled = isSaveEnabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF03C75A), // ✅ 네이버 초록
                    contentColor = Color.White          // ✅ 흰색 텍스트
                )
            ) {
                Text("저장")
            }

            OutlinedButton(
                onClick = {
                    vm.logout()
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
                Text(
                    it,
                    color = if (it.contains("완료") || it.contains("성공") || it.contains("저장")) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}
