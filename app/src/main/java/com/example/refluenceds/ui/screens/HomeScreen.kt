package com.example.refluenceds.ui.screens

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
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import com.example.refluenceds.ui.components.SkeletonItem
import com.example.refluenceds.ui.theme.GradientEnd
import com.example.refluenceds.ui.theme.GradientStart
import com.example.refluenceds.ui.viewmodel.CampaignViewModel

@Composable
fun HomeScreen(
    viewModel: CampaignViewModel,
    onViewAcademyAll: () -> Unit,
    onViewCampaignsAll: () -> Unit,
    onViewBrandsAll: () -> Unit,
    onNavigateToAcademyDetail: (String) -> Unit = {},
    onNavigateToCampaignDetail: (String) -> Unit = {},
    onNavigateToYourReferrals: () -> Unit = {}
) {
    val tutorials by viewModel.tutorials.collectAsState()
    val campaigns by viewModel.campaigns.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showDialog by rememberSaveable { mutableStateOf(true) }
    var showReferralSheet by remember { mutableStateOf(false) }

    if (showDialog) {
        InstagramFollowDialog(onDismiss = { showDialog = false })
    }

    if (showReferralSheet) {
        ReferAFriendBottomSheet(
            onDismissRequest = { showReferralSheet = false },
            onViewReferralsClick = {
                showReferralSheet = false
                onNavigateToYourReferrals()
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Circle Options (Image 1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                TopCircleItem(
                    title = "Tutorial",
                    iconRes = R.drawable.app_icon, // Placeholder
                    onClick = { viewModel.triggerLoading() }
                )
                TopCircleItem(
                    title = "Refluenced",
                    iconRes = R.drawable.app_icon, // Placeholder
                    onClick = { viewModel.triggerLoading() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F1F1))

            // Refer a Friend Banner (Image 1)
            ReferralBanner(onReferClick = { showReferralSheet = true })

            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F1F1))

            // Recommended Campaigns Section
            SectionHeader(
                title = "Recommended campaigns",
                onViewAll = onViewCampaignsAll
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(280.dp)
            ) {
                if (isLoading) {
                    items(3) {
                        SkeletonItem(modifier = Modifier.width(200.dp).height(240.dp).clip(RoundedCornerShape(16.dp)))
                    }
                } else {
                    items(campaigns) { campaign ->
                        RecommendedCampaignCard(
                            campaign = campaign,
                            onClick = { onNavigateToCampaignDetail(campaign.id) }
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F1F1))

            // Referenced Academy Section
        SectionHeader(title = "Referenced Academy", onViewAll = onViewAcademyAll)
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.wrapContentHeight()
        ) {
            if (isLoading) {
                items(3) {
                    SkeletonItem(modifier = Modifier.width(160.dp).height(240.dp).clip(RoundedCornerShape(12.dp)))
                }
            } else {
                items(tutorials) { tutorial ->
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

        // Explore Brands Section
        SectionHeader(title = "Explore Brands", onViewAll = onViewBrandsAll)
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.wrapContentHeight().padding(bottom = 24.dp)
        ) {
                if (isLoading) {
                    items(3) {
                        SkeletonItem(modifier = Modifier.width(200.dp).height(200.dp).clip(RoundedCornerShape(16.dp)))
                    }
                } else {
                    items(3) { index ->
                        BrandCard(
                            name = when (index) {
                                0 -> "Ricardo AG"
                                1 -> "SunIce Festival"
                                else -> "Liebeskind Berlin"
                            },
                            categories = when (index) {
                                0 -> "FASHION • JEWELRY • SUSTAINABILITY • HOME"
                                1 -> "EVENTS • LIFESTYLE"
                                else -> "FASHION • LIFESTYLE"
                            },
                            logoUrl = when (index) {
                                0 -> "android.resource://com.example.refluenceds/${R.drawable.ricardo_ag_logo}"
                                1 -> "android.resource://com.example.refluenceds/${R.drawable.sunice_festival_logo}"
                                else -> "android.resource://com.example.refluenceds/${R.drawable.liebeskind_berlin_logo}"
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // Increased padding for bottom nav
        }
    }
}

@Composable
fun InstagramFollowDialog(onDismiss: () -> Unit) {
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
                        onClick = onDismiss,
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
    campaign: com.example.refluenceds.domain.model.Campaign,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = campaign.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://picsum.photos/seed/${campaign.brandName}/50",
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("•", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.Videocam, null, modifier = Modifier.size(16.dp))
            Text(" 1", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.AddCircleOutline, null, modifier = Modifier.size(16.dp))
            Text(" 3", fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🇬🇧 London", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.Stars, null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFD700))
        }
    }
}

@Composable
fun TopCircleItem(title: String, iconRes: Int, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(listOf(GradientStart, GradientEnd)),
                    shape = CircleShape
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = Color.White
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ReferralBanner(onReferClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFFAF5FF), RoundedCornerShape(10.dp)),
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
                "Refer a Friend and Earn!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B36)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "20 EUR",
                fontSize = 14.sp,
                color = Color(0xFF8B5CF6),
                fontWeight = FontWeight.Bold
            )
        }
        Surface(
            onClick = onReferClick,
            shape = RoundedCornerShape(50),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E2EC))
        ) {
            Text(
                text = "Refer",
                color = Color(0xFF4B4FE4),
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
                tint = Color(0xFF1D1B36)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B36)
            )
        }
        Text(
            text = "View all",
            fontSize = 13.sp,
            color = Color(0xFF6B66FF),
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
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
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
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Bolt, null, modifier = Modifier.size(12.dp))
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(category, fontSize = 11.sp, color = Color.Gray)
        }
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun CategoryFilter(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onCategorySelected(category) },
                color = if (isSelected) Color(0xFF6B66FF) else Color(0xFFF4F4F6),
                contentColor = if (isSelected) Color.White else Color(0xFF333333)
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 11.dp),
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun FilteredCampaignCard(
    campaign: com.example.refluenceds.domain.model.Campaign,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8FF))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = campaign.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(campaign.brandName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(campaign.category, color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Text(" London", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun BrandCard(name: String, categories: String, logoUrl: String) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = logoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                categories,
                fontSize = 9.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GradientStart)
            ) {
                Icon(Icons.Default.NotificationsNone, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Follow", fontSize = 14.sp)
            }
        }
    }
}
