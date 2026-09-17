package com.example.refluenceds.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import com.example.refluenceds.ui.components.AppPullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.refluenceds.ui.components.SkeletonItem
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.viewmodel.CampaignViewModel
import com.example.refluenceds.utils.SetStatusBarAppearance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourCampaignsScreen(
    viewModel: CampaignViewModel,
    onBack: () -> Unit,
    onNavigateToCampaignDetail: (String) -> Unit = {}
) {
    SetStatusBarAppearance(isLightStatusBars = !AppTheme.isDark)

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Favorites", "Past", "Hidden")

    val emptyMessages = listOf(
        "No favorite campaigns\nfavor some and they'll appear\nhere",
        "No past campaigns yet",
        "No hidden campaigns to show"
    )

    val favorites by viewModel.favoriteCampaigns.collectAsState()
    val past by viewModel.pastCampaigns.collectAsState()
    val hidden by viewModel.hiddenCampaigns.collectAsState()
    val isLoading by viewModel.isYourCampaignsLoading.collectAsState()

    LaunchedEffect(selectedTab) {
        viewModel.fetchYourCampaigns(selectedTab)
    }

    val currentCampaigns = when (selectedTab) {
        0 -> favorites
        1 -> past
        else -> hidden
    }

    val textGradientBrush = Brush.horizontalGradient(
        listOf(Color(0xFF7C5CF6), Color(0xFFEC4899))
    )

    Scaffold(
        containerColor = AppTheme.colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your campaigns",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        }
    ) { innerPadding ->
        AppPullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.fetchYourCampaigns(selectedTab) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // ── Tab Row ────────────────────────────────────────────────────────
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

                Spacer(modifier = Modifier.height(20.dp))

                // ── Content Area ───────────────────────────────────────────────────
                when {
                    isLoading && currentCampaigns.isEmpty() -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            repeat(3) {
                                SkeletonItem(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(90.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            }
                        }
                    }

                    currentCampaigns.isNotEmpty() -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(currentCampaigns, key = { it.id?.toString() ?: "" }) { campaign ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            campaign.id?.let { onNavigateToCampaignDetail(it.toString()) }
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                                    border = BorderStroke(1.dp, AppTheme.colors.border)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = campaign.effectiveImageUrl,
                                            contentDescription = campaign.title,
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = campaign.effectiveBrandName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = AppTheme.colors.textSecondary
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = campaign.title.orEmpty(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = AppTheme.colors.textPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = campaign.effectiveCompensation,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp,
                                                color = AppTheme.colors.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        // ── Empty State Card ───────────────────────────────────────
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = AppTheme.colors.surfaceVariant,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 28.dp, horizontal = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = emptyMessages[selectedTab],
                                    style = TextStyle(
                                        brush = textGradientBrush,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
