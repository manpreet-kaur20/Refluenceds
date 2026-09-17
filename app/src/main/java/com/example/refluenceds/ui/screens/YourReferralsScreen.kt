package com.example.refluenceds.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import com.example.refluenceds.ui.components.AppPullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.refluenceds.R
import com.example.refluenceds.data.remote.dto.ReferralStatsDto
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.viewmodel.AuthViewModel
import com.example.refluenceds.utils.SetStatusBarAppearance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourReferralsScreen(
    authViewModel: AuthViewModel? = null,
    onBack: () -> Unit
) {
    SetStatusBarAppearance(isLightStatusBars = true)
    var showReferralSheet by remember { mutableStateOf(false) }

    val textGradientBrush = Brush.horizontalGradient(
        listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))
    )

    LaunchedEffect(Unit) {
        authViewModel?.fetchMyReferralCodeAndStats()
    }

    val stats by authViewModel?.referralStats?.collectAsState() ?: remember { mutableStateOf(null) }
    val userProfile by authViewModel?.userProfile?.collectAsState() ?: remember { mutableStateOf(null) }
    val isLoading by authViewModel?.isLoading?.collectAsState() ?: remember { mutableStateOf(false) }

    val effectiveCode = stats?.getEffectiveCode()?.takeIf { it.isNotBlank() }
        ?: userProfile?.referral?.getEffectiveCode()?.takeIf { it.isNotBlank() }
        ?: userProfile?.referralCode?.takeIf { it.isNotBlank() }
        ?: ""

    val effectiveShareUrl = stats?.getEffectiveShareUrl()?.takeIf { it.isNotBlank() }
        ?: userProfile?.referral?.shareUrl?.takeIf { it.isNotBlank() }
        ?: if (effectiveCode.isNotBlank()) "http://162.241.68.61/refluenced/invite/$effectiveCode" else ""

    val context = LocalContext.current

    Scaffold(
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Your Referrals",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppTheme.colors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppTheme.colors.textPrimary
                        )
                    }
                },
                actions = {
                    // Plus (+) Icon on Top Right with gradient background
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))
                                )
                            )
                            .clickable { showReferralSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Referral",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        }
    ) { innerPadding ->
        AppPullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = {
                authViewModel?.fetchMyReferralCodeAndStats()
                authViewModel?.fetchUserProfile()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Card 1: Referral Code & Link Card if code is available
                if (effectiveCode.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = AppTheme.colors.surface,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Text(
                                text = "Your Referral Code",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AppTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = effectiveCode,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    style = TextStyle(brush = textGradientBrush),
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Referral Code", effectiveCode)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Code copied: $effectiveCode", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AppTheme.colors.surfaceVariant)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = AppTheme.colors.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        val shareText = stats?.getEffectiveShareText()
                                            ?: "Join me on Refluenced! Use my code: $effectiveCode or link: ${effectiveShareUrl.ifEmpty { effectiveCode }}"
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, "Share Referral Link").apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(shareIntent)
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AppTheme.colors.primary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Card 2: Not seeing the person you invited?
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = AppTheme.colors.surface,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Not seeing the person you invited?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = AppTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "That means they haven't registered with your code yet.",
                            fontSize = 14.sp,
                            color = AppTheme.colors.textSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = AppTheme.colors.divider)
                Spacer(modifier = Modifier.height(16.dp))

                // Stats Bar Row
                ReferralStatsRow(stats = stats)

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = AppTheme.colors.divider)
                Spacer(modifier = Modifier.height(28.dp))

                val totalInvited = stats?.getEffectiveTotalInvited() ?: 0

                // Empty / Active State Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = AppTheme.colors.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (totalInvited == 0) "You don't have any referrals yet" else "You have $totalInvited referral(s)",
                            style = TextStyle(brush = textGradientBrush),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Refer a Friend and Earn! Button
                        Surface(
                            onClick = { showReferralSheet = true },
                            shape = RoundedCornerShape(50),
                            color = AppTheme.colors.surface,
                            border = BorderStroke(1.dp, AppTheme.colors.border),
                            shadowElevation = 1.dp
                        ) {
                            Text(
                                text = "Refer a Friend and Earn!",
                                color = AppTheme.colors.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Refer a Friend and Earn! Bottom Sheet
    if (showReferralSheet) {
        ReferAFriendBottomSheet(
            stats = stats,
            defaultCode = effectiveCode,
            defaultShareUrl = effectiveShareUrl,
            onDismissRequest = { showReferralSheet = false },
            onViewReferralsClick = { showReferralSheet = false }
        )
    }
}

// ── Stats Row Component ───────────────────────────────────────────────────────

@Composable
fun ReferralStatsRow(stats: ReferralStatsDto? = null) {
    val totalInvited = stats?.getEffectiveTotalInvited() ?: 0
    val pending = stats?.getEffectivePending() ?: 0
    val completed = stats?.getEffectiveCompleted() ?: 0
    val rewards = stats?.getEffectiveRewards() ?: "0.00 CHF"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$totalInvited", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppTheme.colors.textPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Invited", fontSize = 12.sp, color = AppTheme.colors.textSecondary)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$pending", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppTheme.colors.textPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Pending", fontSize = 12.sp, color = AppTheme.colors.textSecondary)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$completed", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppTheme.colors.textPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Completed", fontSize = 12.sp, color = AppTheme.colors.textSecondary)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(rewards, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppTheme.colors.textPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Earned", fontSize = 12.sp, color = AppTheme.colors.textSecondary)
        }
    }
}

// ── Refer a Friend Bottom Sheet ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferAFriendBottomSheet(
    stats: ReferralStatsDto? = null,
    defaultCode: String = "",
    defaultShareUrl: String = "",
    onDismissRequest: () -> Unit,
    onViewReferralsClick: () -> Unit
) {
    val context = LocalContext.current
    val serverCode = stats?.getEffectiveCode()?.takeIf { it.isNotBlank() } ?: defaultCode
    val shareLink = stats?.getEffectiveShareUrl()?.takeIf { it.isNotBlank() } ?: defaultShareUrl
    var referralCode by remember(serverCode) { mutableStateOf(serverCode) }
    val textGradientBrush = Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)))

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = AppTheme.colors.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header Icon + Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_smile_plus),
                    contentDescription = null,
                    tint = Color(0xFFEC4899),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Refer a Friend and Earn!",
                    style = TextStyle(brush = textGradientBrush),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = AppTheme.colors.divider)
            Spacer(modifier = Modifier.height(18.dp))

            // Earnings breakdown
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("You earn ", fontSize = 15.sp, color = AppTheme.colors.textPrimary, fontWeight = FontWeight.Medium)
                Text(stats?.getEffectiveRewards() ?: "20 EUR", fontSize = 16.sp, style = TextStyle(brush = textGradientBrush), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Your friend earns ", fontSize = 15.sp, color = AppTheme.colors.textPrimary, fontWeight = FontWeight.Medium)
                Text("rewards", fontSize = 16.sp, style = TextStyle(brush = textGradientBrush), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your friend must be accepted to a campaign for you both to receive the reward.",
                fontSize = 12.sp,
                color = AppTheme.colors.textSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Create your referral code section
            Text(
                text = "Your referral code",
                fontSize = 14.sp,
                color = AppTheme.colors.textPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Referral Code Input & Copy / Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = referralCode,
                    onValueChange = { referralCode = it },
                    placeholder = { Text("Your referral code", color = AppTheme.colors.textTertiary, fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AppTheme.colors.textPrimary,
                        unfocusedTextColor = AppTheme.colors.textPrimary,
                        focusedBorderColor = AppTheme.colors.primary,
                        unfocusedBorderColor = AppTheme.colors.border,
                        focusedContainerColor = AppTheme.colors.surfaceVariant,
                        unfocusedContainerColor = AppTheme.colors.surfaceVariant
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Copy / Share Button
                IconButton(
                    onClick = {
                        val shareText = shareLink.ifEmpty { referralCode }
                        if (shareText.isNotEmpty()) {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Referral Code", shareText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppTheme.colors.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Referral Code",
                        tint = AppTheme.colors.primary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val shareText = stats?.getEffectiveShareText()
                            ?: if (shareLink.isNotEmpty()) "Join Refluenced using my referral code $referralCode: $shareLink" else "Join Refluenced using my referral code $referralCode"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share Referral Link").apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF986AF6), Color(0xFFDF6FB0)))
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Share this code or link with friends to earn rewards.",
                    fontSize = 12.sp,
                    color = AppTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = AppTheme.colors.divider)
            Spacer(modifier = Modifier.height(18.dp))

            // Stats row in bottom sheet
            ReferralStatsRow(stats = stats)

            Spacer(modifier = Modifier.height(24.dp))

            // View Referrals -> link button
            Row(
                modifier = Modifier
                    .clickable {
                        onViewReferralsClick()
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View Referrals",
                    color = AppTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
