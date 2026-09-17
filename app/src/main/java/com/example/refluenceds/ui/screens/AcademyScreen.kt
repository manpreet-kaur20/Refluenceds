package com.example.refluenceds.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import com.example.refluenceds.ui.components.AppPullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.refluenceds.domain.model.Tutorial
import com.example.refluenceds.ui.components.SkeletonItem
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.viewmodel.CampaignViewModel
import com.example.refluenceds.utils.SetStatusBarAppearance
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademyScreen(
    viewModel: CampaignViewModel,
    onBack: () -> Unit = {},
    onNavigateToAcademyDetail: (String) -> Unit = {}
) {
    SetStatusBarAppearance(isLightStatusBars = !AppTheme.isDark)

    val liveCategories by viewModel.academyCategories.collectAsState()
    val isAcademyLoading by viewModel.isAcademyLoading.collectAsState()
    val tutorials by viewModel.tutorials.collectAsState()

    val categories = remember(liveCategories) {
        if (liveCategories.isNotEmpty()) liveCategories else listOf("Onboarding", "Basics", "Most popular")
    }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "Onboarding") }

    LaunchedEffect(liveCategories) {
        if (liveCategories.isNotEmpty() && !liveCategories.contains(selectedCategory)) {
            selectedCategory = liveCategories.first()
        }
    }

    LaunchedEffect(selectedCategory) {
        viewModel.fetchTutorials(selectedCategory)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Refluenced Academy",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppTheme.colors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppTheme.colors.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        },
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        AppPullToRefreshBox(
            isRefreshing = isAcademyLoading,
            onRefresh = {
                viewModel.fetchTutorials(selectedCategory)
            },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Category Filter Tabs
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            modifier = Modifier.clickable {
                                if (selectedCategory != category) {
                                    selectedCategory = category
                                }
                            },
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.surface,
                            border = if (isSelected) null else BorderStroke(1.dp, AppTheme.colors.border)
                        ) {
                            Text(
                                text = category,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else AppTheme.colors.textPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Grid of Tutorials
                when {
                    isAcademyLoading && tutorials.isEmpty() -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(4) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    SkeletonItem(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(0.72f)
                                            .clip(RoundedCornerShape(14.dp))
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    SkeletonItem(
                                        modifier = Modifier
                                            .width(80.dp)
                                            .height(16.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    SkeletonItem(
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .height(18.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                }
                            }
                        }
                    }

                    tutorials.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No videos available in this category yet.",
                                color = AppTheme.colors.textSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(tutorials, key = { it.id }) { tutorial ->
                                TutorialGridItem(
                                    tutorial = tutorial,
                                    onClick = { onNavigateToAcademyDetail(tutorial.id) }
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
fun TutorialGridItem(tutorial: Tutorial, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.72f)
                .clip(RoundedCornerShape(14.dp))
        ) {
            AsyncImage(
                model = tutorial.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dark semi-transparent overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.15f))
            )

            // Overlay Video Title Text inside Thumbnail
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tutorial.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 3,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Play Button Overlay in center
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Duration Overlay Badge (Bottom End)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                color = Color(0xFF1D1B36).copy(alpha = 0.75f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = tutorial.duration,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category Tag Row below thumbnail
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(AppTheme.colors.surface, CircleShape)
                    .border(1.dp, AppTheme.colors.border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (tutorial.category) {
                    "Onboarding" -> Icons.Rounded.Bolt
                    "Basics" -> Icons.Rounded.Edit
                    else -> Icons.AutoMirrored.Rounded.TrendingUp
                }
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(13.dp),
                    tint = AppTheme.colors.textPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = tutorial.category,
                fontSize = 12.sp,
                color = AppTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Tutorial Title below Category
        Text(
            text = tutorial.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.textPrimary,
            maxLines = 2
        )
    }
}
