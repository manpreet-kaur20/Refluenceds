package com.example.refluenceds.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import com.example.refluenceds.ui.components.AppPullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.refluenceds.data.remote.dto.ContentFeedItemDto
import com.example.refluenceds.domain.model.Tutorial
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.viewmodel.CampaignViewModel
import com.example.refluenceds.utils.SetStatusBarAppearance

@Composable
private fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    return Brush.linearGradient(
        colors = if (AppTheme.isDark) listOf(
            Color(0xFF222230),
            Color(0xFF333345),
            Color(0xFF222230),
        ) else listOf(
            Color(0xFFE8E8F0),
            Color(0xFFF4F4FA),
            Color(0xFFE8E8F0),
        ),
        start = Offset(0f, 0f),
        end = Offset(translateAnim, translateAnim)
    )
}

// ─── Screen ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourCollectionScreen(
    viewModel: CampaignViewModel = viewModel(),
    onBack: () -> Unit = {},
    onNavigateToSocialFeed: () -> Unit = {},
    onNavigateToAcademy: () -> Unit = {},
    onNavigateToAcademyDetail: (String) -> Unit = {}
) {
    SetStatusBarAppearance(isLightStatusBars = !AppTheme.isDark)

    val tabs = listOf("Instagram", "TikTok", "Academy")
    var selectedTab by remember { mutableIntStateOf(0) }

    val collectionItems by viewModel.collectionItems.collectAsState()
    val isCollectionLoading by viewModel.isCollectionLoading.collectAsState()
    val tutorials by viewModel.tutorials.collectAsState()
    val isAcademyLoading by viewModel.isAcademyLoading.collectAsState()

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            0 -> viewModel.fetchCollection("instagram")
            1 -> viewModel.fetchCollection("tiktok")
            2 -> viewModel.fetchTutorials()
        }
    }

    val gradientText = Brush.horizontalGradient(
        listOf(Color(0xFF7C5CF6), Color(0xFFEC4899))
    )

    Scaffold(
        containerColor = AppTheme.colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your collection",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppTheme.colors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppTheme.colors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppTheme.colors.surface)
            )
        }
    ) { innerPadding ->
        AppPullToRefreshBox(
            isRefreshing = if (selectedTab == 2) isAcademyLoading else isCollectionLoading,
            onRefresh = {
                when (selectedTab) {
                    0 -> viewModel.fetchCollection("instagram")
                    1 -> viewModel.fetchCollection("tiktok")
                    2 -> viewModel.fetchTutorials()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // ── Tab Row centered ───────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        tabs.forEachIndexed { index, label ->
                            val isSelected = selectedTab == index
                            Surface(
                                onClick = { selectedTab = index },
                                shape = RoundedCornerShape(50),
                                color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.surface,
                                border = if (!isSelected) BorderStroke(1.dp, AppTheme.colors.border) else null,
                                shadowElevation = if (isSelected) 2.dp else 0.dp
                            ) {
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                                    color = if (isSelected) Color.White else AppTheme.colors.textSecondary,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ── Tab Content ────────────────────────────────────────────────
                when (selectedTab) {
                    0, 1 -> {
                        // Instagram / TikTok Saved Content
                        when {
                            isCollectionLoading && collectionItems.isEmpty() -> {
                                CollectionSkeletonGrid()
                            }
                            collectionItems.isNotEmpty() -> {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(bottom = 24.dp)
                                ) {
                                    items(collectionItems, key = { it.id?.toString() ?: "" }) { item ->
                                        CollectionMediaCard(
                                            imageUrl = item.effectiveThumbnailUrl.ifBlank { item.effectiveMediaUrl },
                                            platform = if (selectedTab == 0) "Instagram" else "TikTok",
                                            onClick = onNavigateToSocialFeed
                                        )
                                    }
                                }
                            }
                            else -> {
                                val platformName = if (selectedTab == 0) "Instagram" else "TikTok"
                                CollectionEmptyState(
                                    message = "You have not saved any $platformName\ncontent to your collection yet.",
                                    buttonLabel = "Go to Social Feed",
                                    gradientText = gradientText,
                                    onButtonClick = onNavigateToSocialFeed
                                )
                            }
                        }
                    }
                    2 -> {
                        // Academy Collection
                        when {
                            isAcademyLoading && tutorials.isEmpty() -> {
                                CollectionSkeletonGrid()
                            }
                            tutorials.isNotEmpty() -> {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(bottom = 24.dp)
                                ) {
                                    items(tutorials, key = { it.id }) { tutorial ->
                                        CollectionMediaCard(
                                            imageUrl = tutorial.thumbnailUrl,
                                            platform = "Academy",
                                            onClick = { onNavigateToAcademyDetail(tutorial.id) }
                                        )
                                    }
                                }
                            }
                            else -> {
                                CollectionEmptyState(
                                    message = "You have not saved any content\nfrom your Academy yet.",
                                    buttonLabel = "Go to Academy",
                                    gradientText = gradientText,
                                    onButtonClick = onNavigateToAcademy
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CollectionEmptyState(
    message: String,
    buttonLabel: String,
    gradientText: Brush,
    onButtonClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppTheme.colors.surfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 28.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = TextStyle(
                    brush = gradientText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Surface(
                onClick = onButtonClick,
                shape = RoundedCornerShape(50),
                color = AppTheme.colors.surface,
                shadowElevation = 1.dp,
                border = BorderStroke(1.dp, AppTheme.colors.border)
            ) {
                Text(
                    text = buttonLabel,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp),
                    color = AppTheme.colors.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ─── Skeleton Grid ────────────────────────────────────────────────────────────
@Composable
private fun CollectionSkeletonGrid() {
    val brush = shimmerBrush()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.68f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush)
            )
        }
    }
}

// ─── Media Card ───────────────────────────────────────────────────────────────
@Composable
private fun CollectionMediaCard(
    imageUrl: String,
    platform: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.68f)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Bottom gradient + platform label
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)))
                )
        )
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = 0.15f)
        ) {
            Text(
                text = "⊙ $platform",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
