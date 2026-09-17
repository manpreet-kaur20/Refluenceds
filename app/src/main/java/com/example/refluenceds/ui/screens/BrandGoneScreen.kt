package com.example.refluenceds.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.refluenceds.ui.theme.AppTheme

private val BrandBlue   = Color(0xFF3D5AF1)
private val BrandPurple = Color(0xFF7C3AED)
private val BrandPink   = Color(0xFFEC4899)

@Composable
fun BrandGoneScreen(
    onBack: () -> Unit
) {
    // Floating animation for icon
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue   = 0f,
        targetValue    = -12f,
        animationSpec  = infiniteRepeatable(
            animation  = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    Scaffold(
        modifier            = Modifier.fillMaxSize(),
        containerColor      = AppTheme.colors.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar              = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Go back",
                        tint               = AppTheme.colors.textPrimary
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Gradient Go back button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            Brush.horizontalGradient(listOf(BrandPurple, BrandPink))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick  = onBack,
                        modifier = Modifier.fillMaxSize(),
                        colors   = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
                    ) {
                        Text(
                            text       = "Go back",
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier              = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 40.dp),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {
            // Floating icon ring
            Box(
                modifier = Modifier
                    .size(144.dp)
                    .offset(y = offsetY.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFEEF0FF), Color(0xFFF0E8FF))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                BuildingIcon(
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            // Gradient title text
            Text(
                text       = "This brand is gone",
                fontSize   = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign  = TextAlign.Center,
                style      = LocalTextStyle.current.copy(
                    brush  = Brush.horizontalGradient(
                        listOf(BrandBlue, BrandPurple, BrandPink)
                    )
                )
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text      = "This brand is no longer available or the link isn't working anymore.",
                fontSize  = 14.sp,
                color     = AppTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

// ─── Custom building icon drawn with Canvas ─────────
@Composable
private fun BuildingIcon(modifier: Modifier = Modifier) {
    val gradColors = listOf(Color(0xFF3D5AF1), Color(0xFF7C3AED), Color(0xFFEC4899))
    val brush = Brush.linearGradient(gradColors)

    androidx.compose.foundation.Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = w * 0.044f

        val strokeStyle = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Main center building
        val mainPath = Path().apply {
            moveTo(w * 0.28f, h * 0.28f)
            lineTo(w * 0.28f, h * 0.88f)
            lineTo(w * 0.60f, h * 0.88f)
            lineTo(w * 0.60f, h * 0.28f)
            close()
        }
        drawPath(path = mainPath, brush = brush, style = strokeStyle)

        // Right shorter building
        val rightPath = Path().apply {
            moveTo(w * 0.60f, h * 0.44f)
            lineTo(w * 0.60f, h * 0.88f)
            lineTo(w * 0.82f, h * 0.88f)
            lineTo(w * 0.82f, h * 0.44f)
            close()
        }
        drawPath(path = rightPath, brush = brush, style = strokeStyle)

        // Left smaller building
        val leftPath = Path().apply {
            moveTo(w * 0.09f, h * 0.50f)
            lineTo(w * 0.09f, h * 0.88f)
            lineTo(w * 0.28f, h * 0.88f)
            lineTo(w * 0.28f, h * 0.50f)
            close()
        }
        drawPath(path = leftPath, brush = brush, style = strokeStyle)

        // Windows (filled rounded rectangles)
        val windows = listOf(
            androidx.compose.ui.geometry.Rect(w * 0.33f, h * 0.36f, w * 0.44f, h * 0.46f),
            androidx.compose.ui.geometry.Rect(w * 0.48f, h * 0.36f, w * 0.58f, h * 0.46f),
            androidx.compose.ui.geometry.Rect(w * 0.33f, h * 0.52f, w * 0.44f, h * 0.62f),
            androidx.compose.ui.geometry.Rect(w * 0.48f, h * 0.52f, w * 0.58f, h * 0.62f),
            androidx.compose.ui.geometry.Rect(w * 0.33f, h * 0.68f, w * 0.44f, h * 0.78f),
            androidx.compose.ui.geometry.Rect(w * 0.63f, h * 0.52f, w * 0.72f, h * 0.61f),
            androidx.compose.ui.geometry.Rect(w * 0.63f, h * 0.66f, w * 0.72f, h * 0.75f)
        )
        windows.forEach { rect ->
            drawRoundRect(
                brush = brush,
                topLeft = androidx.compose.ui.geometry.Offset(rect.left, rect.top),
                size = androidx.compose.ui.geometry.Size(rect.width, rect.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
            )
        }
    }
}
