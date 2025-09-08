package com.example.shop.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf

@Composable
fun LoginScreen(nav: NavController, vm: AuthViewModel) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val message by vm.message.collectAsState(initial = null)
    val isLoggedIn by vm.isLoggedIn.collectAsState(initial = false)

    LaunchedEffect(Unit) { vm.bootstrap() }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            nav.navigate("search") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("로그인", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("아이디") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("비밀번호") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { vm.login(username.trim(), password) }
                )
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { vm.login(username.trim(), password) },
                enabled = username.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("로그인") }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = { nav.navigate("signup") },
                modifier = Modifier.fillMaxWidth()
            ) { Text("회원가입") }

            message?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
