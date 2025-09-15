package com.example.shop.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.shop.data.db.entity.ProductEntity

@Composable
fun ProductDetailSheet(
    product: ProductEntity,
    vm: ShopViewModel,
    onClose: () -> Unit
) {
    val likedSet by vm.likedSet.collectAsState(initial = emptySet())
    val isLiked = likedSet.contains(product.productId)

    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        AsyncImage(
            model = product.image,
            contentDescription = product.title,
            modifier = Modifier.fillMaxWidth().height(220.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                product.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { vm.toggleLike(product) }) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    tint = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    contentDescription = if (isLiked) "찜 해제" else "찜하기"
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("최저가: ₩${product.lprice}", style = MaterialTheme.typography.titleMedium)
        product.hprice?.let { Text("최고가: ₩$it", style = MaterialTheme.typography.bodyMedium) }
        Spacer(Modifier.height(8.dp))
        Text("쇼핑몰: ${product.mallName}", style = MaterialTheme.typography.bodyMedium)
        product.brand?.let { Text("브랜드: $it", style = MaterialTheme.typography.bodyMedium) }
        product.maker?.let { Text("제조사: $it", style = MaterialTheme.typography.bodyMedium) }
        Spacer(Modifier.height(8.dp))
        product.productType?.let { type ->
            val typeLabel = when (type) {
                1 -> "일반상품"
                2 -> "중고상품"
                3 -> "단종상품"
                4 -> "예약상품"
                else -> "기타"
            }
            Text("상품구분: $typeLabel", style = MaterialTheme.typography.bodyMedium)
        }
        val categories = listOfNotNull(product.category1, product.category2, product.category3, product.category4)
        if (categories.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text("카테고리: ${categories.joinToString(" > ")}", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { vm.toggleLike(product) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03C75A), contentColor = Color.White)
            ) {
                Text(if (isLiked) "찜 해제" else "찜하기")
            }
            OutlinedButton(onClick = onClose, modifier = Modifier.weight(1f)) {
                Text("닫기")
            }
        }
    }
}
