package com.example.refluenceds.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.PhotoSizeSelectActual
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.refluenceds.R
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.utils.SetStatusBarAppearance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaysToEarnScreen(
    onBack: () -> Unit = {},
    onNavigateToReferrals: () -> Unit = {},
    onNavigateToUgcInfo: () -> Unit = {},
    onNavigateToCampaigns: () -> Unit = {}
) {
    SetStatusBarAppearance(isLightStatusBars = true)

    Scaffold(
        containerColor = AppTheme.colors.background,
        topBar = {
            Surface(
                color = AppTheme.colors.surface,
                shadowElevation = 0.5.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppTheme.colors.textPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = "Ways to Earn",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── 1. INVITE A FRIEND ───────────────────────────────────────────
            item {
                WaysToEarnActionCard(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_smile_plus),
                            contentDescription = null,
                            tint = Color(0xFF9333EA),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    title = "Invite a friend",
                    description = "Every friend that applies for a campaign after signing up with your code earns you 20 USD.",
                    onClick = onNavigateToReferrals
                )
            }

            // ── 2. PARTICIPATE IN UGC CAMPAIGNS ──────────────────────────────
            item {
                WaysToEarnActionCard(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Videocam,
                            contentDescription = null,
                            tint = Color(0xFFEC4899),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    title = "Participate in UGC campaigns",
                    description = "First become UGC verified then apply to any UGC campaign on our platform.",
                    onClick = onNavigateToUgcInfo
                )
            }

            // ── 3. COMPLETE A PAID CAMPAIGN ──────────────────────────────────
            item {
                WaysToEarnActionCard(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.MonetizationOn,
                            contentDescription = null,
                            tint = Color(0xFFA855F7),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    title = "Complete a paid campaign",
                    description = "Campaigns with this icon include a cash reward.",
                    onClick = onNavigateToCampaigns
                )
            }

            // ── 4. CONTENT BUYOUTS ───────────────────────────────────────────
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AppTheme.colors.surface,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF9333EA).copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoSizeSelectActual,
                                contentDescription = null,
                                tint = Color(0xFF9333EA),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Content buyouts",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Brands have the opportunity to purchase your content after the campaign ends. For every purchase you make USD 49 on top of all other compensation!",
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = AppTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }

            // ── 5. PAYMENT PROVIDER INFO CARD ────────────────────────────────
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AppTheme.colors.surface,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = AppTheme.colors.textSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Payments through our payment provider",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "We use a payment provider to handle all payouts any issues with transfers or payments please contact them.\n\nFor creators whose local regulations require a tax identification number, providing it will be necessary to complete your account setup.",
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = AppTheme.colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WaysToEarnActionCard(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = AppTheme.colors.surface,
        border = BorderStroke(1.dp, AppTheme.colors.border),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppTheme.colors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = AppTheme.colors.textSecondary
                    )
                }
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
