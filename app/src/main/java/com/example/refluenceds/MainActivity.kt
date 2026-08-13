package com.example.refluenceds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.refluenceds.data.local.SessionManager
import com.example.refluenceds.ui.components.OfflineBanner
import com.example.refluenceds.ui.components.OfflineBottomSheet
import com.example.refluenceds.ui.navigation.MainNavigation
import com.example.refluenceds.ui.theme.RefluencedsTheme
import com.example.refluenceds.utils.LocaleManager
import com.example.refluenceds.utils.NetworkMonitor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by sessionManager.themeState.collectAsState()
            val langCode by sessionManager.languageState.collectAsState()
            val isOnline by networkMonitor.isOnline.collectAsState(initial = true)

            val currentContext = LocalContext.current
            val localizedContext = LocaleManager.applyLocale(currentContext, langCode)

            val isDarkTheme = when (themeMode) {
                "Light" -> false
                "Dark" -> true
                else -> isSystemInDarkTheme()
            }

            CompositionLocalProvider(LocalContext provides localizedContext) {
                RefluencedsTheme(darkTheme = isDarkTheme) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        OfflineBanner(isOffline = !isOnline)
                        MainNavigation(sessionManager = sessionManager)
                    }
                    OfflineBottomSheet(isOffline = !isOnline)
                }
            }
        }
    }
}
