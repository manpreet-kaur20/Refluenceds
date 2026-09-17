package com.example.refluenceds.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6366F1),
    secondary = Color(0xFFCCC2DC),
    tertiary = Color(0xFFEFB8C8),
    background = Color(0xFF0F0F14),
    surface = Color(0xFF181822),
    onBackground = Color(0xFFF4F4F8),
    onSurface = Color(0xFFF4F4F8),
    surfaceVariant = Color(0xFF222230),
    onSurfaceVariant = Color(0xFFA0A0B4),
    outline = Color(0xFF2B2B3C),
    outlineVariant = Color(0xFF262636)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4B4FE4),
    secondary = Color(0xFF625B71),
    tertiary = Color(0xFF7D5260),
    background = Color(0xFFF9FAFC),
    surface = Color.White,
    onBackground = Color(0xFF1D1B36),
    onSurface = Color(0xFF1D1B36),
    surfaceVariant = Color(0xFFF4F5F9),
    onSurfaceVariant = Color(0xFF75758A),
    outline = Color(0xFFEEEEF6),
    outlineVariant = Color(0xFFF0F0F6)
)

@Composable
fun RefluencedsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) DarkAppColors else LightAppColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalAppColors provides appColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}