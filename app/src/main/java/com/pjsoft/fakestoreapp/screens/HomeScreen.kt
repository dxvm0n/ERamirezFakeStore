package com.pjsoft.fakestoreapp.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pjsoft.fakestoreapp.components.ProductCard
import com.pjsoft.fakestoreapp.models.Product
import com.pjsoft.fakestoreapp.services.ProductService
import com.pjsoft.fakestoreapp.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun HomeScreen(
    navController: NavController
) {
    // Una especie de seccion o categorias que uso para ver su estado actual o clic en ello :)
    var products by remember { mutableStateOf(listOf<Product>()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCategory by remember { mutableStateOf("Trending") }

    val categories = listOf("Trending", "Shoes", "Sweatshirts", "Shirts", "Bags")

    LaunchedEffect(true) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://fakestoreapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(ProductService::class.java)
            val result = async(Dispatchers.IO) { service.getAllProducts() }
            products = result.await()
            loading = false
        } catch (e: Exception) {
            Log.e("HomeScreen", e.toString())
            error = true
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "EmiShop",
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("¿What do you looking for?", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = IconTint) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CardBackground,
                unfocusedContainerColor = CardBackground,
                disabledContainerColor = CardBackground,
                focusedIndicatorColor = Accent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Accent
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = {
                        Text(
                            category,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = if (isSelected) Accent else CardBackground,
                        selectedContainerColor = Accent
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Accent)
                }
            }
            error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error loading products. Try later",
                        color = TextSecondary,
                        fontSize = 16.sp
                    )
                }
            }
            products.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No products available",
                        color = TextSecondary,
                        fontSize = 16.sp
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    val filtered = products.filter {
                        it.title.contains(searchQuery.text, ignoreCase = true)
                    }
                    items(filtered) { product ->
                        ProductCard(
                            product = product,
                            onClick = {
                                navController.navigate(ProductDetailScreenRoute(product.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FakeStoreAppTheme {
        HomeScreen(rememberNavController())
    }
}
