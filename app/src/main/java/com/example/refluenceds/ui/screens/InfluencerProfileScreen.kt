package com.example.refluenceds.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.refluenceds.R
import com.example.refluenceds.data.remote.dto.ContentFeedItemDto
import com.example.refluenceds.data.remote.dto.InfluencerSocialPostDto
import com.example.refluenceds.ui.components.SkeletonItem
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.viewmodel.CampaignViewModel

data class InfluencerCampaignFilter(
    val id: String,
    val title: String,
    val pieceCount: String,
    val thumbnailUrl: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfluencerProfileScreen(
    creatorId: String = "",
    creatorName: String = "",
    viewModel: CampaignViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit,
    onNavigateToReviews: (creatorId: String, creatorName: String) -> Unit = { _, _ -> },
    onNavigateToCampaignDetail: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var isBioExpanded by remember { mutableStateOf(false) }
    var isCampaignsExpanded by remember { mutableStateOf(false) }
    var selectedCampaignFilter by remember { mutableStateOf("All") }
    var selectedSocialTab by remember { mutableStateOf(0) } // 0: Instagram, 1: TikTok

    val profile by viewModel.influencerProfile.collectAsState()
    val igFeed by viewModel.influencerInstagramFeed.collectAsState()
    val ttFeed by viewModel.influencerTiktokFeed.collectAsState()
    val campaignContent by viewModel.influencerCampaignContent.collectAsState()
    val isLoading by viewModel.isInfluencerLoading.collectAsState()

    LaunchedEffect(creatorId) {
        if (creatorId.isNotBlank()) {
            viewModel.fetchInfluencerData(creatorId)
        }
    }

    val displayName = profile?.name?.takeIf { it.isNotBlank() }
        ?: creatorName.takeIf { it.isNotBlank() }
        ?: "Creator"

    val coverImageUrl = profile?.effectiveCoverPhoto
        ?: profile?.effectiveProfilePicture
        ?: ""

    val city = buildString {
        profile?.city?.takeIf { it.isNotBlank() }?.let { append(it) }
        val emoji = profile?.country?.emoji
        val countryName = profile?.country?.name
        if (!emoji.isNullOrBlank()) {
            if (isNotEmpty()) append(" ")
            append(emoji)
        } else if (!countryName.isNullOrBlank()) {
            if (isNotEmpty()) append(", ")
            append(countryName)
        }
    }

    val industriesText = profile?.industries
        ?.mapNotNull { it.name?.takeIf { n -> n.isNotBlank() } }
        ?.joinToString(", ")
        ?: ""

    val bioText = profile?.bio.orEmpty()

    val igProfile = profile?.instagramProfile
    val ttProfile = profile?.tiktokProfile
    val igStats = profile?.instagramStats
    val ttStats = profile?.tiktokStats

    val reviewsSummary = profile?.reviewsSummary
    val ratingValue = reviewsSummary?.averageRating ?: profile?.rating ?: 5.0
    val ratingCount = reviewsSummary?.ratingsCount ?: profile?.completedCampaignsCount ?: 0
    val ratingFormatted = reviewsSummary?.formatted ?: "${"%.1f".format(ratingValue)}/5 ($ratingCount)"

    val serverBrandChips = profile?.campaignContent?.brandChips
    val serverCampaignItems = profile?.campaignContent?.items
    val effectiveCampaignItems = if (!serverCampaignItems.isNullOrEmpty()) serverCampaignItems else campaignContent

    // Build dynamic campaign filters from brand_chips or content items
    val campaignFilters = remember(serverBrandChips, effectiveCampaignItems) {
        if (!serverBrandChips.isNullOrEmpty()) {
            serverBrandChips.map { chip ->
                InfluencerCampaignFilter(
                    id = chip.brandName ?: "All",
                    title = chip.brandName ?: "All",
                    pieceCount = "${chip.piecesCount ?: 0} PIECES",
                    thumbnailUrl = chip.logo ?: effectiveCampaignItems.firstOrNull()?.thumbnailUrl ?: effectiveCampaignItems.firstOrNull()?.mediaUrl ?: ""
                )
            }
        } else {
            val list = mutableListOf<InfluencerCampaignFilter>()
            val totalCount = effectiveCampaignItems.size
            val firstThumb = effectiveCampaignItems.firstOrNull()?.thumbnailUrl
                ?: effectiveCampaignItems.firstOrNull()?.mediaUrl
                ?: ""

            if (totalCount > 0) {
                list.add(
                    InfluencerCampaignFilter(
                        id = "All",
                        title = "All",
                        pieceCount = "$totalCount PIECES",
                        thumbnailUrl = firstThumb
                    )
                )
            }

            val brandGroups = effectiveCampaignItems.groupBy { it.effectiveBrandName }
            brandGroups.forEach { (brandName, items) ->
                if (brandName.isNotBlank()) {
                    list.add(
                        InfluencerCampaignFilter(
                            id = brandName,
                            title = brandName,
                            pieceCount = "${items.size} PIECES",
                            thumbnailUrl = items.firstOrNull()?.thumbnailUrl ?: items.firstOrNull()?.mediaUrl ?: ""
                        )
                    )
                }
            }
            list
        }
    }

    val filteredCampaignItems = remember(effectiveCampaignItems, selectedCampaignFilter) {
        if (selectedCampaignFilter.equals("All", ignoreCase = true) || selectedCampaignFilter.isBlank()) {
            effectiveCampaignItems
        } else {
            effectiveCampaignItems.filter { it.effectiveBrandName.equals(selectedCampaignFilter, ignoreCase = true) }
        }
    }

    Scaffold(
        containerColor = AppTheme.colors.background
    ) { innerPadding ->
        if (isLoading && profile == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SkeletonItem(modifier = Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(16.dp)))
                SkeletonItem(modifier = Modifier.fillMaxWidth(0.5f).height(28.dp).clip(RoundedCornerShape(8.dp)))
                SkeletonItem(modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(12.dp)))
                SkeletonItem(modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(16.dp)))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .verticalScroll(scrollState)
            ) {
                // ── 1. Top Cover Photo & Floating Back Arrow ─────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    if (coverImageUrl.isNotBlank()) {
                        AsyncImage(
                            model = coverImageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(AppTheme.colors.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = AppTheme.colors.textSecondary,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }

                    // Top Floating Back Arrow
                    Surface(
                        onClick = onBack,
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.4f),
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(start = 16.dp, top = 8.dp)
                            .size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // ── 2. Profile Info Section ──────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = displayName,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Active status line
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Active creator",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Chips Row
                    if (city.isNotBlank() || industriesText.isNotBlank()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (city.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = AppTheme.colors.surface,
                                    border = BorderStroke(1.dp, AppTheme.colors.border)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = AppTheme.colors.textSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = city,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                    }
                                }
                            }

                            if (industriesText.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = AppTheme.colors.surface,
                                    border = BorderStroke(1.dp, AppTheme.colors.border)
                                ) {
                                    Text(
                                        text = industriesText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppTheme.colors.textPrimary,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // BIO Section
                    if (bioText.isNotBlank()) {
                        Text(
                            text = "BIO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isBioExpanded || bioText.length <= 120) bioText
                            else bioText.take(120) + "...",
                            fontSize = 14.sp,
                            color = AppTheme.colors.textSecondary,
                            lineHeight = 20.sp
                        )

                        if (bioText.length > 120) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBioExpanded) "Show less" else "Show more",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.primary,
                                modifier = Modifier.clickable { isBioExpanded = !isBioExpanded }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // ── 3. Social Metrics Cards ──────────────────────────────────
                    // Instagram Card
                    if (igProfile != null || igStats != null || profile?.handle != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = AppTheme.colors.surface,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, AppTheme.colors.border)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_social_instagram),
                                        contentDescription = null,
                                        tint = AppTheme.colors.textPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Instagram", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.textPrimary)
                                        Text(
                                            igProfile?.handle ?: profile?.handle ?: displayName.lowercase().replace(" ", ""),
                                            fontSize = 11.sp,
                                            color = AppTheme.colors.primary
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Groups, null, tint = Color(0xFF7C5CF6), modifier = Modifier.size(18.dp))
                                        Text(
                                            igProfile?.followersFormatted ?: igStats?.followers ?: "-",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Outlined.ChatBubbleOutline, null, tint = Color(0xFF7C5CF6), modifier = Modifier.size(18.dp))
                                        Text(
                                            igProfile?.engagementRateText ?: igStats?.engagementRate ?: "-",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Outlined.Visibility, null, tint = Color(0xFF7C5CF6), modifier = Modifier.size(18.dp))
                                        Text(
                                            igProfile?.avgViewsFormatted ?: igStats?.avgLikes ?: igStats?.totalPosts?.toString() ?: "-",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // TikTok Card
                    if (ttProfile != null || ttStats != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = AppTheme.colors.surface,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, AppTheme.colors.border)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_social_tiktok),
                                        contentDescription = null,
                                        tint = AppTheme.colors.textPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("TikTok", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.textPrimary)
                                        Text(
                                            ttProfile?.handle ?: profile?.handle ?: displayName.lowercase().replace(" ", ""),
                                            fontSize = 11.sp,
                                            color = AppTheme.colors.primary
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Groups, null, tint = Color(0xFF7C5CF6), modifier = Modifier.size(18.dp))
                                        Text(
                                            ttProfile?.followersFormatted ?: ttStats?.followers ?: "-",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Bolt, null, tint = Color(0xFF7C5CF6), modifier = Modifier.size(18.dp))
                                        Text(
                                            ttProfile?.engagementRateText ?: ttStats?.engagementRate ?: "-",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Outlined.Visibility, null, tint = Color(0xFF7C5CF6), modifier = Modifier.size(18.dp))
                                        Text(
                                            ttProfile?.avgViewsFormatted ?: ttStats?.avgLikes ?: ttStats?.totalPosts?.toString() ?: "-",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // ── 4. Reviews Card ──────────────────────────────────────────
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val cid = profile?.id?.toString()?.takeIf { it.isNotBlank() } ?: creatorId
                                onNavigateToReviews(cid, displayName)
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = AppTheme.colors.surface,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("$displayName's Reviews", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.textPrimary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val starColors = listOf(
                                        Color(0xFF5B61F4),
                                        Color(0xFF7C3AED),
                                        Color(0xFF9333EA),
                                        Color(0xFFC026D3),
                                        Color(0xFFE11D48)
                                    )
                                    val clampedStars = ratingValue.toInt().coerceIn(0, 5)
                                    starColors.forEachIndexed { idx, color ->
                                        val isFilled = if (ratingCount > 0) idx < clampedStars else false
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (isFilled) color else (if (AppTheme.isDark) Color(0xFF334155) else Color(0xFFCBD5E1)),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        if (ratingCount > 0) ratingFormatted else "No reviews yet",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View Reviews",
                                tint = AppTheme.colors.textPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // ── 5. Campaign Content Section ──────────────────────────────
                    if (filteredCampaignItems.isNotEmpty() || (profile?.portfolioPhotos?.isNotEmpty() == true)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Campaign content",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = AppTheme.colors.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Campaign, null, tint = AppTheme.colors.textSecondary, modifier = Modifier.size(16.dp))
                                    Text(" ${filteredCampaignItems.size} ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.textPrimary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Filter Row
                        if (campaignFilters.size > 1) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(campaignFilters) { filter ->
                                    val isSelected = filter.title == selectedCampaignFilter
                                    Surface(
                                        onClick = { selectedCampaignFilter = filter.title },
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isSelected) AppTheme.colors.surfaceVariant else AppTheme.colors.surface,
                                        border = BorderStroke(1.dp, if (isSelected) AppTheme.colors.primary else AppTheme.colors.border),
                                        modifier = Modifier.width(110.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                            ) {
                                                if (filter.thumbnailUrl.isNotBlank()) {
                                                    AsyncImage(
                                                        model = filter.thumbnailUrl,
                                                        contentDescription = null,
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                } else {
                                                    Box(
                                                        modifier = Modifier.fillMaxSize().background(AppTheme.colors.surfaceVariant),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(Icons.Default.PhotoLibrary, null, tint = AppTheme.colors.textSecondary)
                                                    }
                                                }

                                                if (isSelected) {
                                                    Box(
                                                        modifier = Modifier
                                                            .align(Alignment.TopEnd)
                                                            .padding(4.dp)
                                                            .size(18.dp)
                                                            .background(AppTheme.colors.primary, CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = filter.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AppTheme.colors.textPrimary,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = filter.pieceCount,
                                                fontSize = 10.sp,
                                                color = AppTheme.colors.textSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // Campaign 3-Column Grid
                        val visibleItems = if (isCampaignsExpanded) filteredCampaignItems else filteredCampaignItems.take(9)

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            visibleItems.chunked(3).forEach { rowPosts ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    rowPosts.forEach { post ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(160.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                        ) {
                                            AsyncImage(
                                                model = post.effectiveThumbnailUrl.ifBlank { post.effectiveMediaUrl },
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )

                                            // Platform icon
                                            Icon(
                                                painter = painterResource(
                                                    id = if (post.platform?.lowercase() == "tiktok") R.drawable.ic_social_tiktok else R.drawable.ic_social_instagram
                                                ),
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(8.dp)
                                                    .size(16.dp)
                                            )

                                            if (post.mediaType != "image") {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    tint = Color.White.copy(alpha = 0.9f),
                                                    modifier = Modifier
                                                        .align(Alignment.Center)
                                                        .size(28.dp)
                                                )
                                            }

                                            // Bottom Scrim & Stats
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(40.dp)
                                                    .align(Alignment.BottomCenter)
                                                    .background(
                                                        Brush.verticalGradient(
                                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                                        )
                                                    )
                                            )

                                            Row(
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Outlined.FavoriteBorder, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                Text(" ${post.likesCount ?: 0} ", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Icon(Icons.Outlined.ChatBubbleOutline, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                Text(" ${post.commentsCount ?: 0}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    if (rowPosts.size < 3) {
                                        repeat(3 - rowPosts.size) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }

                        if (filteredCampaignItems.size > 9) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Surface(
                                onClick = { isCampaignsExpanded = !isCampaignsExpanded },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = AppTheme.colors.surface,
                                border = BorderStroke(1.dp, AppTheme.colors.border)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isCampaignsExpanded) "Show less" else "Show more",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = if (isCampaignsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = AppTheme.colors.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // ── 6. Social Feed Section ──────────────────────────────────
                    val currentFeed = if (selectedSocialTab == 0) igFeed else ttFeed

                    Text(
                        text = "Social feed",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tab Switcher Bar
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Instagram Tab
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedSocialTab = 0 },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_social_instagram),
                                    contentDescription = null,
                                    tint = if (selectedSocialTab == 0) AppTheme.colors.primary else AppTheme.colors.textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Instagram",
                                    fontSize = 16.sp,
                                    fontWeight = if (selectedSocialTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedSocialTab == 0) AppTheme.colors.primary else AppTheme.colors.textSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.5.dp)
                                    .background(if (selectedSocialTab == 0) AppTheme.colors.primary else AppTheme.colors.border)
                            )
                        }

                        // TikTok Tab
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedSocialTab = 1 },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_social_tiktok),
                                    contentDescription = null,
                                    tint = if (selectedSocialTab == 1) AppTheme.colors.primary else AppTheme.colors.textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "TikTok",
                                    fontSize = 16.sp,
                                    fontWeight = if (selectedSocialTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedSocialTab == 1) AppTheme.colors.primary else AppTheme.colors.textSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.5.dp)
                                    .background(if (selectedSocialTab == 1) AppTheme.colors.primary else AppTheme.colors.border)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (currentFeed.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No social posts available",
                                fontSize = 14.sp,
                                color = AppTheme.colors.textSecondary
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            currentFeed.chunked(3).forEach { rowPosts ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    rowPosts.forEach { post ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(160.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                        ) {
                                            AsyncImage(
                                                model = post.thumbnailUrl ?: post.mediaUrl ?: "",
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )

                                            Icon(
                                                painter = painterResource(
                                                    id = if (selectedSocialTab == 0) R.drawable.ic_social_instagram else R.drawable.ic_social_tiktok
                                                ),
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(8.dp)
                                                    .size(16.dp)
                                            )

                                            // Play icon
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White.copy(alpha = 0.9f),
                                                modifier = Modifier
                                                    .align(Alignment.Center)
                                                    .size(28.dp)
                                            )

                                            // Bottom Scrim & Stats
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(40.dp)
                                                    .align(Alignment.BottomCenter)
                                                    .background(
                                                        Brush.verticalGradient(
                                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                                        )
                                                    )
                                            )

                                            Row(
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Outlined.FavoriteBorder, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                Text(" ${post.likes ?: 0} ", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Icon(Icons.Outlined.ChatBubbleOutline, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                Text(" ${post.comments ?: 0}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    if (rowPosts.size < 3) {
                                        repeat(3 - rowPosts.size) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

