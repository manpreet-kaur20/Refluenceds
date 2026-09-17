package com.example.refluenceds.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import com.example.refluenceds.ui.components.AppPullToRefreshBox
import com.example.refluenceds.utils.Constants
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.refluenceds.R
import com.example.refluenceds.data.remote.dto.BrandItemDto
import com.example.refluenceds.domain.model.Campaign
import com.example.refluenceds.ui.components.SkeletonItem
import com.example.refluenceds.ui.components.StoryItem
import com.example.refluenceds.ui.components.StoryViewerDialog
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.theme.GradientEnd
import com.example.refluenceds.ui.theme.GradientStart
import com.example.refluenceds.ui.viewmodel.AuthViewModel
import com.example.refluenceds.ui.viewmodel.CampaignViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CampaignViewModel,
    authViewModel: AuthViewModel? = null,
    onViewAcademyAll: () -> Unit = {},
    onViewCampaignsAll: () -> Unit = {},
    onViewBrandsAll: () -> Unit = {},
    onNavigateToAcademyDetail: (String) -> Unit = {},
    onNavigateToCampaignDetail: (String) -> Unit = {},
    onNavigateToYourReferrals: () -> Unit = {},
    onNavigateToBrandDetail: (String, String) -> Unit = { _, _ -> }
) {
    val tutorials by viewModel.tutorials.collectAsState()
    val campaigns by viewModel.campaigns.collectAsState()
    val homeData by viewModel.homeData.collectAsState()
    val isHomeLoading by viewModel.isHomeLoading.collectAsState()
    val myBrands by viewModel.myBrands.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var showReferralSheet by remember { mutableStateOf(false) }
    var activeStoryList by remember { mutableStateOf<List<StoryItem>?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchHomeData()
        viewModel.fetchCampaigns()
        viewModel.fetchInboxSummary()
        authViewModel?.fetchMyReferralCodeAndStats()
        authViewModel?.fetchUserProfile()
    }

    val stats by authViewModel?.referralStats?.collectAsState() ?: remember { mutableStateOf(null) }
    val userProfile by authViewModel?.userProfile?.collectAsState() ?: remember { mutableStateOf(null) }

    val effectiveCode = stats?.getEffectiveCode()?.takeIf { it.isNotBlank() }
        ?: userProfile?.referral?.getEffectiveCode()?.takeIf { it.isNotBlank() }
        ?: userProfile?.referralCode?.takeIf { it.isNotBlank() }
        ?: ""

    val effectiveShareUrl = stats?.getEffectiveShareUrl()?.takeIf { it.isNotBlank() }
        ?: userProfile?.referral?.shareUrl?.takeIf { it.isNotBlank() }
        ?: if (effectiveCode.isNotBlank()) "http://162.241.68.61/refluenced/invite/$effectiveCode" else ""

    val rewardAmount = homeData?.referralBanner?.rewardAmount
        ?: stats?.getEffectiveRewards()?.takeIf { it.isNotBlank() && it != "0.00 CHF" }
        ?: "€20.00"

    val referralTitle = homeData?.referralBanner?.title
        ?: "Refer a Friend and Earn!"

    val exploreBrandsList = remember(homeData, myBrands) {
        val brandsFromHome = homeData?.exploreBrands
        if (!brandsFromHome.isNullOrEmpty()) brandsFromHome
        else if (myBrands.isNotEmpty()) myBrands
        else emptyList()
    }

    // Dynamic Stories from API
    // 1. Tutorial: Exactly 1 video
    val tutorialStories = remember(homeData, tutorials) {
        val preview = homeData?.academyPreview
        if (!preview.isNullOrEmpty()) {
            val first = preview.first()
            listOf(
                StoryItem(
                    id = first.id?.toString() ?: "tut_1",
                    title = first.title ?: "Tutorial",
                    imageUrl = first.thumbnailUrl,
                    videoUrl = first.videoUrl,
                    durationSeconds = (first.durationSeconds ?: 8).coerceIn(4, 20)
                )
            )
        } else if (tutorials.isNotEmpty()) {
            val first = tutorials.first()
            listOf(
                StoryItem(
                    id = first.id,
                    title = first.title,
                    imageUrl = first.thumbnailUrl,
                    videoUrl = first.videoUrl,
                    durationSeconds = 8
                )
            )
        } else {
            emptyList()
        }
    }

    // 2. Refluenced: Several videos and stories
    val refluencedStories = remember(homeData, tutorials, campaigns) {
        val list = mutableListOf<StoryItem>()

        val preview = homeData?.academyPreview
        if (!preview.isNullOrEmpty() && preview.size > 1) {
            list.addAll(
                preview.drop(1).map { video ->
                    StoryItem(
                        id = video.id?.toString() ?: "",
                        title = video.title ?: "Refluenced",
                        imageUrl = video.thumbnailUrl,
                        videoUrl = video.videoUrl,
                        durationSeconds = (video.durationSeconds ?: 6).coerceIn(4, 15)
                    )
                }
            )
        } else if (tutorials.size > 1) {
            list.addAll(
                tutorials.drop(1).map { tut ->
                    StoryItem(
                        id = tut.id,
                        title = tut.title,
                        imageUrl = tut.thumbnailUrl,
                        videoUrl = tut.videoUrl,
                        durationSeconds = 6
                    )
                }
            )
        }

        if (campaigns.isNotEmpty()) {
            list.addAll(
                campaigns.take(6).map { c ->
                    StoryItem(
                        id = "camp_${c.id}",
                        title = c.title,
                        imageUrl = c.imageUrl,
                        durationSeconds = 5
                    )
                }
            )
        }
        list
    }

    if (showDialog) {
        InstagramFollowDialog(onDismiss = { showDialog = false })
    }

    if (showReferralSheet) {
        ReferAFriendBottomSheet(
            stats = stats,
            defaultCode = effectiveCode,
            defaultShareUrl = effectiveShareUrl,
            onDismissRequest = { showReferralSheet = false },
            onViewReferralsClick = {
                showReferralSheet = false
                onNavigateToYourReferrals()
            }
        )
    }

    activeStoryList?.let { stories ->
        StoryViewerDialog(
            stories = stories,
            onDismiss = { activeStoryList = null },
            onBookmarkClick = { story ->
                viewModel.toggleBookmark(story.id)
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        containerColor = AppTheme.colors.background
    ) { innerPadding ->
        AppPullToRefreshBox(
            isRefreshing = isHomeLoading,
            onRefresh = {
                viewModel.fetchHomeData()
                viewModel.fetchCampaigns()
                viewModel.fetchTutorials()
                viewModel.fetchInboxSummary()
                authViewModel?.fetchMyReferralCodeAndStats()
                authViewModel?.fetchUserProfile()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
            // Top Circle Options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                TopCircleItem(
                    title = "Tutorial",
                    isTutorial = true,
                    onClick = {
                        if (tutorialStories.isNotEmpty()) {
                            activeStoryList = tutorialStories
                        }
                    }
                )
                TopCircleItem(
                    title = "Refluenced",
                    isTutorial = false,
                    onClick = {
                        if (refluencedStories.isNotEmpty()) {
                            activeStoryList = refluencedStories
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.divider)

            // Refer a Friend Banner (Live API Data)
            ReferralBanner(
                title = referralTitle,
                reward = rewardAmount,
                onReferClick = { showReferralSheet = true }
            )

            HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.divider)

            // Recommended Campaigns Section (Live API Data)
            SectionHeader(
                title = "Recommended campaigns",
                onViewAll = onViewCampaignsAll
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(320.dp)
            ) {
                if (isHomeLoading && campaigns.isEmpty()) {
                    items(3) {
                        SkeletonItem(
                            modifier = Modifier
                                .width(200.dp)
                                .height(240.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                } else if (campaigns.isNotEmpty()) {
                    items(campaigns, key = { it.id }) { campaign ->
                        RecommendedCampaignCard(
                            campaign = campaign,
                            onClick = { onNavigateToCampaignDetail(campaign.id) }
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.divider)

            // Referenced Academy Section (Live API Data)
            SectionHeader(
                title = "Referenced Academy",
                onViewAll = onViewAcademyAll
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.wrapContentHeight()
            ) {
                if (isHomeLoading && tutorials.isEmpty()) {
                    items(3) {
                        SkeletonItem(
                            modifier = Modifier
                                .width(160.dp)
                                .height(240.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                } else if (tutorials.isNotEmpty()) {
                    items(tutorials, key = { it.id }) { tutorial ->
                        AcademyItem(
                            title = tutorial.title,
                            category = tutorial.category,
                            imageUrl = tutorial.thumbnailUrl,
                            duration = tutorial.duration,
                            onClick = { onNavigateToAcademyDetail(tutorial.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Explore Brands Section (Live API Data - Completely Dynamic)
            SectionHeader(
                title = "Explore Brands",
                onViewAll = onViewBrandsAll
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(bottom = 24.dp)
            ) {
                if (isHomeLoading && exploreBrandsList.isEmpty()) {
                    items(3) {
                        SkeletonItem(
                            modifier = Modifier
                                .width(200.dp)
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                } else if (exploreBrandsList.isNotEmpty()) {
                    items(exploreBrandsList, key = { it.id?.toString() ?: it.displayName }) { brand ->
                        BrandCard(
                            brand = brand,
                            onBrandClick = {
                                onNavigateToBrandDetail(brand.id?.toString() ?: "", brand.displayName)
                            },
                            onFollowClick = {
                                brand.id?.let { viewModel.toggleBrandFollow(it) }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(110.dp)) // Padding for floating bottom nav
            }
        }
    }
}

@Composable
fun InstagramFollowDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Box(modifier = Modifier.padding(24.dp)) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Follow us on Instagram to stay up to date!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        style = androidx.compose.ui.text.TextStyle(
                            brush = Brush.linearGradient(listOf(Color(0xFF6C63FF), Color(0xFFFA5252)))
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "We use Instagram to keep you up to date with all the most current campaign information. Join our community today!",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Start,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.REFLUENCED_INSTAGRAM_URL)).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(listOf(Color(0xFF6C63FF), Color(0xFFFA5252))),
                                    shape = RoundedCornerShape(28.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Follow Us", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendedCampaignCard(
    campaign: Campaign,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(180.dp)
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

        Spacer(modifier = Modifier.height(5.dp))

        // Brand Name (Uppercase, grey, bold)
        Text(
            text = campaign.brandName.uppercase().ifBlank { "BRAND" },
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8E8E93),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Campaign Title
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
fun TopCircleItem(
    title: String,
    isTutorial: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(66.dp)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF5B61F4), Color(0xFFEC4899))
                    ),
                    shape = CircleShape
                )
                .padding(3.5.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = Color.White
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isTutorial) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_story_tutorial_cycle),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(1.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.app_icon),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.app_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ReferralBanner(
    title: String = "Refer a Friend and Earn!",
    reward: String = "€20.00",
    onReferClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(if (AppTheme.isDark) Color(0xFF281E34) else Color(0xFFFAF5FF), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_smile_plus),
                contentDescription = null,
                tint = Color(0xFFEC4899),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = reward,
                fontSize = 14.sp,
                color = Color(0xFF8B5CF6),
                fontWeight = FontWeight.Bold
            )
        }
        Surface(
            onClick = onReferClick,
            shape = RoundedCornerShape(50),
            color = AppTheme.colors.surface,
            border = BorderStroke(1.dp, AppTheme.colors.border)
        ) {
            Text(
                text = "Refer",
                color = AppTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, onViewAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (title.contains("Academy")) Icons.Default.School else if (title.contains("Explore")) Icons.Default.Storefront else Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = AppTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textPrimary
            )
        }
        Text(
            text = "View all",
            fontSize = 13.sp,
            color = AppTheme.colors.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onViewAll() }
        )
    }
}

@Composable
fun AcademyItem(
    title: String,
    category: String,
    imageUrl: String,
    duration: String,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AppTheme.colors.surfaceVariant)
        ) {
            if (imageUrl.isNotBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Play Button Overlay
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Duration Overlay
            if (duration.isNotBlank()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = duration,
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Bolt, null, modifier = Modifier.size(12.dp), tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(category, fontSize = 11.sp, color = AppTheme.colors.textSecondary)
        }
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.textPrimary,
            maxLines = 2,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun BrandCard(
    brand: BrandItemDto,
    onBrandClick: () -> Unit = {},
    onFollowClick: () -> Unit = {}
) {
    val isFollowing = brand.isFollowing == true
    val logoUrl = brand.effectiveLogo ?: ""
    val categoryText = brand.effectiveIndustryName.uppercase().ifBlank { "BRAND" }

    Card(
        modifier = Modifier
            .width(220.dp)
            .padding(vertical = 8.dp)
            .clickable { onBrandClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
        border = BorderStroke(1.dp, AppTheme.colors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = if (AppTheme.isDark) 0.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (logoUrl.isNotBlank()) {
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Storefront,
                        contentDescription = null,
                        tint = AppTheme.colors.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = brand.displayName.ifBlank { "Brand" },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = categoryText,
                fontSize = 9.sp,
                color = AppTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            val isDark = AppTheme.isDark
            val followContainerColor = if (isFollowing) {
                if (isDark) Color(0xFF252840) else Color(0xFFEEF2FF)
            } else {
                GradientStart
            }
            val followContentColor = if (isFollowing) {
                if (isDark) Color(0xFF818CF8) else Color(0xFF5B61F4)
            } else {
                Color.White
            }

            Button(
                onClick = onFollowClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = followContainerColor,
                    contentColor = followContentColor
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
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
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
