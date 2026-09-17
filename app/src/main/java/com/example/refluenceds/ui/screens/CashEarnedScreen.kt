package com.example.refluenceds.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.refluenceds.ui.viewmodel.AuthViewModel
import com.example.refluenceds.utils.SetStatusBarAppearance

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.refluenceds.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashEarnedScreen(
    authViewModel: AuthViewModel? = null,
    onBack: () -> Unit = {},
    onNavigateToWaysToEarn: () -> Unit = {},
    onNavigateToContactUs: () -> Unit = {}
) {
    SetStatusBarAppearance(isLightStatusBars = true)

    val userProfile by authViewModel?.userProfile?.collectAsState() ?: remember { mutableStateOf(null) }
    val currency = "USD"
    val balance = "0,00"

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Earnings, 1: Payouts
    var showExplanationSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        containerColor = AppTheme.colors.background,
        topBar = {
            Surface(
                color = AppTheme.colors.surface,
                shadowElevation = 0.5.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            onClick = onBack,
                            shape = CircleShape,
                            color = AppTheme.colors.surfaceVariant,
                            border = BorderStroke(1.dp, AppTheme.colors.border),
                            modifier = Modifier.size(38.dp)
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

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = "Cash Earned",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                    }

                    Text(
                        text = "Ways to Earn",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onNavigateToWaysToEarn() }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
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
            // ── 1. READY NOW BALANCE CARD ────────────────────────────────────
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AppTheme.colors.surface,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "READY NOW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textSecondary,
                            letterSpacing = 0.8.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "$currency $balance",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AppTheme.colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Transfer Button (Disabled gradient pill)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(percent = 50))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF8B80F9).copy(alpha = 0.7f),
                                            Color(0xFFE07A9A).copy(alpha = 0.7f)
                                        )
                                    )
                                )
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Earn CHF 40 to transfer",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.95f)
                            )
                        }
                    }
                }
            }

            // ── 2. "WHEN DO I GET PAID?" CARD
            item {
                Surface(
                    onClick = { showExplanationSheet = true },
                    shape = RoundedCornerShape(16.dp),
                    color = AppTheme.colors.surface,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AppTheme.colors.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                    tint = AppTheme.colors.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "When do I get paid?",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "The 30 days, explained",
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

            // ── 3. EARNINGS / PAYOUTS TAB SWITCHER ───────────────────────────
            item {
                Surface(
                    shape = RoundedCornerShape(percent = 50),
                    color = AppTheme.colors.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Earnings Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(percent = 50))
                                .background(if (selectedTab == 0) AppTheme.colors.surface else Color.Transparent)
                                .clickable { selectedTab = 0 },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Earnings",
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 0) AppTheme.colors.textPrimary else AppTheme.colors.textSecondary
                            )
                        }

                        // Payouts Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(percent = 50))
                                .background(if (selectedTab == 1) AppTheme.colors.surface else Color.Transparent)
                                .clickable { selectedTab = 1 },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Payouts",
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 1) AppTheme.colors.textPrimary else AppTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }

            // ── 4. EMPTY STATE ───────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 100.dp, bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedTab == 0) "Nothing here yet" else "No payouts yet",
                        fontSize = 14.sp,
                        color = AppTheme.colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // ── 5. WHEN DO I GET PAID EXPLANATION SHEET ──────────────────────────────
    if (showExplanationSheet) {
        ModalBottomSheet(
            onDismissRequest = { showExplanationSheet = false },
            sheetState = sheetState,
            containerColor = AppTheme.colors.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(42.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(AppTheme.colors.border)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "When do I get paid?",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Gradient Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF5B6BFA),
                                    Color(0xFF9855D4),
                                    Color(0xFFD6448D)
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bar Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Available from",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppTheme.colors.primary
                    )
                    Text(
                        text = "30 days after it lands in your wallet",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = AppTheme.colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Explanatory Points
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    PayoutExplanationPoint(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        title = "Straight into your wallet",
                        subtitle = "As soon as you complete your campaign, your cash lands in your Refluenced Wallet. No waiting for the brand, no chasing invoices."
                    )

                    PayoutExplanationPoint(
                        icon = Icons.Filled.Bolt,
                        title = "Yours after 30 days",
                        subtitle = "Cash out to your own private account 30 days later."
                    )

                    PayoutExplanationPoint(
                        icon = Icons.Outlined.Schedule,
                        title = "Here’s how",
                        subtitle = "Choose your payout method."
                    )

                    PayoutExplanationPoint(
                        icon = Icons.Outlined.Shield,
                        title = "We guarantee your payout",
                        subtitle = "Your payout is secured by Refluenced from successful campaign completion. Once it’s in your wallet, it’s yours."
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Footer Note Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AppTheme.colors.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Once you’ve cashed out, it can take a few more business days to land in your account.",
                        fontSize = 13.sp,
                        color = AppTheme.colors.textSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chat With Support Button
                OutlinedButton(
                    onClick = {
                        showExplanationSheet = false
                        onNavigateToContactUs()
                    },
                    shape = RoundedCornerShape(percent = 50),
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AppTheme.colors.surface,
                        contentColor = AppTheme.colors.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Chat with support",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun PayoutExplanationPoint(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(AppTheme.colors.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textPrimary
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = AppTheme.colors.textSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
