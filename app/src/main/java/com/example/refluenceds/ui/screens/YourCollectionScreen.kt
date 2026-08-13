package com.example.refluenceds.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
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
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

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
        colors = listOf(
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
fun YourCollectionScreen(onBack: () -> Unit = {}) {
    val tabs = listOf("Instagram", "TikTok", "Academy")
    var selectedTab by remember { mutableStateOf(0) }

    // Simulate loading: true for 2 seconds then false
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(selectedTab) {
        isLoading = true
        delay(2000)
        isLoading = false
    }

    // Sample content per tab: TikTok has one item, others are empty
    val tiktokItems = listOf(
        "https://picsum.photos/seed/tiktok1/400/600"
    )

    val gradientText = Brush.horizontalGradient(
        listOf(Color(0xFF7C5CF6), Color(0xFFEC4899))
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your collection",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1A1A2E)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A2E)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                            color = if (isSelected) Color(0xFF4B4FE4) else Color.White,
                            border = if (!isSelected) BorderStroke(1.dp, Color(0xFFDDDDEE)) else null,
                            shadowElevation = if (isSelected) 2.dp else 0.dp
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
                                color = if (isSelected) Color.White else Color(0xFF5A5A72),
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Content Area ───────────────────────────────────────────────
            when {
                isLoading -> {
                    // Skeleton grid — 2 columns of shimmer cards
                    CollectionSkeletonGrid()
                }

                selectedTab == 1 && tiktokItems.isNotEmpty() -> {
                    // TikTok has content — show grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(tiktokItems) { url ->
                            CollectionMediaCard(imageUrl = url, platform = "TikTok")
                        }
                    }
                }

                else -> {
                    // Empty state card
                    val platformName = when (selectedTab) {
                        0 -> "Social Feed"
                        1 -> "TikTok Feed"
                        else -> "Academy"
                    }
                    val buttonLabel = when (selectedTab) {
                        0 -> "Go to Social Feed"
                        1 -> "Go to TikTok Feed"
                        else -> "Go to Academy"
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFEEEEFD),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 28.dp, horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "You have not saved any content\nfrom your $platformName yet.",
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
                                shape = RoundedCornerShape(50),
                                color = Color.White,
                                shadowElevation = 1.dp,
                                border = BorderStroke(1.dp, Color(0xFFEEEEFF))
                            ) {
                                Text(
                                    text = buttonLabel,
                                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp),
                                    color = Color(0xFF4B4FE4),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
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
private fun CollectionMediaCard(imageUrl: String, platform: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.68f)
            .clip(RoundedCornerShape(16.dp))
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
