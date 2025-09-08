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

import com.example.shop.ui.NaverShopItem
import com.example.shop.ui.ShopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SearchScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: ShopViewModel = viewModel()) {
    Scaffold(topBar = { TopAppBar(title = { Text("네이버 쇼핑 검색") }) }) { pad ->
        Column(Modifier.padding(pad).padding(12.dp)) {
            Row {
                OutlinedTextField(
                    value = viewModel.query,
                    onValueChange = { viewModel.updateQuery(it) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("검색어") }
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { viewModel.search() }) { Text("검색") }
            }

            Spacer(Modifier.height(12.dp))

            when {
                viewModel.loading -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                viewModel.error != null -> {
                    Text("에러: ${viewModel.error}", color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(viewModel.items) { item -> ShopRow(item) }
            }
        }
    }
}

@Composable
private fun ShopRow(item: NaverShopItem) {
    val ctx = LocalContext.current
    val title = remember(item.title) { item.title.replace("<b>", "").replace("</b>", "") }

    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(ctx)
                    .data(item.image)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Text("${item.mallName} • 최저가 ${item.lprice}원",
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
