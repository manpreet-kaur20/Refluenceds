package com.example.refluenceds.ui.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.refluenceds.R
import com.example.refluenceds.ui.theme.GradientStart
import com.example.refluenceds.ui.viewmodel.CampaignViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CampaignDetailScreen(
    campaignId: String = "1",
    viewModel: CampaignViewModel,
    onBack: () -> Unit
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
    var isApplied by remember { mutableStateOf(false) }

    val campaignList by viewModel.campaigns.collectAsState()
    val campaign = campaignList.find { it.id == campaignId } ?: campaignList.firstOrNull()

    val brandName = campaign?.brandName ?: "Navah Cosmetics GmbH"
    val campaignTitle = campaign?.title ?: "Desert Escapes Your Ritual"

    fun shareCampaign() {
        val shareText = "Check out $campaignTitle on Refluenceds!\nhttps://refluenceds.com/campaign/$campaignId"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share campaign")
        context.startActivity(shareIntent)
        Toast.makeText(context, "Sharing campaign...", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            // Sticky Floating Bottom Bar (Image 5)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                color = Color.White,
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
                                text = if (isApplied) "36" else "35",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4B4FE4)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = Color(0xFF4B4FE4),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Influencers applied",
                            fontSize = 12.sp,
                            color = Color(0xFF5A5A72)
                        )
                    }

                    Button(
                        onClick = {
                            if (!isApplied) {
                                isApplied = true
                                showApplyDialog = true
                                viewModel.applyToCampaign(campaignId)
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
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // ── 1. Hero Header Banner (Image 1) ──────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) {
                    AsyncImage(
                        model = if (!campaign?.imageUrl.isNullOrEmpty()) campaign!!.imageUrl else "android.resource://com.example.refluenceds/${R.drawable.fashion_woman}",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Scrim gradient at top & bottom for legibility
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.45f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.75f)
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
                            // Back Button
                            Surface(
                                onClick = onBack,
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color(0xFF1D1B36),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Brand Logo Badge
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(40.dp),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "N",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF1D1B36)
                                    )
                                }
                            }
                        }

                        // Right Action Buttons (3 dots & Favorite)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box {
                                Surface(
                                    onClick = { showMenu = !showMenu },
                                    shape = CircleShape,
                                    color = Color.White,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.MoreHoriz,
                                            contentDescription = "More Options",
                                            tint = Color(0xFF1D1B36),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Popup Options Dropdown Menu (Image 1)
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false },
                                    modifier = Modifier
                                        .width(220.dp)
                                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Follow brand", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) },
                                        onClick = {
                                            showMenu = false
                                            Toast.makeText(context, "Following $brandName", Toast.LENGTH_SHORT).show()
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color(0xFF1D1B36))
                                        }
                                    )
                                    HorizontalDivider(color = Color(0xFFF0F0F5))
                                    DropdownMenuItem(
                                        text = { Text("Hide campaign", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) },
                                        onClick = {
                                            showMenu = false
                                            Toast.makeText(context, "Campaign hidden", Toast.LENGTH_SHORT).show()
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = Color(0xFF1D1B36))
                                        }
                                    )
                                    HorizontalDivider(color = Color(0xFFF0F0F5))
                                    DropdownMenuItem(
                                        text = { Text("Share campaign", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) },
                                        onClick = {
                                            showMenu = false
                                            shareCampaign()
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Outlined.Share, contentDescription = null, tint = Color(0xFF1D1B36))
                                        }
                                    )
                                    HorizontalDivider(color = Color(0xFFF0F0F5))
                                    DropdownMenuItem(
                                        text = { Text("Report campaign", fontWeight = FontWeight.SemiBold, color = Color(0xFFE53935), fontSize = 15.sp) },
                                        onClick = {
                                            showMenu = false
                                            showReportSheet = true
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE53935))
                                        },
                                        modifier = Modifier.background(Color(0xFFFFF0F0))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Heart Favorite Button
                            Surface(
                                onClick = { isFavorite = !isFavorite },
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isFavorite) Color(0xFFE53935) else Color(0xFF1D1B36),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Hero Banner Text Overlay (Bottom of banner)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Text(
                            text = campaignTitle,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 34.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Clock subtext
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "5 more days accepting applications",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // People applied line
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "35 applied",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "  •  ",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.ic_social_tiktok),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = " 1",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // ── 2. Translation Bar & Help Banner (Image 1) ───────────────────────
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Translated to English",
                            fontSize = 13.sp,
                            color = Color(0xFF75758A)
                        )
                        Text(
                            text = "Show Original",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4B4FE4),
                            modifier = Modifier.clickable { }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ask about this campaign Card (Image 1)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFEBEBF4))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xFFF3F4F6)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_help_bubble),
                                            contentDescription = null,
                                            tint = Color(0xFF00897B),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "NEED HELP?",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1D1B36)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Ask about this campaign",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B36)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Instant answers no waiting on the brand",
                                    fontSize = 13.sp,
                                    color = Color(0xFF75758A)
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF1D1B36),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── 3. Campaign Dates (Image 1) ──────────────────────────────────
                    Text(
                        text = "Campaign dates",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B36)
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
                            color = Color(0xFFECECFF)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Aug 21",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4B4FE4)
                                )
                            }
                        }

                        Text(
                            text = "—",
                            fontSize = 18.sp,
                            color = Color(0xFFD0D0E0),
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFECECFF)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Aug 26",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4B4FE4)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F5))

                // ── 4. Brand Top Header Bar (Image 2 Top Bar) ────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF3F3FE),
                            modifier = Modifier.size(36.dp),
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("N", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = brandName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B36)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            Surface(
                                onClick = { showBrandMenu = !showBrandMenu },
                                shape = CircleShape,
                                color = Color(0xFFF3F3FE),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.MoreHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }

                            DropdownMenu(
                                expanded = showBrandMenu,
                                onDismissRequest = { showBrandMenu = false },
                                modifier = Modifier
                                    .width(220.dp)
                                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Follow brand", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) },
                                    onClick = {
                                        showBrandMenu = false
                                        Toast.makeText(context, "Following $brandName", Toast.LENGTH_SHORT).show()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color(0xFF1D1B36))
                                    }
                                )
                                HorizontalDivider(color = Color(0xFFF0F0F5))
                                DropdownMenuItem(
                                    text = { Text("Hide campaign", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) },
                                    onClick = {
                                        showBrandMenu = false
                                        Toast.makeText(context, "Campaign hidden", Toast.LENGTH_SHORT).show()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = Color(0xFF1D1B36))
                                    }
                                )
                                HorizontalDivider(color = Color(0xFFF0F0F5))
                                DropdownMenuItem(
                                    text = { Text("Share campaign", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) },
                                    onClick = {
                                        showBrandMenu = false
                                        shareCampaign()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Outlined.Share, contentDescription = null, tint = Color(0xFF1D1B36))
                                    }
                                )
                                HorizontalDivider(color = Color(0xFFF0F0F5))
                                DropdownMenuItem(
                                    text = { Text("Report campaign", fontWeight = FontWeight.SemiBold, color = Color(0xFFE53935), fontSize = 15.sp) },
                                    onClick = {
                                        showBrandMenu = false
                                        showReportSheet = true
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE53935))
                                    },
                                    modifier = Modifier.background(Color(0xFFFFF0F0))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            onClick = { shareCampaign() },
                            shape = CircleShape,
                            color = Color(0xFFF3F3FE),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F5))

                // ── 5. Campaign Description (Image 2) ────────────────────────────────
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        text = "Campaign description",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B36)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Welcome to NAVAH Cosmetics! 🤍",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1D1B36)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val annotatedDesc = buildAnnotatedString {
                        append("For our ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Desert Escapes Campaign") }
                        append(", we are looking for creators who love high-quality beauty products and want to authentically stage our ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Summer Ritual") }
                        append(".\nWith our ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Desert Escapes PR Box") }
                        append(", we want ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("to take you on a little Summer Escape") }
                        append(" – inspired by warm summer days, golden hour, and effortless beauty.\nAt the ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("heart") }
                        append(" is our ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("hormone-free eyelash serum") }
                        append(", which makes eyelashes appear ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("visibly longer, fuller, and naturally darker") }
                        append(".")
                    }

                    Text(
                        text = annotatedDesc,
                        fontSize = 14.sp,
                        color = Color(0xFF4A4A62),
                        lineHeight = 22.sp,
                        maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 4
                    )

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
                            tint = Color(0xFF1D1B36),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F5))

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── 6. Dont's for the campaign (Image 2) ──────────────────────────
                    Text(
                        text = "Dont's for the campaign",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B36)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val donts = listOf(
                        "No medical or misleading promises of cure (e.g., guaranteed results or unrealistic timeframes).",
                        "Please do not directly compare or mention competitor products or brands.",
                        "No heavily edited filters or effects that distort the result.",
                        "Please do not present the product as a substitute for medical treatments."
                    )

                    donts.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "• ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1B36)
                            )
                            Text(
                                text = item,
                                fontSize = 14.sp,
                                color = Color(0xFF4A4A62),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F5))

                // ── 7. Deliverables & Products (Image 3) ─────────────────────────────
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Text(
                        text = "Deliverables",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B36)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Deliverable Card
                    Surface(
                        modifier = Modifier
                            .width(160.dp)
                            .height(170.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFECECFF)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "TikTok",
                                fontSize = 14.sp,
                                color = Color(0xFF4B4FE4),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_social_tiktok),
                                contentDescription = null,
                                tint = Color(0xFF4B4FE4),
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "x1",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4B4FE4)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // ── Products Section ─────────────────────────────────────────────
                    Text(
                        text = "Products",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B36)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Product Value Box
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFECECFF)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Product value",
                                fontSize = 13.sp,
                                color = Color(0xFF4B4FE4)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "EUR 50",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4B4FE4)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Product Images Gallery
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val productImages = listOf(
                            "android.resource://com.example.refluenceds/${R.drawable.beauty}",
                            "android.resource://com.example.refluenceds/${R.drawable.coral_wash}",
                            "android.resource://com.example.refluenceds/${R.drawable.phantom_parfum}"
                        )
                        items(productImages) { url ->
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

                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F5))

                // ── 8. Direction for campaign (Image 4) ──────────────────────────────
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Text(
                        text = "Direction for campaign",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B36)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val annotatedDirection = buildAnnotatedString {
                        append("Create ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("authentic, high-quality") }
                        append(" content around our ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Desert Escapes PR Box") }
                        append(" and the ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("NAVAH Eyelash Serum") }
                        append(". Start your content with the ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Desert Escapes Box") }
                        append(": Show the ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("box") }
                        append(", the ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("unboxing") }
                        append(", and the ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("included Summer Essentials") }
                        append(" in an ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("aesthetic way") }
                        append(" - ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("take your community along") }
                        append(" during the unboxing.\n\nIn the second part, our ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("NAVAH Eyelash Serum") }
                        append(" should be ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("the focus as the hero product") }
                        append(". Show the ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("serum") }
                        append(" clearly ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("visible") }
                        append(" and ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("integrate a short application") }
                        append(" on the ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("upper lash line") }
                        append(", so your community directly understands how the product is used.\n\nThe special feature: ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Our hormone-free eyelash serum") }
                        append(" not only supports ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("longer and fuller") }
                        append(", but also ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("naturally darker") }
                        append(" lashes – ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("an advantage") }
                        append(" that ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("many eyelash serums do not offer") }
                        append(".\n\nFor the creative implementation, ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("we still want to give you as much freedom as possible") }
                        append(". Whether ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Unboxing, Summer Essentials, Vacation Prep, Get Unready With Me") }
                        append(", or ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Evening Routine") }
                        append(" – it ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("is important") }
                        append(" to us that the ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("PR Box, content, and application of the serum") }
                        append(" are part of your content and that ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("the implementation matches your personal style") }
                        append(".\n\nIn the appendix, you will find a compact ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("OnePager") }
                        append(" for orientation with ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("all important information") }
                        append(" about our ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Eyelash Serum") }
                        append(".")
                    }

                    Text(
                        text = annotatedDirection,
                        fontSize = 14.sp,
                        color = Color(0xFF4A4A62),
                        lineHeight = 22.sp
                    )
                }

                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F5))

                // ── 9. Brand offers & Banners (Image 5) ──────────────────────────────
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Text(
                        text = "Brand offers",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B36)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val annotatedBrandOffers = buildAnnotatedString {
                        append("The Navah Cosmetics Eyelash Serum combines everything you could wish for from a modern eyelash serum: ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("visibly longer, fuller, and naturally darker lashes") }
                        append(" – completely without hormones.\n\nOur ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("innovative formula") }
                        append(" combines a powerful peptide complex with selected plant-based ingredients such as argan oil, red clover extract, green tea extract, and vitamin E. The result: stronger, healthier-looking lashes with more length, volume, and more intense natural color.\n\nThe serum is ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("vegan") }
                        append(", dermatologically tested, and can be effortlessly integrated into your daily beauty routine. Perfect for anyone who desires expressive lashes – even without mascara.")
                    }

                    Text(
                        text = annotatedBrandOffers,
                        fontSize = 14.sp,
                        color = Color(0xFF4A4A62),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Branded Campaign Banner Card (Image 5)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF6FAF7)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Branded Campaign",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00897B)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Brands will be able to promote your content with TikTok \"Spark ads\". This will let the company turn your content into a Spark Ad and give you more exposure after the campaign is over!",
                                fontSize = 13.sp,
                                color = Color(0xFF4A4A62),
                                lineHeight = 19.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                onClick = { },
                                shape = RoundedCornerShape(50),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFF4B4FE4))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Learn more",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4B4FE4)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF4B4FE4),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // No Copyright Music Card (Image 5)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFAF9FF)
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFFFF0F0), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicOff,
                                    contentDescription = null,
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "No Copyright Music",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B36)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "The music provided by Instagram and TikTok is not approved for commercial usage.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF75758A),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(60.dp))
                }
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
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    "Your application for Desert Escapes Campaign has been sent to Navah Cosmetics. You will receive updates in your inbox.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            },
            confirmButton = {
                Button(
                    onClick = { showApplyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B4FE4))
                ) {
                    Text("Awesome!")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    // Report Campaign Bottom Sheet (Image 1)
    if (showReportSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReportSheet = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = Color.White,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 6.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .background(Color(0xFFE0E0E0), shape = CircleShape)
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
                    text = "Report Desert Escapes 🌴 Your Summer Lash Ritual",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B36),
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
                                isSelected = selectedReportReason == option,
                                onClick = {
                                    selectedReportReason = option
                                    Toast.makeText(context, "Report submitted for '$option'. Thank you!", Toast.LENGTH_SHORT).show()
                                    showReportSheet = false
                                }
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row2.forEach { option ->
                            ReportChipItem(
                                text = option,
                                isSelected = selectedReportReason == option,
                                onClick = {
                                    selectedReportReason = option
                                    Toast.makeText(context, "Report submitted for '$option'. Thank you!", Toast.LENGTH_SHORT).show()
                                    showReportSheet = false
                                }
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row3.forEach { option ->
                            ReportChipItem(
                                text = option,
                                isSelected = selectedReportReason == option,
                                onClick = {
                                    selectedReportReason = option
                                    Toast.makeText(context, "Report submitted for '$option'. Thank you!", Toast.LENGTH_SHORT).show()
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
fun ReportChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) Color(0xFFECECFF) else Color.White,
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF4B4FE4) else Color(0xFFD6D6E8))
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4B4FE4),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}
