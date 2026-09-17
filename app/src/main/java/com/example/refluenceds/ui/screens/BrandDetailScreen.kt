package com.example.refluenceds.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.*
import com.example.refluenceds.ui.components.AppPullToRefreshBox
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
import coil.compose.AsyncImage
import com.example.refluenceds.R
import com.example.refluenceds.data.remote.dto.*
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.theme.GradientStart
import com.example.refluenceds.ui.viewmodel.CampaignViewModel
import com.example.refluenceds.utils.SetStatusBarAppearance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandDetailScreen(
    brandId: String,
    brandName: String = "",
    viewModel: CampaignViewModel,
    onBack: () -> Unit,
    onNavigateToCampaignDetail: (String) -> Unit = {},
    onNavigateToReviews: (brandId: String, brandName: String) -> Unit = { _, _ -> }
) {
    SetStatusBarAppearance(isLightStatusBars = !AppTheme.isDark)
    val context = LocalContext.current

    LaunchedEffect(brandId, brandName) {
        viewModel.fetchBrandDetail(brandId = brandId, brandName = brandName)
    }

    val brandDetail by viewModel.brandDetail.collectAsState()
    val isLoading by viewModel.isBrandDetailLoading.collectAsState()
    val activeCampaigns by viewModel.brandActiveCampaigns.collectAsState()
    val pastCampaigns by viewModel.brandPastCampaigns.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Active, 1: Past Campaigns

    val brand = brandDetail ?: BrandItemDto(
        id = brandId,
        companyName = brandName.ifBlank { "Brand" },
        name = brandName.ifBlank { "Brand" },
        description = "",
        isFollowing = false,
        followersCount = 0,
        activeCampaignsCount = 0,
        rating = 0.0,
        ratingsCount = 0
    )

    val isFollowing = brand.isFollowing == true
    val isDark = AppTheme.isDark

    val defaultDescription = "Ricardo ist eine 1997 in Baar gegründete Schweizer Online-Auktionsplattform, die eine breite Palette an Produkten für Käufer und Verkäufer anbietet. Wir ermöglichen den sicheren Handel mit vielfältigen Neu- und Gebrauchtwaren, von Kleidung und Accessoires bis hin zu Fahrzeugen und Elektronik, durch flexible Optionen wie Sofortkauf, Auktionen und Preisvorschläge. Als etablierter Marktplatz bieten wir eine benutzerfreundliche Umgebung für den Kauf und Verkauf."
    val descriptionText = brand.description.takeIf { !it.isNullOrBlank() } ?: defaultDescription

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.surface,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = AppTheme.colors.textPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        AppPullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = {
                viewModel.fetchBrandDetail(brandId = brandId, brandName = brandName)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            // ── Top Brand Profile Section ────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Circular Logo
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        val logoUrl = brand.effectiveLogo.orEmpty()
                        if (logoUrl.isNotBlank()) {
                            AsyncImage(
                                model = logoUrl,
                                contentDescription = brand.displayName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = brand.displayName.take(1).uppercase().ifBlank { "R" },
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Brand Name
                    Text(
                        text = brand.displayName.ifBlank { "Brand" },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Follow / Following Pill Button
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
                        onClick = {
                            brand.id?.let { viewModel.toggleBrandFollow(it) }
                                ?: viewModel.toggleBrandFollow(brand.displayName)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(40.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = followContainerColor,
                            contentColor = followContentColor
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            imageVector = if (isFollowing) Icons.Filled.NotificationsActive else Icons.Default.NotificationsNone,
                            contentDescription = null,
                            modifier = Modifier.size(17.dp),
                            tint = followContentColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isFollowing) "Following" else "Follow",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Brand Description
                    Text(
                        text = descriptionText,
                        fontSize = 13.5.sp,
                        color = AppTheme.colors.textSecondary,
                        lineHeight = 21.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── Social & Website Card ────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                    border = BorderStroke(1.dp, AppTheme.colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Instagram Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    val igUrl = "https://instagram.com"
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(igUrl)).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_social_instagram),
                                contentDescription = "Instagram",
                                tint = Color(0xFF5B61F4),
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier.height(32.dp),
                            color = AppTheme.colors.divider
                        )

                        // Website Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    val webUrl = brand.website ?: "https://www.ricardo.ch"
                                    val formatted = if (!webUrl.startsWith("http")) "https://$webUrl" else webUrl
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formatted)).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Language,
                                contentDescription = "Website",
                                tint = Color(0xFF5B61F4),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }

            // ── Stats Card (Campaigns & Influencer Collab) ────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                    border = BorderStroke(1.dp, AppTheme.colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Campaigns Col
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = AppTheme.colors.textPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Campaigns",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTheme.colors.textPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${brand.activeCampaignsCount ?: 13}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier.height(44.dp),
                            color = AppTheme.colors.divider
                        )

                        // Influencer Collab Col
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = AppTheme.colors.textPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Influencer collab",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTheme.colors.textPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${brand.followersCount ?: 130}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )
                        }
                    }
                }
            }

            // ── Reviews Card ─────────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onNavigateToReviews(
                                brand.id?.toString() ?: brandId,
                                brand.displayName
                            )
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                    border = BorderStroke(1.dp, AppTheme.colors.border)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "${brand.displayName}'s Reviews",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Star Rating Row (5 stars in purple-magenta gradient colors)
                            val starColors = listOf(
                                Color(0xFF5B61F4),
                                Color(0xFF7C3AED),
                                Color(0xFF9333EA),
                                Color(0xFFC026D3),
                                Color(0xFFE11D48)
                            )
                            val ratingVal = (brand.rating ?: 0.0)
                            val reviewCount = brand.ratingsCount ?: 0
                            val clampedStars = ratingVal.toInt().coerceIn(0, 5)

                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                starColors.forEachIndexed { idx, color ->
                                    val isFilled = if (reviewCount > 0) idx < clampedStars else false
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (isFilled) color else (if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            val ratingFormatted = if (ratingVal % 1.0 == 0.0) ratingVal.toInt().toString() else "%.1f".format(ratingVal)
                            val ratingText = if (reviewCount > 0) "$ratingFormatted/5 ($reviewCount)" else "No reviews yet"

                            Text(
                                text = ratingText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppTheme.colors.textPrimary
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                                contentDescription = "View Reviews",
                                tint = AppTheme.colors.textPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // ── Tabs (Active / Past Campaigns) ───────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Active Tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Active",
                            fontSize = 15.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) Color(0xFF5B61F4) else AppTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (selectedTab == 0) {
                            Box(
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(2.5.dp)
                                    .background(Color(0xFF5B61F4), RoundedCornerShape(2.dp))
                            )
                        } else {
                            Spacer(modifier = Modifier.height(2.5.dp))
                        }
                    }

                    // Past Campaigns Tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Past Campaigns",
                            fontSize = 15.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) Color(0xFF5B61F4) else AppTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (selectedTab == 1) {
                            Box(
                                modifier = Modifier
                                    .width(72.dp)
                                    .height(2.5.dp)
                                    .background(Color(0xFF5B61F4), RoundedCornerShape(2.dp))
                            )
                        } else {
                            Spacer(modifier = Modifier.height(2.5.dp))
                        }
                    }
                }
            }

            // ── Campaigns Content / Empty Card ───────────────────────────────
            val currentList = if (selectedTab == 0) activeCampaigns else pastCampaigns
            if (currentList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(
                                color = if (isDark) Color(0xFF252840) else Color(0xFFEEF2FF),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedTab == 0) "No active campaigns" else "No past campaigns",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF818CF8) else Color(0xFF5B61F4)
                        )
                    }
                }
            } else {
                items(currentList, key = { it.id?.toString() ?: "" }) { campaignDto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                campaignDto.id?.let { onNavigateToCampaignDetail(it.toString()) }
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                        border = BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = campaignDto.effectiveImageUrl,
                                contentDescription = campaignDto.title,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = campaignDto.title.orEmpty(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = AppTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = campaignDto.effectiveCompensation,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF5B61F4)
                                )
                            }
                        }
                    }
                }
            }
        }
        }
    }
}
