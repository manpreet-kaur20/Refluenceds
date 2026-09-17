package com.example.refluenceds.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.refluenceds.ui.theme.AppTheme

@Composable
fun ConnectInstagramScreen(
    onConnect: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    val context = LocalContext.current
    Scaffold(
        containerColor = AppTheme.colors.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Circular Instagram Icon Badge
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFF3E8FF), Color(0xFFFCE7F3))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_social_instagram),
                    contentDescription = "Instagram",
                    modifier = Modifier.size(44.dp),
                    tint = Color(0xFFE1306C)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Gradient Title: "Connect your Instagram"
            Text(
                text = "Connect your\nInstagram",
                style = TextStyle(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF818CF8), Color(0xFFEC4899))
                    ),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Minimum Admission Standards Table Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (AppTheme.isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, AppTheme.colors.border)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Header Title
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Our Minimum Admission Standards",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                    }

                    HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.divider)

                    // Subheaders
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👥", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Follower count",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppTheme.colors.textSecondary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💬", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Engagement rate",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppTheme.colors.textSecondary
                            )
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.divider)

                    // Data Rows
                    val standards = listOf(
                        "1,000" to "10%",
                        "2,000" to "7.5%",
                        "5,000" to "5%",
                        "10,000" to "3%"
                    )

                    standards.forEachIndexed { index, pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 28.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pair.first,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppTheme.colors.textPrimary
                            )
                            Text(
                                text = pair.second,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppTheme.colors.textPrimary
                            )
                        }

                        if (index < standards.size - 1) {
                            HorizontalDivider(
                                thickness = 0.8.dp,
                                color = AppTheme.colors.divider.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // "Connect" Gradient Button
            Button(
                onClick = {
                    val instagramLoginUrl = "https://www.instagram.com/accounts/login/"
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramLoginUrl)).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
                    }
                    onConnect()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF7C5CF6), Color(0xFFEC4899))
                            ),
                            shape = RoundedCornerShape(50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Connect",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // "Close" Outlined Button
            OutlinedButton(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.5.dp, Color(0xFFE0E7FF)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
            ) {
                Text(
                    text = "Close",
                    color = Color(0xFF6366F1),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
