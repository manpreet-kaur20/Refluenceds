package com.example.refluenceds.ui.screens

import android.content.Intent
import android.widget.Toast
import com.example.refluenceds.data.remote.dto.sanitizeMediaUrl
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.refluenceds.R
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.theme.GradientStart
import com.example.refluenceds.ui.viewmodel.AuthViewModel
import com.example.refluenceds.ui.viewmodel.CampaignViewModel

import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CampaignDetailScreen(
    campaignId: String = "1",
    viewModel: CampaignViewModel,
    authViewModel: AuthViewModel? = null,
    onBack: () -> Unit,
    onNavigateToBrandDetail: (String, String) -> Unit = { _, _ -> },
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToConnectInstagram: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showMenu by remember { mutableStateOf(false) }
    var showBrandMenu by remember { mutableStateOf(false) }
    var showReportSheet by remember { mutableStateOf(false) }
    var selectedReportReason by remember { mutableStateOf<String?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var showApplyDialog by remember { mutableStateOf(false) }
    var showIncompleteProfileDialog by remember { mutableStateOf(false) }
    var isApplied by remember { mutableStateOf(false) }

    val userProfile by (authViewModel?.userProfile ?: remember { MutableStateFlow(null) }).collectAsState()

    val detail by viewModel.campaignDetail.collectAsState()
    val isLoading by viewModel.isCampaignDetailLoading.collectAsState()
    val fallbackList by viewModel.campaigns.collectAsState()
    val fallbackCampaign = remember(fallbackList, campaignId) {
        fallbackList.find { it.id == campaignId } ?: fallbackList.firstOrNull()
    }

    LaunchedEffect(campaignId) {
        if (campaignId.isNotBlank()) {
            viewModel.fetchCampaignDetail(campaignId)
        }
        authViewModel?.fetchUserProfile()
    }

    val brandName = detail?.effectiveBrandName
        ?: fallbackCampaign?.brandName
        ?: "Brand"

    val brandLogo = detail?.effectiveBrandLogo

    val campaignTitle = detail?.title
        ?: fallbackCampaign?.title
        ?: "Campaign"

    val heroImageUrl = detail?.effectiveImageUrl
        ?: fallbackCampaign?.imageUrl
        ?: ""

    val applicationsCount = detail?.effectiveApplicationsCount ?: 0
    val deliverablesSummary = detail?.deliverablesSummary.orEmpty()
    val deadlineText = detail?.effectiveDeadline.orEmpty()
    val startDate = detail?.effectiveStartDate.orEmpty()
    val endDate = detail?.effectiveEndDate.orEmpty()
    val descriptionText = detail?.description ?: detail?.shortDescription ?: fallbackCampaign?.description.orEmpty()
    val requirements = detail?.effectiveRequirements ?: emptyList()
    val deliverables = detail?.deliverables ?: emptyList()
    val compensationText = detail?.effectiveCompensation ?: fallbackCampaign?.reward.orEmpty()
    val galleryImages = detail?.effectiveGalleryImages ?: emptyList()

    fun shareCampaign() {
        val shareText = "Check out $campaignTitle on Refluenceds!\nhttps://refluenceds.com/campaign/$campaignId"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share campaign").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareIntent)
        Toast.makeText(context, "Sharing campaign...", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = AppTheme.colors.background,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .navigationBarsPadding(),
                color = AppTheme.colors.surface,
                shadowElevation = 12.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${if (isApplied) applicationsCount + 1 else applicationsCount}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = AppTheme.colors.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Influencers applied",
                            fontSize = 12.sp,
                            color = AppTheme.colors.textSecondary
                        )
                    }

                    Button(
                        onClick = {
                            if (!isApplied) {
                                val user = userProfile
                                val isComplete = user?.isDetailsComplete == true
                                val isIgConnected = !user?.instagram.isNullOrBlank()

                                if (!isComplete) {
                                    showIncompleteProfileDialog = true
                                } else if (!isIgConnected) {
                                    onNavigateToConnectInstagram()
                                } else {
                                    isApplied = true
                                    showApplyDialog = true
                                    viewModel.applyToCampaign(campaignId)
                                }
                            }
                        },
                        modifier = Modifier
                            .width(150.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color(0xFF4B4FE4), Color(0xFF6B66FF))
                                    ),
                                    shape = RoundedCornerShape(50)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isApplied) "Applied ✓" else "Apply",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // ── 1. Hero Header Banner (Edge to edge) ─────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    if (heroImageUrl.isNotBlank()) {
                        AsyncImage(
                            model = heroImageUrl,
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
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = AppTheme.colors.textSecondary,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }

                    // Scrim gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.45f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )

                    // Top Floating Navigation Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                onClick = onBack,
                                shape = CircleShape,
                                color = AppTheme.colors.surface,
                                modifier = Modifier.size(40.dp),
                                border = BorderStroke(1.dp, AppTheme.colors.border)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = AppTheme.colors.textPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Brand Logo Badge
                            Surface(
                                shape = CircleShape,
                                color = AppTheme.colors.surface,
                                modifier = Modifier.size(40.dp),
                                border = BorderStroke(1.dp, AppTheme.colors.border)
                            ) {
                                if (!brandLogo.isNullOrBlank()) {
                                    AsyncImage(
                                        model = brandLogo,
                                        contentDescription = brandName,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = brandName.take(1).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = AppTheme.colors.textPrimary
                                        )
                                    }
                                }
                            }
                        }

                        // Right Action Buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box {
                                Surface(
                                    onClick = { showMenu = !showMenu },
                                    shape = CircleShape,
                                    color = AppTheme.colors.surface,
                                    modifier = Modifier.size(40.dp),
                                    border = BorderStroke(1.dp, AppTheme.colors.border)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.MoreHoriz,
                                            contentDescription = "More Options",
                                            tint = AppTheme.colors.textPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false },
                                    modifier = Modifier
                                        .width(220.dp)
                                        .background(AppTheme.colors.surface, shape = RoundedCornerShape(16.dp))
                                ) {
                                    // 1. Follow / Unfollow Brand
                                    val isFollowingBrand = detail?.isBrandFollowed == true || (detail?.brand?.isFollowing == true)
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = if (isFollowingBrand) "Unfollow brand" else "Follow brand",
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 15.sp,
                                                color = AppTheme.colors.textPrimary
                                            )
                                        },
                                        onClick = {
                                            showMenu = false
                                            val bId = detail?.brandId ?: detail?.brand?.id
                                            if (bId != null) {
                                                viewModel.toggleBrandFollow(bId)
                                                val msg = if (isFollowingBrand) "Unfollowed $brandName" else "Following $brandName"
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Following $brandName", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.NotificationsNone,
                                                contentDescription = null,
                                                tint = AppTheme.colors.textPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    )
                                    HorizontalDivider(color = AppTheme.colors.divider)

                                    // 2. Hide Campaign
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Hide campaign",
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 15.sp,
                                                color = AppTheme.colors.textPrimary
                                            )
                                        },
                                        onClick = {
                                            showMenu = false
                                            viewModel.hideCampaign(campaignId) { success, msg ->
                                                Toast.makeText(context, if (success) "Campaign hidden" else msg, Toast.LENGTH_SHORT).show()
                                                if (success) {
                                                    onBack()
                                                }
                                            }
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_visibility_off),
                                                contentDescription = null,
                                                tint = AppTheme.colors.textPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    )
                                    HorizontalDivider(color = AppTheme.colors.divider)

                                    // 3. Share Campaign
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Share campaign",
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 15.sp,
                                                color = AppTheme.colors.textPrimary
                                            )
                                        },
                                        onClick = {
                                            showMenu = false
                                            shareCampaign()
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_share_upload),
                                                contentDescription = null,
                                                tint = AppTheme.colors.textPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    )
                                    HorizontalDivider(color = AppTheme.colors.divider)

                                    // 4. Report Campaign (highlighted red card row)
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Report campaign",
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFFEF4444),
                                                fontSize = 15.sp
                                            )
                                        },
                                        onClick = {
                                            showMenu = false
                                            showReportSheet = true
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_warning_triangle),
                                                contentDescription = null,
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        modifier = Modifier.background(
                                            if (AppTheme.isDark) Color(0xFF2E1719) else Color(0xFFFFF1F2)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Surface(
                                onClick = {
                                    isFavorite = !isFavorite
                                    viewModel.toggleFavorite(campaignId)
                                },
                                shape = CircleShape,
                                color = AppTheme.colors.surface,
                                modifier = Modifier.size(40.dp),
                                border = BorderStroke(1.dp, AppTheme.colors.border)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isFavorite || detail?.isFavorited == true) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isFavorite || detail?.isFavorited == true) Color(0xFFE53935) else AppTheme.colors.textPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Hero Banner Text Overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Text(
                            text = campaignTitle,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 34.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (deadlineText.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Deadline: $deadlineText",
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$applicationsCount applied",
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            if (deliverablesSummary.isNotBlank()) {
                                Text(
                                    text = "  •  ",
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = deliverablesSummary,
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // ── 2. Dates Section ────────────────────────────────────────────────
                if (startDate.isNotBlank() || endDate.isNotBlank()) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Text(
                            text = "Campaign dates",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = AppTheme.colors.surfaceVariant
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = startDate.ifBlank { "TBD" },
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.primary
                                    )
                                }
                            }

                            Text(
                                text = "—",
                                fontSize = 18.sp,
                                color = AppTheme.colors.textSecondary,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = AppTheme.colors.surfaceVariant
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = endDate.ifBlank { "TBD" },
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.primary
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.divider)
                }

                // ── 3. Brand Top Header Bar ─────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            val bid = detail?.brandId?.toString() ?: fallbackCampaign?.id ?: ""
                            onNavigateToBrandDetail(bid, brandName)
                        }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AppTheme.colors.surfaceVariant,
                            modifier = Modifier.size(36.dp),
                            border = BorderStroke(1.dp, AppTheme.colors.border)
                        ) {
                            if (!brandLogo.isNullOrBlank()) {
                                AsyncImage(
                                    model = brandLogo,
                                    contentDescription = brandName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(brandName.take(1).uppercase(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppTheme.colors.textPrimary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = brandName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            onClick = { shareCampaign() },
                            shape = CircleShape,
                            color = AppTheme.colors.surfaceVariant,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Share, contentDescription = "Share", tint = AppTheme.colors.textPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.divider)

                // ── 4. Campaign Description ─────────────────────────────────────────
                if (descriptionText.isNotBlank()) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Text(
                            text = "Campaign description",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = descriptionText,
                            fontSize = 14.sp,
                            color = AppTheme.colors.textSecondary,
                            lineHeight = 22.sp,
                            maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 5
                        )

                        if (descriptionText.length > 200) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isDescriptionExpanded = !isDescriptionExpanded }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDescriptionExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand",
                                    tint = AppTheme.colors.textPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.divider)
                }

                // ── 5. Requirements / Guidelines ────────────────────────────────────
                if (requirements.isNotEmpty()) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Text(
                            text = "Requirements for the campaign",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        requirements.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "• ",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.primary
                                )
                                Text(
                                    text = item,
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.textSecondary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.divider)
                }

                // ── 6. Deliverables Section ─────────────────────────────────────────
                if (deliverables.isNotEmpty()) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                        Text(
                            text = "Deliverables",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(deliverables) { del ->
                                Surface(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .height(160.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = AppTheme.colors.surfaceVariant
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = (del.platform ?: "Social").replaceFirstChar { it.uppercase() },
                                            fontSize = 13.sp,
                                            color = AppTheme.colors.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Icon(
                                            painter = painterResource(
                                                id = if (del.platform?.lowercase() == "tiktok") R.drawable.ic_social_tiktok else R.drawable.ic_social_instagram
                                            ),
                                            contentDescription = null,
                                            tint = AppTheme.colors.primary,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "${(del.deliverableType ?: "Post").replaceFirstChar { it.uppercase() }} x${del.quantity ?: 1}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.divider)
                }

                // ── 7. Reward / Compensation ────────────────────────────────────────
                if (compensationText.isNotBlank()) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                        Text(
                            text = "Reward & Compensation",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = AppTheme.colors.surfaceVariant
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (detail?.campaignTypeLabel?.isNotBlank() == true) detail!!.campaignTypeLabel!! else "Budget / Value",
                                    fontSize = 13.sp,
                                    color = AppTheme.colors.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = compensationText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.primary
                                )
                            }
                        }

                        if (galleryImages.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(galleryImages) { url ->
                                    AsyncImage(
                                        model = url,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(130.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.divider)
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Success Dialog on Apply
    if (showApplyDialog) {
        AlertDialog(
            onDismissRequest = { showApplyDialog = false },
            title = {
                Text(
                    "Application Submitted!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = AppTheme.colors.textPrimary
                )
            },
            text = {
                Text(
                    "Your application for $campaignTitle has been sent to $brandName. You will receive updates in your inbox.",
                    fontSize = 14.sp,
                    color = AppTheme.colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showApplyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.primary)
                ) {
                    Text("Awesome!", color = Color.White)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = AppTheme.colors.surface
        )
    }

    // Incomplete Profile Dialog
    if (showIncompleteProfileDialog) {
        IncompleteProfileDialog(
            onDismissRequest = { showIncompleteProfileDialog = false },
            onCompleteProfileClick = {
                showIncompleteProfileDialog = false
                onNavigateToEditProfile()
            }
        )
    }

    // Report Campaign Bottom Sheet
    if (showReportSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReportSheet = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = AppTheme.colors.surface,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 6.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .background(AppTheme.colors.border, shape = CircleShape)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp)
            ) {
                Text(
                    text = "Report $campaignTitle",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                val row1 = listOf("Offensive material", "False marketing")
                val row2 = listOf("Missing information", "inadequate offer")
                val row3 = listOf("Other")

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row1.forEach { option ->
                            ReportChipItem(
                                text = option,
                                selected = selectedReportReason == option,
                                onClick = {
                                    selectedReportReason = option
                                    viewModel.reportCampaign(campaignId, option) { _, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                    showReportSheet = false
                                }
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row2.forEach { option ->
                            ReportChipItem(
                                text = option,
                                selected = selectedReportReason == option,
                                onClick = {
                                    selectedReportReason = option
                                    viewModel.reportCampaign(campaignId, option) { _, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                    showReportSheet = false
                                }
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row3.forEach { option ->
                            ReportChipItem(
                                text = option,
                                selected = selectedReportReason == option,
                                onClick = {
                                    selectedReportReason = option
                                    viewModel.reportCampaign(campaignId, option) { _, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                    showReportSheet = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IncompleteProfileDialog(
    onDismissRequest: () -> Unit,
    onCompleteProfileClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = null,
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(44.dp)
            )
        },
        title = {
            Text(
                text = "Complete Your Profile",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "Please complete all required profile details (Personal Info & Address) before applying for campaigns.",
                fontSize = 14.sp,
                color = AppTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onCompleteProfileClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.primary)
            ) {
                Text("Complete Profile", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel", color = AppTheme.colors.textSecondary)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = AppTheme.colors.surface
    )
}

@Composable
fun ReportChipItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) AppTheme.colors.surfaceVariant else AppTheme.colors.surface,
        border = BorderStroke(1.dp, if (selected) AppTheme.colors.primary else AppTheme.colors.border)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) AppTheme.colors.primary else AppTheme.colors.textSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}
