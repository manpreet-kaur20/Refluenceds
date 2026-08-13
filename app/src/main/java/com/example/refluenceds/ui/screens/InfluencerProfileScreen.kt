package com.example.refluenceds.ui.screens

import androidx.compose.animation.AnimatedVisibility
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

data class CampaignContentItem(
    val id: String,
    val title: String,
    val pieceCount: String,
    val thumbnailUrl: String
)

data class SocialFeedPost(
    val id: String,
    val mediaUrl: String,
    val likes: Int,
    val comments: Int,
    val isVideo: Boolean = true
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfluencerProfileScreen(
    creatorName: String = "Lia",
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    var isBioExpanded by remember { mutableStateOf(false) }
    var isCampaignsExpanded by remember { mutableStateOf(false) }
    var selectedCampaignFilter by remember { mutableStateOf("All") }
    var selectedSocialTab by remember { mutableStateOf(0) } // 0: Instagram, 1: TikTok

    fun getRes(resId: Int): String = "android.resource://com.example.refluenceds/$resId"

    val campaignFilters = listOf(
        CampaignContentItem("1", "All", "68 PIECES", getRes(R.drawable.beauty)),
        CampaignContentItem("2", "BOSS Parfums", "6 PIECES", getRes(R.drawable.coral_wash)),
        CampaignContentItem("3", "BOSS Parfums", "9 PIECES", getRes(R.drawable.phantom_parfum)),
        CampaignContentItem("4", "Jean Paul Ga...", "8 PIECES", getRes(R.drawable.seidenfelt_ugc))
    )

    val campaignGridPosts = remember {
        listOf(
            SocialFeedPost("c1", getRes(R.drawable.woman_laptop_post), 428, 13),
            SocialFeedPost("c2", getRes(R.drawable.fashion_woman), 19, 0),
            SocialFeedPost("c3", getRes(R.drawable.fashion_model), 0, 0),
            SocialFeedPost("c4", getRes(R.drawable.fashion_shirt), 136, 1),
            SocialFeedPost("c5", getRes(R.drawable.beauty), 2050, 3),
            SocialFeedPost("c6", getRes(R.drawable.coral_wash), 927, 2),
            SocialFeedPost("c7", getRes(R.drawable.phantom_parfum), 392, 0),
            SocialFeedPost("c8", getRes(R.drawable.seidenfelt_ugc), 182, 0),
            SocialFeedPost("c9", getRes(R.drawable.lifestyle), 32, 0),
            SocialFeedPost("c10", getRes(R.drawable.fashion), 512, 14),
            SocialFeedPost("c11", getRes(R.drawable.travel), 89, 4),
            SocialFeedPost("c12", getRes(R.drawable.events), 340, 8)
        )
    }

    val instagramPosts = remember {
        listOf(
            SocialFeedPost("ig1", getRes(R.drawable.fashion_woman), 11, 0, false),
            SocialFeedPost("ig2", getRes(R.drawable.woman_laptop_post), 12, 2, false),
            SocialFeedPost("ig3", getRes(R.drawable.fashion_model), 23, 0, false),
            SocialFeedPost("ig4", getRes(R.drawable.fashion_shirt), 8, 1, false),
            SocialFeedPost("ig5", getRes(R.drawable.beauty), 6, 0, false),
            SocialFeedPost("ig6", getRes(R.drawable.coral_wash), 51, 4, false),
            SocialFeedPost("ig7", getRes(R.drawable.phantom_parfum), 44, 2, false),
            SocialFeedPost("ig8", getRes(R.drawable.seidenfelt_ugc), 19, 1, false),
            SocialFeedPost("ig9", getRes(R.drawable.lifestyle), 87, 5, false)
        )
    }

    val tiktokPosts = remember {
        listOf(
            SocialFeedPost("tt1", getRes(R.drawable.travel), 43, 1),
            SocialFeedPost("tt2", getRes(R.drawable.fashion_model), 30, 3),
            SocialFeedPost("tt3", getRes(R.drawable.beauty), 43, 2),
            SocialFeedPost("tt4", getRes(R.drawable.coral_wash), 380, 3),
            SocialFeedPost("tt5", getRes(R.drawable.phantom_parfum), 25, 0),
            SocialFeedPost("tt6", getRes(R.drawable.seidenfelt_ugc), 43, 0),
            SocialFeedPost("tt7", getRes(R.drawable.woman_laptop_post), 120, 5),
            SocialFeedPost("tt8", getRes(R.drawable.fashion_woman), 94, 2),
            SocialFeedPost("tt9", getRes(R.drawable.fashion_shirt), 210, 6)
        )
    }

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // ── 1. Top Cover Photo & Floating Back Arrow (Image 1) ─────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                AsyncImage(
                    model = getRes(R.drawable.woman_laptop_post),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Top Floating Back Arrow
                Surface(
                    onClick = onBack,
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.35f),
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(16.dp)
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

            // ── 2. Profile Info Section (Image 1) ──────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(
                    text = creatorName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B36)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Active status line
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFFF59E0B), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Active this month",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B36)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Chips Row
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF5A5A72),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Rothrist",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1D1B36)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Text(
                            text = "Beauty, Fashion",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1D1B36),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // BIO Section
                Text(
                    text = "BIO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF75758A)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isBioExpanded)
                        "Schon mit 14 Jahren habe ich – ganz ohne Masterplan, dafür mit einer riesigen Portion ADHS-Power – eine Community aufgebaut. Ich erstelle leidenschaftlich gerne Videos über Beauty, Lifestyle und Fashion!"
                    else
                        "Schon mit 14 Jahren habe ich – ganz ohne Masterplan, dafür mit einer riesigen Portion ADHS-Power – eine Com...",
                    fontSize = 14.sp,
                    color = Color(0xFF4A4A62),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isBioExpanded) "Show less" else "Show more",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B4FE4),
                    modifier = Modifier.clickable { isBioExpanded = !isBioExpanded }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── 3. Social Metrics Cards (Image 2) ──────────────────────────────
                // Instagram Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Color(0xFFF0F0F6))
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
                                tint = Color(0xFF1D1B36),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Instagram", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B36))
                                Text("liadiotima", fontSize = 11.sp, color = Color(0xFF4B4FE4))
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Groups, null, tint = Color(0xFF7C5CF6), modifier = Modifier.size(18.dp))
                                Text("327", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.ChatBubbleOutline, null, tint = Color(0xFF7C5CF6), modifier = Modifier.size(18.dp))
                                Text("16.8%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.Visibility, null, tint = Color(0xFF7C5CF6), modifier = Modifier.size(18.dp))
                                Text("518", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // TikTok Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Color(0xFFF0F0F6))
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
                                tint = Color(0xFF1D1B36),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("TikTok", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B36))
                                Text("liadiotima.ugc", fontSize = 11.sp, color = Color(0xFF4B4FE4))
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Groups, null, tint = Color(0xFF7C5CF6), modifier = Modifier.size(18.dp))
                                Text("5.47K", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Bolt, null, tint = Color(0xFF7C5CF6), modifier = Modifier.size(18.dp))
                                Text("17XMO", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.Visibility, null, tint = Color(0xFF7C5CF6), modifier = Modifier.size(18.dp))
                                Text("987", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── 4. Reviews Card (Image 2) ──────────────────────────────────────
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Color(0xFFF0F0F6))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Lia's Reviews", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B36))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFF7C5CF6),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("5/5 (16)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B36))
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF1D1B36),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ── 5. Campaign Content Section (Images 2 & 3) ──────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Campaign content",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B36)
                    )

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFF3F4F6)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Campaign, null, tint = Color(0xFF5A5A72), modifier = Modifier.size(16.dp))
                            Text(" 17  ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.PhotoLibrary, null, tint = Color(0xFF5A5A72), modifier = Modifier.size(16.dp))
                            Text(" 21", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Horizontal Filter Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(campaignFilters) { filter ->
                        val isSelected = filter.title == selectedCampaignFilter
                        Surface(
                            onClick = { selectedCampaignFilter = filter.title },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) Color(0xFFECECFF) else Color.White,
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF4B4FE4) else Color(0xFFE5E7EB)),
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
                                    AsyncImage(
                                        model = filter.thumbnailUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .size(18.dp)
                                                .background(Color(0xFF4B4FE4), CircleShape),
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
                                    maxLines = 1
                                )
                                Text(
                                    text = filter.pieceCount,
                                    fontSize = 10.sp,
                                    color = Color(0xFF75758A)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Campaign 3-Column Video Grid
                val visibleCampaignPosts = if (isCampaignsExpanded) campaignGridPosts else campaignGridPosts.take(9)

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    visibleCampaignPosts.chunked(3).forEach { rowPosts ->
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
                                        model = post.mediaUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    // TikTok Icon top left
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_social_tiktok),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(8.dp)
                                            .size(16.dp)
                                    )

                                    // Play Icon center
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.9f),
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(28.dp)
                                    )

                                    // Bottom Gradient Scrim & Stats
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
                                        Text(" ${post.likes} ", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Icon(Icons.Outlined.ChatBubbleOutline, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        Text(" ${post.comments}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Fill remaining spaces if incomplete row
                            if (rowPosts.size < 3) {
                                repeat(3 - rowPosts.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Expand / Collapse Button (Image 3)
                Surface(
                    onClick = { isCampaignsExpanded = !isCampaignsExpanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isCampaignsExpanded) "Show less" else "Show 9 more",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4B4FE4)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (isCampaignsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color(0xFF4B4FE4),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ── 6. Social Feed Section (Images 4 & 5) ──────────────────────────
                Text(
                    text = "Social feed",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B36)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Switcher Bar
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                                tint = if (selectedSocialTab == 0) Color(0xFF4B4FE4) else Color(0xFF9CA3AF),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Instagram",
                                fontSize = 16.sp,
                                fontWeight = if (selectedSocialTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedSocialTab == 0) Color(0xFF4B4FE4) else Color(0xFF9CA3AF)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.5.dp)
                                .background(if (selectedSocialTab == 0) Color(0xFF4B4FE4) else Color(0xFFE5E7EB))
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
                                tint = if (selectedSocialTab == 1) Color(0xFF4B4FE4) else Color(0xFF9CA3AF),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TikTok",
                                fontSize = 16.sp,
                                fontWeight = if (selectedSocialTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedSocialTab == 1) Color(0xFF4B4FE4) else Color(0xFF9CA3AF)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.5.dp)
                                .background(if (selectedSocialTab == 1) Color(0xFF4B4FE4) else Color(0xFFE5E7EB))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Handle Subheader Link
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { }
                ) {
                    Text(
                        text = if (selectedSocialTab == 0) "liadiotima" else "liadiotima.ugc",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4FE4)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF4B4FE4),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Social Grid (Instagram / TikTok)
                val currentFeedPosts = if (selectedSocialTab == 0) instagramPosts else tiktokPosts

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    currentFeedPosts.chunked(3).forEach { rowPosts ->
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
                                        model = post.mediaUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    // Badge Icon top left
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

                                    if (post.isVideo) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.9f),
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .size(28.dp)
                                        )
                                    }

                                    // Bottom Gradient Scrim & Stats
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
                                        Text(" ${post.likes} ", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Icon(Icons.Outlined.ChatBubbleOutline, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        Text(" ${post.comments}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
