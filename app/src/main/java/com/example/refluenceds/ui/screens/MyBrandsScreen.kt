package com.example.refluenceds.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.refluenceds.ui.components.SkeletonItem
import com.example.refluenceds.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBrandsScreen(onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Following") }
    var isLoading by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedFilter) {
        isLoading = true
        kotlinx.coroutines.delay(1500)
        isLoading = false
    }

    val brands = listOf(
        BrandData("Ricardo AG", "FASHION • JEWELRY •\nSUSTAINABILITY • HOME", "android.resource://com.example.refluenceds/${com.example.refluenceds.R.drawable.ricardo_ag_logo}"),
        BrandData("SunIce Festival", "EVENTS • LIFESTYLE", "android.resource://com.example.refluenceds/${com.example.refluenceds.R.drawable.sunice_festival_logo}"),
        BrandData("Liebeskind Berlin", "FASHION • LIFESTYLE", "android.resource://com.example.refluenceds/${com.example.refluenceds.R.drawable.liebeskind_berlin_logo}"),
        BrandData("IONIQ Skincare", "BEAUTY • TECHNOLOGY • LIFESTYLE", "android.resource://com.example.refluenceds/${com.example.refluenceds.R.drawable.uniq_skincare_logo}"),
        BrandData("UND GRETEL", "", "android.resource://com.example.refluenceds/${com.example.refluenceds.R.drawable.und_gretel_logo}"),
        BrandData("Kapten & Son GmbH", "", "android.resource://com.example.refluenceds/${com.example.refluenceds.R.drawable.kapten_son_logo}")
    )

    val filteredBrands = brands.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Brands", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp)
                            .background(Color(0xFFF1F1F1), CircleShape)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Filter", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search for brands by their name", color = Color.LightGray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedBorderColor = Color(0xFFF1F1F1),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                singleLine = true
            )

            if (isLoading) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(6) {
                        SkeletonItem(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)))
                    }
                }
            } else if (filteredBrands.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.padding(24.dp),
                        color = Color(0xFFF1F1FF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Looks like there are no brands matching your criteria.",
                            modifier = Modifier.padding(24.dp),
                            color = Color(0xFFFA5252).copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredBrands) { brand ->
                        BrandGridCard(brand)
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Filter By",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                FilterOptionRow(
                    text = "Following",
                    icon = Icons.Default.NotificationsNone,
                    isSelected = selectedFilter == "Following",
                    onClick = {
                        if (selectedFilter != "Following") {
                            selectedFilter = "Following"
                        } else {
                            // Re-trigger skeleton loading even if same filter selected
                            isLoading = true
                        }
                        showFilterSheet = false
                    }
                )
                
                FilterOptionRow(
                    text = "My Interests",
                    icon = Icons.Default.FavoriteBorder,
                    isSelected = selectedFilter == "My Interests",
                    onClick = {
                        if (selectedFilter != "My Interests") {
                            selectedFilter = "My Interests"
                        } else {
                            // Re-trigger skeleton loading even if same filter selected
                            isLoading = true
                        }
                        showFilterSheet = false
                    }
                )
            }
        }
    }
}

@Composable
fun FilterOptionRow(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val activeColor = if (isSelected) Color(0xFF4B4FE4) else Color.Gray
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = activeColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 16.sp, color = activeColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BrandGridCard(brand: BrandData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F1F1))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = brand.logoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(brand.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                brand.categories,
                fontSize = 9.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
                minLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.NotificationsNone, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Follow", fontSize = 13.sp)
            }
        }
    }
}

data class BrandData(val name: String, val categories: String, val logoUrl: String)
