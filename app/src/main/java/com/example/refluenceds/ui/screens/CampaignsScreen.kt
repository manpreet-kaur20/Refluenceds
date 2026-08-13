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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.refluenceds.domain.model.Campaign
import com.example.refluenceds.ui.components.SkeletonItem
import com.example.refluenceds.ui.theme.GradientStart
import com.example.refluenceds.ui.viewmodel.CampaignViewModel

import androidx.compose.ui.res.painterResource
import com.example.refluenceds.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignsScreen(
    viewModel: CampaignViewModel,
    onNavigateToCampaignDetail: (String) -> Unit = {}
) {
    val campaigns by viewModel.campaigns.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var isGridView by remember { mutableStateOf(true) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Recommended") }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    val filterSheetState = rememberModalBottomSheetState()

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
                        modifier = Modifier.clickable { showCategoryDropdown = !showCategoryDropdown },
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
                                tint = Color(0xFF4B4FE4)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedCategory,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF4B4FE4)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = if (showCategoryDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF4B4FE4)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false },
                        modifier = Modifier
                            .width(220.dp)
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
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
                                        color = if (isSelected) Color(0xFF4B4FE4) else Color(0xFF1D1B36),
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
                                        tint = if (isSelected) Color(0xFF4B4FE4) else Color(0xFF1D1B36)
                                    )
                                },
                                modifier = Modifier.background(
                                    if (isSelected) Color(0xFFF7F7FA) else Color.Transparent
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
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFEBEBF2))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter",
                                tint = Color(0xFF4B4FE4),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        onClick = { isGridView = !isGridView },
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFEBEBF2))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_view_toggle),
                                contentDescription = "Toggle Layout",
                                tint = Color(0xFF4B4FE4),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (isLoading) {
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
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
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(3) {
                            SkeletonItem(modifier = Modifier.fillMaxWidth().height(450.dp).clip(RoundedCornerShape(16.dp)))
                        }
                    }
                }
            } else if (selectedCategory == "Eligible") {
                EligibleEmptyState()
            } else {
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(campaigns) { campaign ->
                            CampaignGridItem(
                                campaign = campaign,
                                onClick = { onNavigateToCampaignDetail(campaign.id) }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(campaigns) { campaign ->
                            CampaignListItem(
                                campaign = campaign,
                                onClick = { onNavigateToCampaignDetail(campaign.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = filterSheetState,
                containerColor = Color.White
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
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    FilterOption("Instagram Campaigns")
                    FilterOption("TikTok Campaigns")
                    FilterOption("UGC Campaigns")
                    FilterOption("Instagram Stories Only")
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
            color = Color(0xFF1D1B36)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Verify your social accounts or apply to ugc to see what campaigns you are eligible for.",
            fontSize = 14.sp,
            color = Color(0xFF5A5A72),
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
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF0F0F6))
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
                    tint = Color(0xFF1D1B36)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1D1B36)
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
fun FilterOption(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 16.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        fontSize = 16.sp,
        color = Color.Gray
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
        AsyncImage(
            model = campaign.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "android.resource://com.example.refluenceds/${R.drawable.refluenced_ag_logo}",
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("•", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            
            if (campaign.title.contains("Schogetten")) {
                Icon(Icons.Default.AttachMoney, null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                Spacer(modifier = Modifier.width(2.dp))
                Icon(Icons.Default.Videocam, null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                Text(" 1 ", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Default.AddCircleOutline, null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                Text(" 3", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
            } else if (campaign.title.contains("FW26") || campaign.title.contains("Lash")) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_social_tiktok),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.DarkGray
                )
                Text(" 1", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
            } else {
                Icon(Icons.Default.Videocam, null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                Text(" 1 ", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Default.AddCircleOutline, null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                Text(" 3", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = campaign.title,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D1B36),
            maxLines = 2,
            lineHeight = 17.sp
        )
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
            .height(450.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = campaign.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Overlay Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AsyncImage(
                    model = "https://picsum.photos/seed/${campaign.brandName}/50",
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp)
                )
                Row {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.MoreHoriz, null, modifier = Modifier.padding(8.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.FavoriteBorder, null, modifier = Modifier.padding(8.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text("🇬🇧 London 🎡", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("∞ Ongoing", color = Color.White, fontSize = 14.sp)
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                Icon(Icons.Default.Videocam, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Text(" 1 ", color = Color.White)
                Icon(Icons.Default.AddCircleOutline, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Text(" 3", color = Color.White)
            }
            
            Text("Product", color = Color.White, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(4) { index ->
                    AsyncImage(
                        model = "https://picsum.photos/seed/prod$index/100",
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
