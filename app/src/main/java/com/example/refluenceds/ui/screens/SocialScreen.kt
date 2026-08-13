package com.example.refluenceds.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Flag
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
import com.example.refluenceds.domain.model.Post
import com.example.refluenceds.ui.components.SkeletonItem
import com.example.refluenceds.ui.viewmodel.CampaignViewModel
import kotlinx.coroutines.launch

// ─── Brand gradient colours (blue → purple → pink) ───────────────────────────
private val BrandBlue   = Color(0xFF3D5AF1)
private val BrandPurple = Color(0xFF7C3AED)
private val BrandPink   = Color(0xFFEC4899)
private val gradientBrush = Brush.horizontalGradient(listOf(BrandPurple, BrandPink))

// ─── Report reason chips ──────────────────────────────────────────────────────
private val reportReasons = listOf("Offensive material", "False marketing", "Other")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(
    viewModel: CampaignViewModel,
    onNavigateToBrandGone: () -> Unit = {},
    onNavigateToInfluencerProfile: () -> Unit = {}
) {
    val posts      by viewModel.socialPosts.collectAsState()
    val isLoading  by viewModel.isLoading.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Collections", "Fashion", "Beauty", "Gastronomy", "Food & Drink")

    // ── Dialog & Sheet state ──────────────────────────────────────────────────
    val sheetScope  = rememberCoroutineScope()
    val sheetState  = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showOptionsDialog by remember { mutableStateOf(false) }
    var showReportSheet   by remember { mutableStateOf(false) }
    var selectedPost      by remember { mutableStateOf<Post?>(null) }

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
        containerColor = Color.White,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 4.dp)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        if (category == "All" && isSelected) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFF4B4FE4),
                                modifier = Modifier.clickable { selectedCategory = category }
                            ) {
                                Text(
                                    text = "All",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                                )
                            }
                        } else if (category == "Collections") {
                            Row(
                                modifier = Modifier.clickable { selectedCategory = category },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = null,
                                    tint = Color(0xFF4B4FE4),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Collections",
                                    color = Color(0xFF4B4FE4),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            }
                        } else {
                            Text(
                                text = category,
                                modifier = Modifier.clickable { selectedCategory = category },
                                color = Color(0xFF4B4FE4),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns    = GridCells.Fixed(2),
            modifier   = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding         = PaddingValues(16.dp),
            horizontalArrangement  = Arrangement.spacedBy(14.dp),
            verticalArrangement    = Arrangement.spacedBy(22.dp)
        ) {
            if (isLoading) {
                items(6) {
                    SkeletonItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.68f)
                            .clip(RoundedCornerShape(20.dp))
                    )
                }
            } else {
                items(posts.size) { index ->
                    val post = posts[index]
                    SocialPostCard(
                        post          = post,
                        index         = index,
                        onMoreClick   = { openOptions(post) }
                    )
                }
            }
        }
    }

    // ─── Options Dialog (Overlay dialog over post card as in Image 1) ──────────
    if (showOptionsDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showOptionsDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
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
                        color = Color(0xFF1A1D2E)
                    )

                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showOptionsDialog = false
                                onNavigateToInfluencerProfile()
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("View Influencer", color = Color(0xFF1A1D2E), fontSize = 14.sp)
                    }

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showOptionsDialog = false
                                onNavigateToBrandGone()
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("View Brand", color = Color(0xFF1A1D2E), fontSize = 14.sp)
                    }

                    HorizontalDivider(color = Color(0xFFEEEEEE))

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
                            tint = Color(0xFF1A1D2E),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Report Content", color = Color(0xFF1A1D2E), fontSize = 14.sp)
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
            containerColor    = Color.White,
            shape             = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle        = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color(0xFFE5E7EB), RoundedCornerShape(2.dp))
                )
            }
        ) {
            ReportContentSheet(
                post            = selectedPost,
                reasons         = reportReasons,
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
                    submitted = true
                    sheetScope.launch {
                        kotlinx.coroutines.delay(1200)
                        closeReportSheet()
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
            color      = Color(0xFF1A1D2E)
        )

        HorizontalDivider(color = Color(0xFFE5E7EB))

        // View Influencer
        OptionItem(
            label     = "View Influencer",
            textColor = Color(0xFF1A1D2E),
            onClick   = onViewInfluencer
        )

        HorizontalDivider(color = Color(0xFFE5E7EB))

        // View Brand
        OptionItem(
            label     = "View Brand",
            textColor = Color(0xFF1A1D2E),
            onClick   = onViewBrand
        )

        HorizontalDivider(color = Color(0xFFE5E7EB))

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
                color      = Color(0xFF1A1D2E)
            )
            IconButton(onClick = onClose) {
                Icon(
                    painter            = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "Close",
                    tint               = Color(0xFF6B7080),
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
                        selectedContainerColor      = BrandBlue,
                        selectedLabelColor          = Color.White,
                        containerColor              = Color.White,
                        labelColor                  = BrandBlue
                    ),
                    border    = FilterChipDefaults.filterChipBorder(
                        enabled              = true,
                        selected             = selected,
                        borderColor          = BrandBlue,
                        selectedBorderColor  = BrandBlue,
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
            placeholder       = { Text("Additional comments…", color = Color(0xFF9CA3AF)) },
            modifier          = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape             = RoundedCornerShape(12.dp),
            colors            = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedBorderColor   = BrandBlue
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

// ─────────────────────────────────────────────────────────────────────────────
// SOCIAL POST CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SocialPostCard(
    post        : Post,
    index       : Int = 0,
    onMoreClick : () -> Unit = {}
) {
    var bookmarked by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.68f)
                .clip(RoundedCornerShape(20.dp))
        ) {
            AsyncImage(
                model            = post.contentUrl,
                contentDescription = null,
                modifier         = Modifier.fillMaxSize(),
                contentScale     = ContentScale.Crop
            )

            // Top text overlay for post 0
            if (index == 0) {
                Text(
                    text = "offizielle Geldleistung in...",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                )

                // Purple Circle Badge overlay ("Bis zu CHF 2'016 pro Monat")
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(72.dp),
                    shape = CircleShape,
                    color = Color(0xFF4A154B)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Bis zu",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "CHF 2'016",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "pro Monat",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Bottom text overlay for post 2 ("PIXEL")
            if (index == 2) {
                Text(
                    text = "PIXEL",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }

            // Bottom gradient for stats readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                        )
                    )
            )

            // Stats row
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter            = painterResource(id = R.drawable.ic_social_tiktok),
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(16.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter            = painterResource(id = R.drawable.ic_heart_outline),
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = when (index % 4) {
                            0 -> "1350"
                            1 -> "2114"
                            2 -> "474"
                            else -> "5014"
                        },
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter            = painterResource(id = R.drawable.ic_nav_chat_outline),
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = when (index % 4) {
                            0 -> "13"
                            1 -> "5"
                            2 -> "13"
                            else -> "23"
                        },
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Avatars
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model            = post.creatorAvatar,
                    contentDescription = null,
                    modifier         = Modifier
                        .size(28.dp)
                        .clip(CircleShape),
                    contentScale     = ContentScale.Crop
                )
                Text(text = " + ", fontSize = 13.sp, color = Color(0xFF8E8E93), fontWeight = FontWeight.Bold)

                if (index == 0) {
                    // Green Shield Brand Icon
                    Surface(
                        modifier = Modifier.size(28.dp),
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.5.dp, Color(0xFF4CAF50))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_target_concentric),
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else if (index == 1) {
                    // Lidl Logo Yellow/Blue Badge
                    Surface(
                        modifier = Modifier.size(28.dp),
                        shape = CircleShape,
                        color = Color(0xFFFFCC00),
                        border = BorderStroke(1.5.dp, Color(0xFF0050AA))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("LIDL", fontSize = 7.sp, fontWeight = FontWeight.Black, color = Color(0xFFE30613))
                        }
                    }
                } else {
                    AsyncImage(
                        model            = "https://picsum.photos/seed/${post.creatorName}/80",
                        contentDescription = null,
                        modifier         = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color(0xFFECECF4), CircleShape),
                        contentScale     = ContentScale.Crop
                    )
                }
            }

            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Bookmark button
                Surface(
                    onClick   = { bookmarked = !bookmarked },
                    modifier  = Modifier.size(32.dp),
                    shape     = CircleShape,
                    color     = Color.White,
                    border    = BorderStroke(1.dp, Color(0xFFECECF4))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector        = if (bookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint               = if (bookmarked) BrandBlue else Color(0xFF1D1B36),
                            modifier           = Modifier.size(16.dp)
                        )
                    }
                }

                // Three-dot button
                Surface(
                    onClick   = onMoreClick,
                    modifier  = Modifier.size(32.dp),
                    shape     = CircleShape,
                    color     = Color.White,
                    border    = BorderStroke(1.dp, Color(0xFFECECF4))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector        = Icons.Default.MoreHoriz,
                            contentDescription = "More options",
                            tint               = Color(0xFF1D1B36),
                            modifier           = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
