package com.example.refluenceds.ui.screens

import android.content.Intent
import android.net.Uri
import com.example.refluenceds.utils.Constants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import com.example.refluenceds.ui.components.AppPullToRefreshBox
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
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.refluenceds.R
import com.example.refluenceds.ui.viewmodel.CampaignViewModel

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.text.TextStyle
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.viewmodel.AuthViewModel
import com.example.refluenceds.utils.SetStatusBarAppearance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarningsScreen(
    viewModel: CampaignViewModel,
    authViewModel: AuthViewModel? = null,
    onNavigateToCampaigns: () -> Unit = {},
    onNavigateToYourCampaigns: () -> Unit = {},
    onNavigateToYourCollection: () -> Unit = {},
    onNavigateToMyBrands: () -> Unit = {},
    onNavigateToAcademy: () -> Unit = {},
    onNavigateToYourReferrals: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToContactUs: () -> Unit = {},
    onNavigateToCashEarned: () -> Unit = {},
    onNavigateToWaysToEarn: () -> Unit = {},
    onNavigateToUgcInfo: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        authViewModel?.fetchUserProfile()
        authViewModel?.fetchMyReferralCodeAndStats()
        viewModel.fetchMyBadges()
    }

    val userProfile by authViewModel?.userProfile?.collectAsState() ?: remember { mutableStateOf(null) }
    val stats by authViewModel?.referralStats?.collectAsState() ?: remember { mutableStateOf(null) }

    val displayName = remember(userProfile) {
        val u = userProfile
        val combined = "${u?.firstName.orEmpty()} ${u?.lastName.orEmpty()}".trim()
        if (combined.isNotEmpty()) combined
        else if (!u?.fullName.isNullOrBlank()) u!!.fullName!!
        else "User"
    }

    val photoUrls = remember(userProfile) {
        val photos = userProfile?.getAllPhotos() ?: emptyList()
        if (photos.isEmpty()) {
            val single = userProfile?.avatar
            if (!single.isNullOrBlank()) listOf(single) else emptyList()
        } else {
            photos
        }
    }

    val pagerState = rememberPagerState(pageCount = { photoUrls.size })
    val listState = rememberLazyListState()
    val statusBarAlpha by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0) 1f
            else (listState.firstVisibleItemScrollOffset.toFloat() / 150f).coerceIn(0f, 1f)
        }
    }
    SetStatusBarAppearance(isLightStatusBars = statusBarAlpha > 0.5f)

    val myReferralCode = stats?.getEffectiveCode() ?: userProfile?.referral?.getEffectiveCode() ?: userProfile?.referralCode
    val hasAppliedReferral = userProfile?.referral?.hasApplied == true || userProfile?.hasAppliedReferral == true
    val hasSkippedReferral = userProfile?.referral?.hasSkipped == true || userProfile?.hasSkippedReferral == true
    var showReferralCard by remember { mutableStateOf(true) }
    val shouldShowReferralCard = !hasAppliedReferral && !hasSkippedReferral && showReferralCard
    var showFeedbackSheet by remember { mutableStateOf(false) }
    var showPodcastDialog by remember { mutableStateOf(false) }
    var feedbackStep by remember { mutableStateOf(0) }
    val textGradientBrush = Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)))

    val isAuthLoading by authViewModel?.isLoading?.collectAsState() ?: remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        AppPullToRefreshBox(
            isRefreshing = isAuthLoading,
            onRefresh = {
                authViewModel?.fetchUserProfile()
                authViewModel?.fetchMyReferralCodeAndStats()
                viewModel.fetchMyBadges()
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 110.dp)
            ) {
                // Header Profile Image Carousel with Edit Profile Button
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(420.dp)
                    ) {
                        if (photoUrls.isNotEmpty()) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(photoUrls[page])
                                        .crossfade(true)
                                        .error(R.drawable.ic_broken_image)
                                        .fallback(R.drawable.ic_broken_image)
                                        .build(),
                                    contentDescription = "Profile Cover",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(AppTheme.colors.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile Cover Placeholder",
                                    tint = AppTheme.colors.primary,
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                        }
                        
                        // Top gradient overlay so guidelines are visible over bright photos
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .align(Alignment.TopCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent)
                                    )
                                )
                        )

                        // Gradient overlay at bottom of header image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                                    )
                                )
                        )

                        // Top Segment Lines / Image Guide for Multiple Photos
                        if (photoUrls.size > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter)
                                    .statusBarsPadding()
                                    .padding(top = 10.dp, start = 16.dp, end = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                            repeat(photoUrls.size) { index ->
                                val isCurrent = pagerState.currentPage == index
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            if (isCurrent) Color.White else Color.White.copy(alpha = 0.35f)
                                        )
                                )
                            }
                        }
                    }

                    // Edit Profile Button at bottom center of cover image
                    Surface(
                        onClick = { onNavigateToEditProfile() },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp),
                        shape = RoundedCornerShape(50),
                        color = Color.Black.copy(alpha = 0.25f),
                        border = BorderStroke(1.5.dp, Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Edit Profile",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            // User Name Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = displayName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                    if (!userProfile?.bio.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = userProfile!!.bio!!,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = AppTheme.colors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = AppTheme.colors.divider,
                        thickness = 1.dp
                    )
                }
            }

            // Your Socials Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Your socials",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AppTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    ProfileSocialCard(title = "Instagram", action = "Connect", iconRes = R.drawable.ic_social_instagram)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileSocialCard(title = "TikTok", action = "Connect", iconRes = R.drawable.ic_social_tiktok)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileSocialCard(title = "UGC", action = "Apply", iconRes = R.drawable.ic_social_video, onClick = onNavigateToUgcInfo)
                }
            }

            // Your Reviews Box
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = AppTheme.colors.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your reviews",
                            fontSize = 13.sp,
                            color = AppTheme.colors.textPrimary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "You haven't been reviewed yet.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(brush = textGradientBrush)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            onClick = { onNavigateToCampaigns() },
                            shape = RoundedCornerShape(50),
                            color = AppTheme.colors.surface,
                            border = BorderStroke(1.dp, AppTheme.colors.border)
                        ) {
                            Text(
                                text = "Apply for a campaign",
                                color = AppTheme.colors.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            }

            // Invite your friends & earn Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clickable { onNavigateToYourReferrals() },
                    shape = RoundedCornerShape(16.dp),
                    color = AppTheme.colors.surface,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, AppTheme.colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_smile_plus),
                            contentDescription = null,
                            tint = Color(0xFFC03A82),
                            modifier = Modifier
                                .size(24.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Invite your friends & earn",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                style = TextStyle(brush = textGradientBrush)
                            )
                            if (!myReferralCode.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Your code: $myReferralCode",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = AppTheme.colors.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Every friend that applies for a campaign after signing up with your code earns both of you 20 EUR.",
                                fontSize = 13.sp,
                                color = AppTheme.colors.textSecondary,
                                lineHeight = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = AppTheme.colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Were you referred? Card
            if (shouldShowReferralCard) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = AppTheme.colors.surface,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (AppTheme.isDark) Color(0xFF351528) else Color(0xFFFDF4F8),
                                    border = BorderStroke(1.dp, if (AppTheme.isDark) Color(0xFF5A1E40) else Color(0xFFF6E6EE))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Timer,
                                            contentDescription = null,
                                            tint = Color(0xFFEC4899),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "7 DAYS LEFT",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            style = TextStyle(brush = textGradientBrush)
                                        )
                                    }
                                }
                                
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = AppTheme.colors.textSecondary,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { showReferralCard = false }
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Were you referred?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = AppTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Add your referrer code for bonus benefits",
                                fontSize = 13.sp,
                                color = AppTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }

            // Cash Earned Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clickable { onNavigateToCashEarned() },
                    shape = RoundedCornerShape(16.dp),
                    color = AppTheme.colors.surface,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, AppTheme.colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "USD 0,00",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Cash Earned",
                                fontSize = 13.sp,
                                color = AppTheme.colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onNavigateToWaysToEarn() }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = AppTheme.colors.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ways to Earn",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.primary
                                )
                            }
                        }
                        
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = AppTheme.colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Questions? We're here. Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clickable {
                            onNavigateToContactUs()
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = AppTheme.colors.surface,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, AppTheme.colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (AppTheme.isDark) Color(0xFF0E2E1E) else Color(0xFFE8F8F0)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ChatBubbleOutline,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "NEED HELP?",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Questions? We're here.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = AppTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Chat with us to find answers or get support.",
                                fontSize = 13.sp,
                                color = AppTheme.colors.textSecondary
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = AppTheme.colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Menu Items List Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = AppTheme.colors.surface,
                    border = BorderStroke(1.dp, AppTheme.colors.border)
                ) {
                    Column {
                        ProfileMenuItem(
                            title = "Help us improve the app",
                            iconRes = R.drawable.ic_help_bubble,
                            onClick = {
                                feedbackStep = 0
                                showFeedbackSheet = true
                            }
                        )
                        HorizontalDivider(color = AppTheme.colors.divider)
                        ProfileMenuItem(
                            title = "Your campaigns",
                            iconRes = R.drawable.ic_nav_megaphone_outline,
                            onClick = { onNavigateToYourCampaigns() }
                        )
                        HorizontalDivider(color = AppTheme.colors.divider)
                        ProfileMenuItem("Your collection", icon = Icons.Outlined.BookmarkBorder, onClick = { onNavigateToYourCollection() })
                        HorizontalDivider(color = AppTheme.colors.divider)
                        ProfileMenuItem("My Brands", icon = Icons.Outlined.Business, onClick = { onNavigateToMyBrands() })
                        HorizontalDivider(color = AppTheme.colors.divider)
                        ProfileMenuItem("Refluenced Academy", icon = Icons.Outlined.School, onClick = { onNavigateToAcademy() })
                        HorizontalDivider(color = AppTheme.colors.divider)
                        ProfileMenuItem("Your Referrals", iconRes = R.drawable.ic_smile_plus, onClick = { onNavigateToYourReferrals() })
                        HorizontalDivider(color = AppTheme.colors.divider)
                        ProfileMenuItem("Settings", icon = Icons.Outlined.Settings, onClick = { onNavigateToSettings() })
                    }
                }
            }

            // Bottom Promo Section (Podcast Card & Social Follow Cards)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Left Column: Podcast Card
                    Surface(
                        onClick = { showPodcastDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(280.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = AppTheme.colors.surface,
                        border = BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = "https://picsum.photos/seed/podcast/400/500",
                                contentDescription = "Podcast",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFD8437D))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Our Podcast",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Refluenced -\nOffline Talks",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 18.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Right Column: Social Follow Cards
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Instagram Follow Card
                        SocialFollowCard(
                            title = "Follow us on\nInstagram",
                            iconRes = R.drawable.ic_social_instagram,
                            url = Constants.REFLUENCED_INSTAGRAM_URL
                        )

                        // TikTok Follow Card
                        SocialFollowCard(
                            title = "Follow us on\nTikTok",
                            iconRes = R.drawable.ic_social_tiktok,
                            url = Constants.REFLUENCED_TIKTOK_URL
                        )
                    }
                }
            }

            // App Version Footer
            item {
                Text(
                    text = "App version: 2.2.9 (254)",
                    color = AppTheme.colors.textSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp, bottom = 12.dp, start = 20.dp)
                )
            }
        }

        // Top Status Bar Background Overlay that appears on scroll
        if (statusBarAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(AppTheme.colors.surface.copy(alpha = statusBarAlpha))
            )
        }
        }
    }
}

    if (showFeedbackSheet) {
        FeedbackBottomSheet(
            authViewModel = authViewModel,
            onDismissRequest = { showFeedbackSheet = false },
            feedbackStep = feedbackStep,
            onStepChange = { feedbackStep = it },
            onNavigateToContactUs = {
                showFeedbackSheet = false
                onNavigateToContactUs()
            }
        )
    }

    if (showPodcastDialog) {
        OurPodcastBottomSheet(onDismiss = { showPodcastDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackBottomSheet(
    authViewModel: AuthViewModel? = null,
    onDismissRequest: () -> Unit,
    feedbackStep: Int,
    onStepChange: (Int) -> Unit,
    onNavigateToContactUs: () -> Unit = {}
) {
    var feedbackText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = AppTheme.colors.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .background(AppTheme.colors.border, shape = RoundedCornerShape(2.dp))
            )
        },
        sheetMaxWidth = androidx.compose.ui.unit.Dp.Unspecified
    ) {
        // Header for Step 0 and Step 1
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cancel",
                        color = AppTheme.colors.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        modifier = Modifier.clickable { onDismissRequest() }
                    )
                    Text(
                        text = "Feedback",
                        color = AppTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (feedbackStep == 1) {
                        Text(
                            text = "Back",
                            color = AppTheme.colors.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            modifier = Modifier.clickable { onStepChange(0) }
                        )
                    } else {
                        Spacer(modifier = Modifier.width(44.dp))
                    }
                }

                HorizontalDivider(color = AppTheme.colors.divider, thickness = 1.dp)

                if (feedbackStep == 0) {
                    // Step 0: Choose Option (Screenshot 1)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Choose one of the options below",
                            color = AppTheme.colors.textSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // App Card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onStepChange(1) },
                            shape = RoundedCornerShape(16.dp),
                            color = AppTheme.colors.surface,
                            border = BorderStroke(1.dp, AppTheme.colors.border)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(AppTheme.colors.primary.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_tools_wrench),
                                            contentDescription = null,
                                            tint = AppTheme.colors.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "App",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = AppTheme.colors.textPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Bugs, errors, sync issues, feature requests...",
                                            fontSize = 13.sp,
                                            color = AppTheme.colors.textSecondary
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = AppTheme.colors.textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Chat Support Card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToContactUs() },
                            shape = RoundedCornerShape(16.dp),
                            color = AppTheme.colors.surface,
                            border = BorderStroke(1.dp, AppTheme.colors.border)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(AppTheme.colors.primary.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_chat_question),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "Chat Support",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = AppTheme.colors.textPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Missing product, deadline extension, ...",
                                            fontSize = 13.sp,
                                            color = AppTheme.colors.textSecondary
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = AppTheme.colors.textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                } else if (feedbackStep == 1) {
                    // Step 1: Write Feedback Form (Screenshot 2)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Help us make the app better! Your feedback will be received directly by our developers.",
                            color = AppTheme.colors.textSecondary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            placeholder = { Text("Write your feedback here...", color = AppTheme.colors.textTertiary, fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = AppTheme.colors.textPrimary,
                                unfocusedTextColor = AppTheme.colors.textPrimary,
                                focusedBorderColor = AppTheme.colors.primary,
                                unfocusedBorderColor = AppTheme.colors.border,
                                focusedContainerColor = AppTheme.colors.surfaceVariant,
                                unfocusedContainerColor = AppTheme.colors.surfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (feedbackText.trim().isEmpty()) {
                                    android.widget.Toast.makeText(context, "Please enter your feedback", android.widget.Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (authViewModel != null) {
                                    isSubmitting = true
                                    authViewModel.submitFeedback(
                                        feedback = feedbackText.trim(),
                                        onSuccess = { msg ->
                                            isSubmitting = false
                                            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                                            onDismissRequest()
                                        },
                                        onError = { err ->
                                            isSubmitting = false
                                            android.widget.Toast.makeText(context, err, android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                } else {
                                    onDismissRequest()
                                }
                            },
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
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
                                if (isSubmitting) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(22.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Submit",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
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
fun ProfileSocialCard(title: String, action: String, iconRes: Int, onClick: () -> Unit = {}) {
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
                onClick = onClick,
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
fun ProfileMenuItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconRes: Int? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = AppTheme.colors.textPrimary,
                    modifier = Modifier.size(22.dp)
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppTheme.colors.textPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = AppTheme.colors.textPrimary
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = AppTheme.colors.textSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}


@Composable
fun SocialFollowCard(
    title: String,
    iconRes: Int,
    url: String = ""
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = AppTheme.colors.surface,
        border = BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color(0xFFE55589),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = AppTheme.colors.textPrimary,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = {
                    if (url.isNotBlank()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                },
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, AppTheme.colors.primary),
                modifier = Modifier.height(34.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "Follow",
                    color = AppTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OurPodcastBottomSheet(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Top Cover Image
            AsyncImage(
                model = "https://picsum.photos/seed/podcast/600/400",
                contentDescription = "Our Podcast",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentScale = ContentScale.Crop
            )

            // Pink Header Title Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD8437D))
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Column {
                    Text(
                        text = "Our Podcast",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Refluenced - Offline Talks",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Platform Options (Youtube, Spotify, Apple Podcasts)
            PodcastPlatformOption(
                title = "Youtube",
                iconRes = R.drawable.ic_podcast_youtube,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PODCAST_YOUTUBE_URL)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    onDismiss()
                }
            )

            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

            PodcastPlatformOption(
                title = "Spotify",
                iconRes = R.drawable.ic_podcast_spotify,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PODCAST_SPOTIFY_URL)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    onDismiss()
                }
            )

            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

            PodcastPlatformOption(
                title = "Apple Podcasts",
                iconRes = R.drawable.ic_podcast_apple,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PODCAST_APPLE_URL)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PodcastPlatformOption(
    title: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            color = Color(0xFF5B61F4),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
