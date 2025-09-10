package com.example.shop.ui.web

import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewScreen(url: String) {
    val context = LocalContext.current
    var openedExternal by remember { mutableStateOf(false) }

    fun openExternal(u: String) {
        if (openedExternal) return
        openedExternal = true
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(u)))
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val u = request?.url?.toString() ?: return false
                        return if (u.startsWith("http://") || u.startsWith("https://")) {
                            view?.loadUrl(u)
                            true
                        } else {
                            openExternal(u)
                            true
                        }
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        openExternal(request?.url?.toString() ?: url)
                    }

                    override fun onReceivedHttpError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        errorResponse: WebResourceResponse?
                    ) {
                        openExternal(request?.url?.toString() ?: url)
                    }
                }
                loadUrl(url)
            }
        },
        update = { wv ->
            val current = wv.url ?: ""
            if (current != url) wv.loadUrl(url)
        }
    )
}
