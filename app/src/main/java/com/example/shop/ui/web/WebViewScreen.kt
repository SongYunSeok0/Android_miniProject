package com.example.shop.ui.web

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewScreen(
    url: String = ""
) {
    AndroidView(factory = { context -> 
        WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {}
            loadUrl(url)
        }
    })
}