package com.pjsoft.fakestoreapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.pjsoft.fakestoreapp.models.Product
import com.pjsoft.fakestoreapp.models.Rating
import com.pjsoft.fakestoreapp.ui.theme.*

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(170.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.TopEnd
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) FavoriteRed else IconTint,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(22.dp)
                        .clickable { isFavorite = !isFavorite }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.title,
                color = TextPrimary,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "€${product.price}",
                color = Accent,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    val testProduct = Product(
        id = 1,
        title = "Men’s Pullover Hoodie",
        price = 97.0,
        description = "Soft and comfortable cotton hoodie.",
        category = "Sweatshirts",
        image = "https://miimagen",
        rating = Rating(rate = 4.5, count = 120)
    )

    FakeStoreAppTheme {
        ProductCard(product = testProduct, onClick = {})
    }
}
