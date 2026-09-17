package com.example.refluenceds.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.refluenceds.ui.theme.AppTheme

private fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

/**
 * Dynamically controls the system status bar icon appearance.
 *
 * @param isLightStatusBars
 *   - `true`: Status bar background is light/white -> Status bar icons are rendered in DARK (black/grey).
 *   - `false`: Status bar background is dark -> Status bar icons are rendered in LIGHT (white).
 */
@Composable
fun SetStatusBarAppearance(isLightStatusBars: Boolean) {
    val isAppDark = AppTheme.isDark
    val finalIsLightStatusBars = if (isAppDark) false else isLightStatusBars

    val context = LocalContext.current
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity() ?: context.findActivity()
            val window = activity?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.isAppearanceLightStatusBars = finalIsLightStatusBars
            }
        }
    }
}

