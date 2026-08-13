package com.example.refluenceds.ui.screens

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
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.refluenceds.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourReferralsScreen(onBack: () -> Unit) {
    var showReferralSheet by remember { mutableStateOf(false) }

    val textGradientBrush = Brush.horizontalGradient(
        listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))
    )

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Your Referrals",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1D1B36)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1D1B36)
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
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Card 1: Not seeing the person you invited?
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF8F8FD),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Not seeing the person you invited?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1D1B36)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "That means they haven't registered with your code yet.",
                        fontSize = 14.sp,
                        color = Color(0xFF5A5A72),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            HorizontalDivider(color = Color(0xFFF0F0F6))
            Spacer(modifier = Modifier.height(20.dp))

            // Stats Bar Row
            ReferralStatsRow()

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFF0F0F6))
            Spacer(modifier = Modifier.height(48.dp))

            // Empty State Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFEEF0FE),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You don't have any referrals yet",
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
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Text(
                            text = "Refer a Friend and Earn!",
                            color = Color(0xFF4B4FE4),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
                        )
                    }
                }
            }
        }
    }

    // Refer a Friend and Earn! Bottom Sheet
    if (showReferralSheet) {
        ReferAFriendBottomSheet(
            onDismissRequest = { /* Do not dismiss on outside click */ },
            onViewReferralsClick = { showReferralSheet = false }
        )
    }
}

// ── Stats Row Component ───────────────────────────────────────────────────────

@Composable
fun ReferralStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("0", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1D1B36))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Referrals", fontSize = 12.sp, color = Color(0xFF9E9EB0))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("0", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1D1B36))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Completed Referral", fontSize = 12.sp, color = Color(0xFF9E9EB0))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("EUR 0", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1D1B36))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Earned", fontSize = 12.sp, color = Color(0xFF9E9EB0))
        }
    }
}

// ── Refer a Friend Bottom Sheet ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferAFriendBottomSheet(
    onDismissRequest: () -> Unit,
    onViewReferralsClick: () -> Unit
) {
    var referralCode by remember { mutableStateOf("") }
    val textGradientBrush = Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)))

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = Color.White,
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
            HorizontalDivider(color = Color(0xFFF0F0F6))
            Spacer(modifier = Modifier.height(18.dp))

            // Earnings breakdown
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("You earn ", fontSize = 15.sp, color = Color(0xFF1D1B36), fontWeight = FontWeight.Medium)
                Text("20 EUR", fontSize = 16.sp, style = TextStyle(brush = textGradientBrush), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Your friend earns ", fontSize = 15.sp, color = Color(0xFF1D1B36), fontWeight = FontWeight.Medium)
                Text("20 EUR", fontSize = 16.sp, style = TextStyle(brush = textGradientBrush), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your friend must be accepted to a campaign for you both to receive the reward.",
                fontSize = 12.sp,
                color = Color(0xFF7A7A90),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Create your referral code section
            Text(
                text = "Create your referral code",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1D1B36)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = referralCode,
                    onValueChange = { referralCode = it },
                    placeholder = { Text("Create your code here", color = Color.LightGray, fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4B4FE4),
                        unfocusedBorderColor = Color(0xFFE2E2EC)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Save Button
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .width(100.dp)
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF986AF6), Color(0xFFDF6FB0))),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Text("Save", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                    text = "This code can only be edited once.",
                    fontSize = 12.sp,
                    color = Color(0xFF7A7A90)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFF0F0F6))
            Spacer(modifier = Modifier.height(18.dp))

            // Stats row in bottom sheet
            ReferralStatsRow()

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
                    color = Color(0xFF4B4FE4),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF4B4FE4),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
