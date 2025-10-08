package com.pjsoft.fakestoreapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.pjsoft.fakestoreapp.models.Product
import com.pjsoft.fakestoreapp.models.Rating
import com.pjsoft.fakestoreapp.services.ProductService
import com.pjsoft.fakestoreapp.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun ProductDetailScreen(
    id: Int,
    navController: NavController? = null
) {
    // La variable isFavorite la agregue para simular una "bolsa de favoritos"
    var product by remember { mutableStateOf<Product?>(null) }
    var loading by remember { mutableStateOf(true) }
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://fakestoreapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(ProductService::class.java)
            val result = async(Dispatchers.IO) { service.getProductById(id) }
            product = result.await()
            loading = false
        } catch (e: Exception) {
            loading = false
        }
    }

    if (loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BeigeBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Accent)
        }
    } else {
        product?.let { item ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BeigeBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { navController?.popBackStack() }
                    )

                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) FavoriteRed else IconTint,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { isFavorite = !isFavorite }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AsyncImage(
                    model = item.image,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = item.title,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "€${item.price}",
                    color = Accent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "⭐ ${item.rating.rate}  (${item.rating.count})",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Divider(
                        color = DividerColor,
                        modifier = Modifier
                            .height(14.dp)
                            .width(1.dp)
                    )
                    Text(
                        text = item.category.replaceFirstChar { it.uppercase() },
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = item.description,
                    color = TextSecondary,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Add to cart",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BeigeBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Product not found", color = TextSecondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    FakeStoreAppTheme {
        ProductDetailScreen(
            id = 1,
            navController = null
        )
    }
}
