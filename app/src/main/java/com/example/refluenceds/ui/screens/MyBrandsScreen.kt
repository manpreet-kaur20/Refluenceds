package com.example.refluenceds.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import com.example.refluenceds.ui.components.AppPullToRefreshBox
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
import com.example.refluenceds.data.remote.dto.BrandItemDto
import com.example.refluenceds.ui.components.SkeletonItem
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.viewmodel.CampaignViewModel
import com.example.refluenceds.utils.SetStatusBarAppearance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBrandsScreen(
    viewModel: CampaignViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit,
    onNavigateToBrandDetail: (String, String) -> Unit = { _, _ -> }
) {
    SetStatusBarAppearance(isLightStatusBars = true)

    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Following") }

    val liveBrands by viewModel.myBrands.collectAsState()
    val isHomeLoading by viewModel.isHomeLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMyBrands()
    }

    val filteredBrands = remember(liveBrands, searchQuery, selectedFilter) {
        liveBrands.filter { brand ->
            val nameMatches = brand.displayName.contains(searchQuery, ignoreCase = true)
            if (!nameMatches) return@filter false
            if (selectedFilter == "Following") {
                brand.isFollowing != false
            } else {
                true
            }
        }
    }

    Scaffold(
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Brands", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppTheme.colors.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppTheme.colors.textPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(38.dp)
                            .background(AppTheme.colors.primary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filter",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppTheme.colors.surface)
            )
        }
    ) { padding ->
        AppPullToRefreshBox(
            isRefreshing = isHomeLoading,
            onRefresh = { viewModel.fetchMyBrands() },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search for brands by their name", color = AppTheme.colors.textTertiary, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AppTheme.colors.textSecondary) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AppTheme.colors.textPrimary,
                        unfocusedTextColor = AppTheme.colors.textPrimary,
                        focusedBorderColor = AppTheme.colors.primary,
                        unfocusedBorderColor = AppTheme.colors.border,
                        unfocusedContainerColor = AppTheme.colors.surface,
                        focusedContainerColor = AppTheme.colors.surface
                    ),
                    singleLine = true
                )

                if (isHomeLoading && liveBrands.isEmpty()) {
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
                            color = AppTheme.colors.surfaceVariant,
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
                            BrandGridCard(
                                brand = brand,
                                onBrandClick = {
                                    onNavigateToBrandDetail(brand.id?.toString() ?: "", brand.displayName)
                                },
                                onToggleFollow = {
                                    brand.id?.let { viewModel.toggleBrandFollow(it) }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = AppTheme.colors.surface,
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
                    color = AppTheme.colors.textPrimary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                FilterOptionRow(
                    text = "Following",
                    icon = Icons.Default.NotificationsNone,
                    isSelected = selectedFilter == "Following",
                    onClick = {
                        selectedFilter = "Following"
                        showFilterSheet = false
                    }
                )
                
                FilterOptionRow(
                    text = "All Brands",
                    icon = Icons.Default.FavoriteBorder,
                    isSelected = selectedFilter == "All Brands",
                    onClick = {
                        selectedFilter = "All Brands"
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
    val activeColor = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textSecondary
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
fun BrandGridCard(
    brand: BrandItemDto,
    onBrandClick: () -> Unit = {},
    onToggleFollow: () -> Unit = {}
) {
    val isFollowing = brand.isFollowing ?: false

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBrandClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
        border = BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = brand.effectiveLogo ?: "",
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = brand.displayName.ifEmpty { "Brand" },
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = brand.effectiveIndustryName.ifEmpty { brand.city.orEmpty() }.ifEmpty { "BEAUTY • FASHION" },
                fontSize = 8.5.sp,
                color = AppTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 11.5.sp,
                minLines = 2,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            val isDark = AppTheme.isDark
            val followContainerColor = if (isFollowing) {
                if (isDark) Color(0xFF252840) else Color(0xFFEEF2FF)
            } else {
                AppTheme.colors.primary
            }
            val followContentColor = if (isFollowing) {
                if (isDark) Color(0xFF818CF8) else Color(0xFF5B61F4)
            } else {
                Color.White
            }

            Button(
                onClick = onToggleFollow,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = followContainerColor,
                    contentColor = followContentColor
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
            ) {
                Icon(
                    imageVector = if (isFollowing) Icons.Filled.NotificationsActive else Icons.Default.NotificationsNone,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = followContentColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isFollowing) "Following" else "Follow",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
