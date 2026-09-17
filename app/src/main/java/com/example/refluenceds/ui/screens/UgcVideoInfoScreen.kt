package com.example.refluenceds.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.refluenceds.R
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.utils.SetStatusBarAppearance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UgcVideoInfoScreen(
    onBack: () -> Unit = {},
    onApply: () -> Unit = {}
) {
    SetStatusBarAppearance(isLightStatusBars = true)
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = AppTheme.colors.background,
        topBar = {
            Surface(
                color = AppTheme.colors.surface,
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

                    // Title with gradient/colored text
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)) {
                                append("What is a ")
                            }
                            withStyle(SpanStyle(color = Color(0xFFEC4899), fontWeight = FontWeight.Bold)) {
                                append("UGC video?")
                            }
                        },
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = AppTheme.colors.surface,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Primary Gradient Button: "Got it. Let's apply"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF6366F1),
                                        Color(0xFFEC4899)
                                    )
                                )
                            )
                            .clickable { onApply() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Got it. Let's apply",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Secondary Button: "Apply later"
                    Surface(
                        onClick = onBack,
                        shape = RoundedCornerShape(percent = 50),
                        color = AppTheme.colors.surface,
                        border = BorderStroke(1.dp, AppTheme.colors.border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Apply later",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppTheme.colors.primary
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Subtitle with highlighted "product"
            Text(
                text = buildAnnotatedString {
                    append("A short video about a ")
                    withStyle(SpanStyle(color = Color(0xFF059669), fontWeight = FontWeight.Bold)) {
                        append("product")
                    }
                    append(" you love... not\nabout you.")
                },
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                color = AppTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ── VIDEO PREVIEW CARD ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF1E2028))
            ) {
                // Creator Background Image
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark translucent overlay for contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                )

                // Center Title & Play Button
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Creator Basics",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier.size(72.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    Text(
                        text = "What is UGC?",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Bottom Left & Right Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Refluenced badge & title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_story_refluenced),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "What UGC really is...",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Duration Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "00:52",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ── COMPARISON CARDS (Product video vs Intro video) ─────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Product Video (Green)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (AppTheme.isDark) Color(0xFF0E2E1E) else Color(0xFFEFFDF5),
                    border = BorderStroke(1.dp, if (AppTheme.isDark) Color(0xFF1B4D2E) else Color(0xFFD1FAE5)),
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Product video",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Show the product. Talk about why you love it.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = if (AppTheme.isDark) Color(0xFFD1FAE5) else Color(0xFF374151)
                        )
                    }
                }

                // Intro Video (Red)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (AppTheme.isDark) Color(0xFF321414) else Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, if (AppTheme.isDark) Color(0xFF5A2222) else Color(0xFFFEE2E2)),
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Intro video",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Don't introduce yourself. We want the product.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = if (AppTheme.isDark) Color(0xFFFEE2E2) else Color(0xFF374151)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
