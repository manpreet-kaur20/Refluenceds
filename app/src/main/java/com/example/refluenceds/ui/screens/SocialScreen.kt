@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.refluenceds.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.PlayArrow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.refluenceds.utils.SetStatusBarAppearance
import com.example.refluenceds.R
import com.example.refluenceds.domain.model.Post
import com.example.refluenceds.ui.components.AppPullToRefreshBox
import com.example.refluenceds.ui.components.SkeletonItem
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.viewmodel.CampaignViewModel
import kotlinx.coroutines.launch

// ─── Brand gradient colours (blue → purple → pink) ───────────────────────────
private val BrandBlue   = Color(0xFF3D5AF1)
private val BrandPurple = Color(0xFF7C3AED)
private val BrandPink   = Color(0xFFEC4899)
private val gradientBrush = Brush.horizontalGradient(listOf(BrandPurple, BrandPink))

// ─── Report reason chips ──────────────────────────────────────────────────────
private val reportReasons = listOf("Offensive material", "False marketing", "Other")

@Composable
fun SocialScreen(
    viewModel: CampaignViewModel,
    onNavigateToBrandGone: (brandId: String, brandName: String) -> Unit = { _, _ -> },
    onNavigateToInfluencerProfile: (creatorId: String, creatorName: String) -> Unit = { _, _ -> },
    onNavigateToBrandDetail: (brandId: String, brandName: String) -> Unit = { _, _ -> }
) {
    SetStatusBarAppearance(isLightStatusBars = !AppTheme.isDark)
    val posts      by viewModel.socialPosts.collectAsState()
    val isLoading  by viewModel.isContentFeedLoading.collectAsState()
    val pagination by viewModel.contentFeedPagination.collectAsState()
    val apiReportReasons by viewModel.reportReasons.collectAsState()
    val gridState  = rememberLazyGridState()
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Collections", "Fashion", "Beauty", "Gastronomy", "Food & Drink")

    val reasonsList = remember(apiReportReasons) {
        if (apiReportReasons.isNotEmpty()) {
            apiReportReasons.mapNotNull { it.label ?: it.reason ?: it.key }
        } else {
            reportReasons
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchReportReasons()
    }

    LaunchedEffect(selectedCategory) {
        val tab = if (selectedCategory == "Collections") "collections" else "all"
        val cat = if (selectedCategory in listOf("All", "Collections")) null else selectedCategory
        viewModel.fetchContentFeed(tab = tab, category = cat, page = 1, perPage = 10)
    }

    LaunchedEffect(gridState, pagination.hasMore, pagination.isLoadingMore) {
        snapshotFlow {
            val totalItems = gridState.layoutInfo.totalItemsCount
            val lastVisibleIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            totalItems > 0 && lastVisibleIndex >= totalItems - 2 && pagination.hasMore && !pagination.isLoadingMore
        }.collect { shouldLoad ->
            if (shouldLoad) {
                viewModel.loadMoreContentFeed()
            }
        }
    }

    // ── Dialog & Sheet state ──────────────────────────────────────────────────
    val sheetScope  = rememberCoroutineScope()
    val sheetState  = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showOptionsDialog by remember { mutableStateOf(false) }
    var showReportSheet   by remember { mutableStateOf(false) }
    var selectedPost      by remember { mutableStateOf<Post?>(null) }
    var selectedPostForDetail by remember { mutableStateOf<Post?>(null) }

    // ── Report state ─────────────────────────────────────────────────────────
    var selectedReasons by remember { mutableStateOf(setOf<String>()) }
    var reportComment   by remember { mutableStateOf("") }
    var submitted       by remember { mutableStateOf(false) }

    // ── Helpers ──────────────────────────────────────────────────────────────
    fun openOptions(post: Post) {
        selectedPost = post
        showOptionsDialog = true
    }

    fun openReport() {
        selectedReasons = setOf()
        reportComment   = ""
        submitted       = false
        showReportSheet = true
        sheetScope.launch { sheetState.show() }
    }

    fun closeReportSheet() {
        sheetScope.launch {
            sheetState.hide()
            showReportSheet = false
        }
    }

    Scaffold(
        modifier       = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 4.dp)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        if (isSelected) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = AppTheme.colors.primary,
                                modifier = Modifier.clickable { selectedCategory = category }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (category == "Collections") {
                                        Icon(
                                            imageVector = Icons.Outlined.BookmarkBorder,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = category,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .clickable { selectedCategory = category }
                                    .padding(horizontal = 4.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (category == "Collections") {
                                    Icon(
                                        imageVector = Icons.Outlined.BookmarkBorder,
                                        contentDescription = null,
                                        tint = AppTheme.colors.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = category,
                                    color = AppTheme.colors.primary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        AppPullToRefreshBox(
            isRefreshing = isLoading && !pagination.isLoadingMore,
            onRefresh = {
                val tab = if (selectedCategory == "Collections") "collections" else "all"
                val cat = if (selectedCategory in listOf("All", "Collections")) null else selectedCategory
                viewModel.fetchContentFeed(tab = tab, category = cat, page = 1, perPage = 10)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            LazyVerticalGrid(
                columns    = GridCells.Fixed(2),
                state      = gridState,
                modifier   = Modifier.fillMaxSize(),
                contentPadding         = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 110.dp),
                horizontalArrangement  = Arrangement.spacedBy(14.dp),
                verticalArrangement    = Arrangement.spacedBy(18.dp)
            ) {
                if (isLoading && posts.isEmpty()) {
                    items(6) {
                        SkeletonItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.68f)
                                .clip(RoundedCornerShape(18.dp))
                        )
                    }
                } else if (posts.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_social_video),
                                    contentDescription = null,
                                    tint = AppTheme.colors.textSecondary.copy(alpha = 0.4f),
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "No posts available",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                } else {
                    items(posts.size, key = { index -> posts[index].id.ifBlank { index.toString() } }) { index ->
                        if (index >= posts.size - 2) {
                            LaunchedEffect(index) {
                                viewModel.loadMoreContentFeed()
                            }
                        }
                        val post = posts[index]
                        SocialPostCard(
                            post            = post,
                            index           = index,
                            onPostClick     = { selectedPostForDetail = post },
                            onBookmarkClick = { viewModel.toggleBookmark(post.id) },
                            onMoreClick     = { openOptions(post) }
                        )
                    }

                    if (pagination.isLoadingMore) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    color = AppTheme.colors.primary,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ─── Full Screen Detail Reel Dialog (Image 3) ────────────────────────────
    if (selectedPostForDetail != null) {
        val currentDetailPost = selectedPostForDetail!!
        LaunchedEffect(currentDetailPost.id) {
            viewModel.fetchContentFeedDetail(currentDetailPost.id) { updatedPost ->
                if (selectedPostForDetail?.id == updatedPost.id) {
                    selectedPostForDetail = updatedPost
                }
            }
        }

        SocialFeedDetailDialog(
            post = currentDetailPost,
            onDismiss = { selectedPostForDetail = null },
            onBookmarkClick = { viewModel.toggleBookmark(currentDetailPost.id) },
            onNavigateToBrand = { brandId, brandName ->
                selectedPostForDetail = null
                onNavigateToBrandGone(brandId, brandName)
            },
            onNavigateToInfluencer = { creatorId, creatorName ->
                selectedPostForDetail = null
                onNavigateToInfluencerProfile(creatorId, creatorName)
            }
        )
    }

    // ─── Options Dialog (Overlay dialog over post card as in Image 1) ──────────
    if (showOptionsDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showOptionsDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AppTheme.colors.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.width(260.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "Options",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AppTheme.colors.textPrimary
                    )

                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = AppTheme.colors.divider)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showOptionsDialog = false
                                onNavigateToInfluencerProfile(
                                    selectedPost?.creatorId.orEmpty(),
                                    selectedPost?.creatorName.orEmpty()
                                )
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("View Influencer", color = AppTheme.colors.textPrimary, fontSize = 14.sp)
                    }

                    HorizontalDivider(color = AppTheme.colors.divider)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showOptionsDialog = false
                                val bid = selectedPost?.brandId.orEmpty()
                                val bname = selectedPost?.brandName.orEmpty()
                                if (bid.isNotBlank() || bname.isNotBlank()) {
                                    onNavigateToBrandDetail(bid, bname)
                                } else {
                                    onNavigateToBrandGone(bid, bname)
                                }
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("View Brand", color = AppTheme.colors.textPrimary, fontSize = 14.sp)
                    }

                    HorizontalDivider(color = AppTheme.colors.divider)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showOptionsDialog = false
                                openReport()
                            }
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = null,
                            tint = Color(0xFFE53E3E),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Report Content", color = Color(0xFFE53E3E), fontSize = 14.sp)
                    }
                }
            }
        }
    }

    // ─── Report Content Bottom Sheet (Image 2) ───────────────────────────────
    if (showReportSheet) {
        ModalBottomSheet(
            onDismissRequest  = { showReportSheet = false },
            sheetState        = sheetState,
            containerColor    = AppTheme.colors.surface,
            shape             = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle        = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(AppTheme.colors.border, RoundedCornerShape(2.dp))
                )
            }
        ) {
            ReportContentSheet(
                post            = selectedPost,
                reasons         = reasonsList,
                selectedReasons = selectedReasons,
                comment         = reportComment,
                submitted       = submitted,
                onToggleReason  = { reason ->
                    selectedReasons = if (reason in selectedReasons)
                        selectedReasons - reason
                    else
                        selectedReasons + reason
                },
                onCommentChange = { reportComment = it },
                onSubmit        = {
                    selectedPost?.id?.let { contentId ->
                        val reasonText = selectedReasons.joinToString(", ").ifBlank { "Other" }
                        viewModel.reportContent(contentId, reasonText, reportComment) {
                            submitted = true
                            sheetScope.launch {
                                kotlinx.coroutines.delay(1200)
                                closeReportSheet()
                            }
                        }
                    }
                },
                onClose         = { closeReportSheet() }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// OPTIONS SHEET
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OptionsSheet(
    onViewInfluencer : () -> Unit,
    onViewBrand      : () -> Unit,
    onReportContent  : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        // Title
        Text(
            text       = "Options",
            modifier   = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            textAlign  = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp,
            color      = AppTheme.colors.textPrimary
        )

        HorizontalDivider(color = AppTheme.colors.divider)

        // View Influencer
        OptionItem(
            label     = "View Influencer",
            textColor = AppTheme.colors.textPrimary,
            onClick   = onViewInfluencer
        )

        HorizontalDivider(color = AppTheme.colors.divider)

        // View Brand
        OptionItem(
            label     = "View Brand",
            textColor = AppTheme.colors.textPrimary,
            onClick   = onViewBrand
        )

        HorizontalDivider(color = AppTheme.colors.divider)

        // Report Content (with flag icon)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onReportContent() }
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector        = Icons.Outlined.Flag,
                contentDescription = null,
                tint               = Color(0xFFE53E3E),
                modifier           = Modifier
                    .size(18.dp)
                    .padding(end = 0.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text       = "Report Content",
                color      = Color(0xFFE53E3E),
                fontWeight = FontWeight.Medium,
                fontSize   = 15.sp
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun OptionItem(label: String, textColor: Color, onClick: () -> Unit) {
    Box(
        modifier       = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            color      = textColor,
            fontWeight = FontWeight.Normal,
            fontSize   = 15.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// REPORT CONTENT SHEET
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ReportContentSheet(
    post            : Post?,
    reasons         : List<String>,
    selectedReasons : Set<String>,
    comment         : String,
    submitted       : Boolean,
    onToggleReason  : (String) -> Unit,
    onCommentChange : (String) -> Unit,
    onSubmit        : () -> Unit,
    onClose         : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        // Header row
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text       = "Report Content",
                fontWeight = FontWeight.Bold,
                fontSize   = 17.sp,
                color      = AppTheme.colors.textPrimary
            )
            IconButton(onClick = onClose) {
                Icon(
                    painter            = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "Close",
                    tint               = AppTheme.colors.textSecondary,
                    modifier           = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Content preview image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model            = post?.contentUrl,
                contentDescription = "Reported content",
                modifier         = Modifier.fillMaxSize(),
                contentScale     = ContentScale.Crop
            )
            // Dark gradient overlay at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(0.4f), Color.Transparent)
                        )
                    )
            )
        }

        Spacer(Modifier.height(16.dp))

        // Reason chips
        FlowRow(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement   = Arrangement.spacedBy(8.dp)
        ) {
            reasons.forEach { reason ->
                val selected = reason in selectedReasons
                FilterChip(
                    selected  = selected,
                    onClick   = { onToggleReason(reason) },
                    label     = {
                        Text(
                            text       = reason,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    colors    = FilterChipDefaults.filterChipColors(
                        selectedContainerColor      = AppTheme.colors.primary,
                        selectedLabelColor          = Color.White,
                        containerColor              = AppTheme.colors.surfaceVariant,
                        labelColor                  = AppTheme.colors.primary
                    ),
                    border    = FilterChipDefaults.filterChipBorder(
                        enabled              = true,
                        selected             = selected,
                        borderColor          = AppTheme.colors.primary,
                        selectedBorderColor  = AppTheme.colors.primary,
                        borderWidth          = 1.5.dp,
                        selectedBorderWidth  = 1.5.dp
                    ),
                    shape     = RoundedCornerShape(50.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Comment box
        OutlinedTextField(
            value             = comment,
            onValueChange     = onCommentChange,
            placeholder       = { Text("Additional comments…", color = AppTheme.colors.textSecondary) },
            modifier          = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape             = RoundedCornerShape(12.dp),
            colors            = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = AppTheme.colors.border,
                focusedBorderColor   = AppTheme.colors.primary,
                focusedTextColor     = AppTheme.colors.textPrimary,
                unfocusedTextColor   = AppTheme.colors.textPrimary
            )
        )

        Spacer(Modifier.height(16.dp))

        // Submit button with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(if (submitted) Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF059669))) else gradientBrush)
                .clickable(enabled = !submitted) { onSubmit() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = if (submitted) "✓  Submitted!" else "Submit",
                color      = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000f).replace(".0M", "M")
        count >= 1_000 -> String.format("%.1fK", count / 1_000f).replace(".0K", "K")
        else -> count.toString()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SOCIAL POST CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SocialPostCard(
    post            : Post,
    index           : Int = 0,
    onPostClick     : () -> Unit = {},
    onBookmarkClick : () -> Unit = {},
    onMoreClick     : () -> Unit = {}
) {
    var bookmarked by remember(post.id, post.isBookmarked) { mutableStateOf(post.isBookmarked) }
    val platformIcon = when (post.platform.lowercase()) {
        "tiktok" -> R.drawable.ic_social_tiktok
        else -> R.drawable.ic_social_instagram
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.68f)
                .clip(RoundedCornerShape(18.dp))
                .clickable { onPostClick() }
        ) {
            AsyncImage(
                model            = post.contentUrl,
                contentDescription = null,
                modifier         = Modifier.fillMaxSize(),
                contentScale     = ContentScale.Crop
            )

            // Bottom gradient for stats readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )

            // Stats row inside card at bottom
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter            = painterResource(id = platformIcon),
                    contentDescription = post.platform,
                    tint               = Color.White,
                    modifier           = Modifier.size(15.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter            = painterResource(id = R.drawable.ic_heart_outline),
                        contentDescription = "Likes",
                        tint               = Color.White,
                        modifier           = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = formatCount(post.likes),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter            = painterResource(id = R.drawable.ic_nav_chat_outline),
                        contentDescription = "Comments",
                        tint               = Color.White,
                        modifier           = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = formatCount(post.commentsCount),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Bottom row with avatars on left, icons on right
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Avatars (Creator + Brand)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (post.creatorAvatar.isNotEmpty()) {
                    AsyncImage(
                        model            = post.creatorAvatar,
                        contentDescription = post.creatorName,
                        modifier         = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.surface)
                            .border(1.dp, AppTheme.colors.border, CircleShape),
                        contentScale     = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(26.dp),
                        shape = CircleShape,
                        color = AppTheme.colors.primary.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = post.creatorName.take(1).uppercase().ifBlank { "C" },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.primary
                            )
                        }
                    }
                }

                if (post.brandLogo.isNotEmpty() || post.brandName.isNotEmpty()) {
                    Text(
                        text = " + ",
                        fontSize = 12.sp,
                        color = Color(0xFF8E8E93),
                        fontWeight = FontWeight.SemiBold
                    )

                    if (post.brandLogo.isNotEmpty()) {
                        AsyncImage(
                            model            = post.brandLogo,
                            contentDescription = post.brandName,
                            modifier         = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(AppTheme.colors.surface)
                                .border(1.dp, AppTheme.colors.border, CircleShape),
                            contentScale     = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            modifier = Modifier.size(26.dp),
                            shape = CircleShape,
                            color = Color(0xFF1A1D2E),
                            border = BorderStroke(1.dp, AppTheme.colors.border)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = post.brandName.take(1).uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Bookmark and More horizontal icons
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            bookmarked = !bookmarked
                            onBookmarkClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (bookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (bookmarked) AppTheme.colors.primary else AppTheme.colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onMoreClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "More options",
                        tint = AppTheme.colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SOCIAL FEED DETAIL DIALOG (FULL SCREEN REEL)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SocialFeedDetailDialog(
    post                   : Post,
    onDismiss              : () -> Unit,
    onBookmarkClick        : () -> Unit = {},
    onNavigateToBrand      : (brandId: String, brandName: String) -> Unit = { _, _ -> },
    onNavigateToInfluencer : (creatorId: String, creatorName: String) -> Unit = { _, _ -> }
) {
    var isLiked by remember(post.id) { mutableStateOf(false) }
    var isBookmarked by remember(post.id, post.isBookmarked) { mutableStateOf(post.isBookmarked) }
    var likeCount by remember(post.id, post.likes) { mutableIntStateOf(post.likes) }
    val context = LocalContext.current
    val platformIcon = when (post.platform.lowercase()) {
        "tiktok" -> R.drawable.ic_social_tiktok
        else -> R.drawable.ic_social_instagram
    }

    val videoUrlToPlay = remember(post.id, post.videoUrl, post.contentUrl) {
        if (post.videoUrl.isNotBlank()) {
            post.videoUrl
        } else if (post.mediaType.lowercase() == "video" || post.contentUrl.endsWith(".mp4", true) || (!post.contentUrl.contains(".jpg", true) && !post.contentUrl.contains(".png", true) && !post.contentUrl.contains(".webp", true) && !post.contentUrl.contains(".jpeg", true))) {
            post.contentUrl
        } else {
            ""
        }
    }

    var isPlaying by remember(post.id) { mutableStateOf(true) }
    var isBuffering by remember(post.id) { mutableStateOf(false) }
    var videoProgress by remember(post.id) { mutableFloatStateOf(0f) }

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = (playbackState == Player.STATE_BUFFERING)
            }
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                isBuffering = false
                isPlaying = false
                android.util.Log.e("SocialFeedDetail", "ExoPlayer Error for URL $videoUrlToPlay: ${error.message}", error)
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    LaunchedEffect(isPlaying, isBuffering) {
        if (isPlaying && !isBuffering) {
            while (true) {
                val duration = exoPlayer.duration
                if (duration > 0) {
                    videoProgress = exoPlayer.currentPosition.toFloat() / duration.toFloat()
                }
                kotlinx.coroutines.delay(16)
            }
        }
    }

    DisposableEffect(videoUrlToPlay) {
        if (videoUrlToPlay.isNotBlank()) {
            isBuffering = true
            val mediaItem = MediaItem.fromUri(Uri.parse(videoUrlToPlay))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        } else {
            isBuffering = false
        }
        onDispose {
            exoPlayer.stop()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        SetStatusBarAppearance(isLightStatusBars = false)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (videoUrlToPlay.isNotBlank()) {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer.play()
                        }
                    }
                }
        ) {
            // Thumbnail fallback
            if (post.contentUrl.isNotBlank()) {
                AsyncImage(
                    model = post.contentUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Full screen video
            if (videoUrlToPlay.isNotBlank()) {
                key(videoUrlToPlay) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (isBuffering) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(44.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    }
                } else if (!isPlaying) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.45f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Top gradient for back button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                        )
                    )
            )

            // Bottom gradient for metadata and actions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                        )
                    )
            )

            // Top Bar with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Right side Action Buttons (Heart + Bookmark)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 16.dp, bottom = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Heart button (White circular button)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        onClick = {
                            isLiked = !isLiked
                            likeCount += if (isLiked) 1 else -1
                        },
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (isLiked) Color(0xFFE53E3E) else Color(0xFF1A1D2E),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = formatCount(likeCount),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Bookmark button (White circular button)
                Surface(
                    onClick = {
                        isBookmarked = !isBookmarked
                        onBookmarkClick()
                    },
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) Color(0xFF5B61F4) else Color(0xFF1A1D2E),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Share button (White circular button)
                Surface(
                    onClick = {
                        val shareUrl = post.contentUrl.ifBlank { "https://refluenced.com" }
                        val shareText = "${post.caption.ifBlank { "Check out this video on Refluenced" }}\n$shareUrl"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share Video").apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(shareIntent)
                    },
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.IosShare,
                            contentDescription = "Share",
                            tint = Color(0xFF1A1D2E),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Bottom Left content (Creator/Brand pills, Caption, Stats)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 16.dp, end = 80.dp, bottom = 24.dp)
            ) {
                // Creator & Brand Pills
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Creator Pill
                    if (post.creatorName.isNotEmpty() || post.creatorAvatar.isNotEmpty()) {
                        Surface(
                            onClick = {
                                onDismiss()
                                onNavigateToInfluencer(post.creatorId, post.creatorName)
                            },
                            shape = RoundedCornerShape(50),
                            color = Color.Black.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (post.creatorAvatar.isNotEmpty()) {
                                    AsyncImage(
                                        model = post.creatorAvatar,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(Modifier.width(6.dp))
                                }
                                Text(
                                    text = post.creatorName.ifEmpty { "Creator" },
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Brand Pill
                    if (post.brandName.isNotEmpty() || post.brandLogo.isNotEmpty()) {
                        Surface(
                            onClick = {
                                onDismiss()
                                onNavigateToBrand(post.brandId, post.brandName)
                            },
                            shape = RoundedCornerShape(50),
                            color = Color.Black.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (post.brandLogo.isNotEmpty()) {
                                    AsyncImage(
                                        model = post.brandLogo,
                                        contentDescription = post.brandName,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Surface(
                                        modifier = Modifier.size(24.dp),
                                        shape = CircleShape,
                                        color = Color(0xFF1A1D2E)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = post.brandName.take(1).uppercase(),
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = post.brandName,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                if (post.caption.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    // Post Title / Caption
                    Text(
                        text = post.caption,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Bottom Stats Row (Platform icon, likes, comments)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = platformIcon),
                        contentDescription = post.platform,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_heart_outline),
                            contentDescription = "Likes",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = formatCount(likeCount),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_nav_chat_outline),
                            contentDescription = "Comments",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = formatCount(post.commentsCount),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Bottom Progress Bar Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .height(2.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(videoProgress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(Color.White)
                )
            }
        }
    }
}


