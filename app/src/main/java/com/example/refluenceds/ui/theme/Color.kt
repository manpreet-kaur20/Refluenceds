package com.example.refluenceds.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val PrimaryLight = Color(0xFF4B4FE4)
val SecondaryLight = Color(0xFF625B71)
val TertiaryLight = Color(0xFF7D5260)

val PrimaryDark = Color(0xFF6366F1)
val SecondaryDark = Color(0xFFCCC2DC)
val TertiaryDark = Color(0xFFEFB8C8)

// Expressive Vibrant Fallbacks
val VibrantPink = Color(0xFFFF00CC)
val VibrantBlue = Color(0xFF3333FF)
val ElectricViolet = Color(0xFF8F00FF)

val GradientStart = Color(0xFF5B5BD6)
val GradientEnd = Color(0xFFFF4E7E)
val LightGray = Color(0xFFF5F5F5)
val DarkBlue = Color(0xFF3F3D56)

data class AppColors(
    val background: Color,
    val surface: Color,
    val cardBackground: Color,
    val surfaceVariant: Color,
    val inputBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val border: Color,
    val divider: Color,
    val primary: Color,
    val primaryVariant: Color,
    val navBarBackground: Color,
    val navBarBorder: Color,
    val navBarIconTint: Color,
    val isDark: Boolean
) {
    val primaryGradient: Brush
        get() = Brush.horizontalGradient(
            listOf(GradientStart, GradientEnd)
        )
}

val LightAppColors = AppColors(
    background = Color(0xFFF9FAFC),
    surface = Color.White,
    cardBackground = Color.White,
    surfaceVariant = Color(0xFFF4F5F9),
    inputBackground = Color(0xFFF8F8FC),
    textPrimary = Color(0xFF1D1B36),
    textSecondary = Color(0xFF75758A),
    textTertiary = Color(0xFF8B8B9E),
    border = Color(0xFFEEEEF6),
    divider = Color(0xFFF0F0F6),
    primary = Color(0xFF4B4FE4),
    primaryVariant = Color(0xFF3B3EC4),
    navBarBackground = Color.White,
    navBarBorder = Color.Transparent,
    navBarIconTint = Color(0xFF23213D),
    isDark = false
)

val DarkAppColors = AppColors(
    background = Color(0xFF0F0F14),
    surface = Color(0xFF181822),
    cardBackground = Color(0xFF181822),
    surfaceVariant = Color(0xFF222230),
    inputBackground = Color(0xFF222230),
    textPrimary = Color(0xFFF4F4F8),
    textSecondary = Color(0xFFA0A0B4),
    textTertiary = Color(0xFF707085),
    border = Color(0xFF2B2B3C),
    divider = Color(0xFF262636),
    primary = Color(0xFF6366F1),
    primaryVariant = Color(0xFF7C7EFF),
    navBarBackground = Color(0xFF181822),
    navBarBorder = Color(0xFF2B2B3C),
    navBarIconTint = Color(0xFFE5E5EA),
    isDark = true
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }

object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val isDark: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current.isDark
}

