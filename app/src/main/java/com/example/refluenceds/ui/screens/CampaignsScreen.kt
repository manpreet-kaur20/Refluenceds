package com.example.refluenceds.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import com.example.refluenceds.ui.components.AppPullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.refluenceds.domain.model.Campaign
import com.example.refluenceds.ui.components.SkeletonItem
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.theme.GradientStart
import com.example.refluenceds.ui.viewmodel.CampaignViewModel
import com.example.refluenceds.utils.SetStatusBarAppearance

import androidx.compose.ui.res.painterResource
import com.example.refluenceds.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignsScreen(
    viewModel: CampaignViewModel,
    onNavigateToCampaignDetail: (String) -> Unit = {}
) {
    SetStatusBarAppearance(isLightStatusBars = true)

    val campaigns by viewModel.campaigns.collectAsState()
    val isLoading by viewModel.isCampaignsLoading.collectAsState()
    var isGridView by remember { mutableStateOf(true) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedCategory by remember { mutableStateOf("Explore") }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    val filterSheetState = rememberModalBottomSheetState()

    LaunchedEffect(selectedCategory) {
        val preset = when (selectedCategory) {
            "Recommended" -> "recommended"
            "Eligible" -> "eligible"
            else -> null
        }
        viewModel.fetchCampaigns(filterPreset = preset)
    }

    val filteredCampaigns = remember(campaigns, selectedFilter) {
        if (selectedFilter.isBlank() || selectedFilter == "All" || selectedFilter == "All Campaigns") {
            campaigns
        } else {
            campaigns.filter { campaign ->
                val plat = campaign.platform.orEmpty().lowercase()
                val deliv = campaign.deliverablesSummary.orEmpty().lowercase()
                val cat = campaign.category.lowercase()
                when (selectedFilter) {
                    "Instagram Campaigns" -> {
                        plat.contains("instagram") || deliv.contains("instagram") || (plat.isBlank() && !plat.contains("tiktok") && !plat.contains("ugc"))
                    }
                    "TikTok Campaigns" -> {
                        plat.contains("tiktok") || deliv.contains("tiktok")
                    }
                    "UGC Campaigns" -> {
                        plat.contains("ugc") || cat.contains("ugc") || deliv.contains("ugc")
                    }
                    "Instagram Stories Only" -> {
                        deliv.contains("story") || deliv.contains("stories")
                    }
                    else -> true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Dropdown category selector button
                Box {
                    Surface(
                        onClick = { showCategoryDropdown = !showCategoryDropdown },
                        shape = RoundedCornerShape(50),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val iconRes = when (selectedCategory) {
                                "Explore" -> R.drawable.ic_telescope
                                "Eligible" -> R.drawable.ic_target_concentric
                                else -> R.drawable.ic_thumbs_up
                            }
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = AppTheme.colors.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedCategory,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = AppTheme.colors.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = if (showCategoryDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = AppTheme.colors.primary
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false },
                        modifier = Modifier
                            .width(220.dp)
                            .background(AppTheme.colors.surface, shape = RoundedCornerShape(16.dp))
                    ) {
                        val categories = listOf(
                            Triple("Explore", R.drawable.ic_telescope, "Explore"),
                            Triple("Recommended", R.drawable.ic_thumbs_up, "Recommended"),
                            Triple("Eligible", R.drawable.ic_target_concentric, "Eligible")
                        )

                        categories.forEach { (cat, icon, label) ->
                            val isSelected = selectedCategory == cat
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textPrimary,
                                        fontSize = 15.sp
                                    )
                                },
                                onClick = {
                                    selectedCategory = cat
                                    showCategoryDropdown = false
                                    viewModel.triggerLoading()
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = icon),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textPrimary
                                    )
                                },
                                modifier = Modifier.background(
                                    if (isSelected) AppTheme.colors.surfaceVariant else Color.Transparent
                                )
                            )
                        }
                    }
                }

                // Action buttons: Filter & View Mode
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = AppTheme.colors.surface,
                        border = BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter",
                                tint = AppTheme.colors.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        onClick = { isGridView = !isGridView },
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = AppTheme.colors.surface,
                        border = BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_view_toggle),
                                contentDescription = "Toggle Layout",
                                tint = AppTheme.colors.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        AppPullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = {
                val preset = when (selectedCategory) {
                    "Recommended" -> "recommended"
                    "Eligible" -> "eligible"
                    else -> null
                }
                viewModel.fetchCampaigns(filterPreset = preset)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                if (isLoading) {
                    if (isGridView) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 110.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            items(6) {
                                SkeletonItem(modifier = Modifier.fillMaxWidth().aspectRatio(0.8f).clip(RoundedCornerShape(16.dp)))
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 110.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(3) {
                                SkeletonItem(modifier = Modifier.fillMaxWidth().height(450.dp).clip(RoundedCornerShape(16.dp)))
                            }
                        }
                    }
                } else if (filteredCampaigns.isEmpty()) {
                    if (selectedCategory == "Eligible") {
                        EligibleEmptyState()
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No campaigns found",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (selectedFilter != "All") "No campaigns match the filter \"$selectedFilter\"." else "Try selecting Explore or refreshing the feed.",
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    if (isGridView) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 110.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            items(filteredCampaigns, key = { it.id }) { campaign ->
                                CampaignGridItem(
                                    campaign = campaign,
                                    onClick = { onNavigateToCampaignDetail(campaign.id) }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 110.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredCampaigns, key = { it.id }) { campaign ->
                                CampaignListItem(
                                    campaign = campaign,
                                    onClick = { onNavigateToCampaignDetail(campaign.id) }
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
                sheetState = filterSheetState,
                containerColor = AppTheme.colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Filter By",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppTheme.colors.textPrimary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    FilterOption(
                        title = "All Campaigns",
                        isSelected = selectedFilter == "All" || selectedFilter == "All Campaigns",
                        onClick = {
                            selectedFilter = "All"
                            showFilterSheet = false
                        }
                    )
                    FilterOption(
                        title = "Instagram Campaigns",
                        isSelected = selectedFilter == "Instagram Campaigns",
                        onClick = {
                            selectedFilter = "Instagram Campaigns"
                            showFilterSheet = false
                        }
                    )
                    FilterOption(
                        title = "TikTok Campaigns",
                        isSelected = selectedFilter == "TikTok Campaigns",
                        onClick = {
                            selectedFilter = "TikTok Campaigns"
                            showFilterSheet = false
                        }
                    )
                    FilterOption(
                        title = "UGC Campaigns",
                        isSelected = selectedFilter == "UGC Campaigns",
                        onClick = {
                            selectedFilter = "UGC Campaigns"
                            showFilterSheet = false
                        }
                    )
                    FilterOption(
                        title = "Instagram Stories Only",
                        isSelected = selectedFilter == "Instagram Stories Only",
                        onClick = {
                            selectedFilter = "Instagram Stories Only"
                            showFilterSheet = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EligibleEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "No Matching Campaigns Right Now",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = AppTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Verify your social accounts or apply to ugc to see what campaigns you are eligible for.",
            fontSize = 14.sp,
            color = AppTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        EligibleActionCard(title = "Instagram", action = "Connect", iconRes = R.drawable.ic_social_instagram)
        Spacer(modifier = Modifier.height(14.dp))
        EligibleActionCard(title = "TikTok", action = "Connect", iconRes = R.drawable.ic_social_tiktok)
        Spacer(modifier = Modifier.height(14.dp))
        EligibleActionCard(title = "UGC", action = "Apply", iconRes = R.drawable.ic_social_video)
    }
}

@Composable
fun EligibleActionCard(title: String, action: String, iconRes: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = AppTheme.colors.surface,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = AppTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AppTheme.colors.textPrimary
                )
            }
            
            Button(
                onClick = { },
                modifier = Modifier
                    .width(104.dp)
                    .height(38.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(50)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(listOf(Color(0xFF6B66FF), Color(0xFFE55589))),
                            shape = RoundedCornerShape(50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = action,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FilterOption(
    title: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textSecondary
    )
}

@Composable
fun CampaignGridItem(
    campaign: Campaign,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        // Image Box with Logo & Applicants Pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.78f)
                .clip(RoundedCornerShape(16.dp))
                .background(AppTheme.colors.surfaceVariant)
        ) {
            if (campaign.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = campaign.imageUrl,
                    contentDescription = campaign.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Top-left Brand Logo Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(32.dp)
                    .background(Color.White, CircleShape)
                    .border(BorderStroke(0.5.dp, Color(0x22000000)), CircleShape)
                    .padding(2.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!campaign.brandLogo.isNullOrBlank()) {
                    AsyncImage(
                        model = campaign.brandLogo,
                        contentDescription = campaign.brandName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = campaign.brandName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AppTheme.colors.primary
                    )
                }
            }

            // Bottom Applicants Badge Overlay (if applicants > 0 or has badge)
            if (campaign.applicantsCount > 0 || !campaign.applicantsBadge.isNullOrBlank()) {
                val badgeText = if (campaign.applicantsCount > 0) {
                    "${campaign.applicantsCount} APPLICANTS"
                } else {
                    campaign.applicantsBadge?.uppercase() ?: "APPLICANTS"
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(50),
                    color = Color.White,
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_flame),
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3748)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Deliverables Tag Pill
        DeliverablesPillTag(campaign)

        Spacer(modifier = Modifier.height(5.dp))

        // Brand Name (Uppercase, grey, bold/semi-bold)
        Text(
            text = campaign.brandName.uppercase(),
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8E8E93),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Campaign Title (Bold, primary color, 2 lines)
        Text(
            text = campaign.title,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 17.sp
        )
    }
}

@Composable
fun DeliverablesPillTag(campaign: Campaign) {
    val isDark = AppTheme.isDark
    val isTikTok = campaign.platform?.lowercase()?.contains("tiktok") == true ||
            campaign.category.contains("tiktok", ignoreCase = true) ||
            campaign.title.contains("tiktok", ignoreCase = true)

    Surface(
        shape = RoundedCornerShape(50),
        color = if (isDark) Color(0xFF23272F) else Color(0xFFF1F3F5)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isTikTok) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_social_tiktok),
                    contentDescription = "TikTok",
                    modifier = Modifier.size(11.dp),
                    tint = AppTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${campaign.reelCount.coerceAtLeast(1)}",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.textPrimary
                )
            } else {
                // Reel / Video deliverable
                Icon(
                    painter = painterResource(id = R.drawable.ic_deliverable_reel),
                    contentDescription = "Reel",
                    modifier = Modifier.size(12.dp),
                    tint = AppTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${campaign.reelCount.coerceAtLeast(1)}",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.textPrimary
                )

                // Optional Photo / Carousel deliverable
                val photoCount = if (campaign.photoCount > 0) campaign.photoCount else if (campaign.id.hashCode() % 2 == 0) 3 else 0
                if (photoCount > 0) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "•",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_deliverable_photo),
                        contentDescription = "Photo",
                        modifier = Modifier.size(11.dp),
                        tint = AppTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$photoCount",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun CampaignListItem(
    campaign: Campaign,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.surfaceVariant)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = campaign.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )
        
        // Overlay Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = campaign.brandName,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = AppTheme.colors.primary
                ) {
                    Text(
                        text = campaign.reward,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = campaign.title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                lineHeight = 26.sp
            )
            
            if (campaign.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = campaign.description,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    maxLines = 2,
                    lineHeight = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = campaign.category,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = campaign.status,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
