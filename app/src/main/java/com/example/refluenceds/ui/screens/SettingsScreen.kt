package com.example.refluenceds.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.viewmodel.AuthViewModel
import com.example.refluenceds.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel? = null,
    sessionManager: com.example.refluenceds.data.local.SessionManager? = null,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToLanguage: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToPushNotifications: () -> Unit = {},
    onNavigateToEmailNotifications: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToContactUs: () -> Unit = {}
) {
    var selectedTheme by remember { mutableStateOf(sessionManager?.getTheme() ?: "System") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppTheme.colors.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Theme Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Theme",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = AppTheme.colors.textPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Theme Segmented Control
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    color = AppTheme.colors.inputBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ThemeOptionPill(
                            label = "Light",
                            icon = Icons.Outlined.WbSunny,
                            isSelected = selectedTheme == "Light",
                            onClick = {
                                selectedTheme = "Light"
                                sessionManager?.setTheme("Light")
                                authViewModel?.updateTheme("Light")
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionPill(
                            label = "Dark",
                            icon = Icons.Outlined.NightsStay,
                            isSelected = selectedTheme == "Dark",
                            onClick = {
                                selectedTheme = "Dark"
                                sessionManager?.setTheme("Dark")
                                authViewModel?.updateTheme("Dark")
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionPill(
                            label = "System",
                            icon = Icons.Outlined.CropFree,
                            isSelected = selectedTheme == "System",
                            onClick = {
                                selectedTheme = "System"
                                sessionManager?.setTheme("System")
                                authViewModel?.updateTheme("System")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = AppTheme.colors.divider)

            // Account Section
            SettingsSectionHeader(icon = Icons.Outlined.Lightbulb, title = "Account")
            SettingsClickableRow(title = "Edit Profile", onClick = { onNavigateToEditProfile() })
            HorizontalDivider(color = AppTheme.colors.divider, modifier = Modifier.padding(horizontal = 20.dp))
            SettingsClickableRow(title = "Language Preference", trailingText = "English", onClick = { onNavigateToLanguage() })

            HorizontalDivider(color = AppTheme.colors.divider)

            // Sign in & Security Section
            SettingsSectionHeader(icon = Icons.Outlined.Lock, title = "Sign in & Security")
            SettingsClickableRow(title = "Change password", onClick = { onNavigateToChangePassword() })

            HorizontalDivider(color = AppTheme.colors.divider)

            // Communications Section
            SettingsSectionHeader(icon = Icons.Outlined.ChatBubbleOutline, title = "Communications")
            SettingsClickableRow(title = "Push notifications", onClick = { onNavigateToPushNotifications() })
            HorizontalDivider(color = AppTheme.colors.divider, modifier = Modifier.padding(horizontal = 20.dp))
            SettingsClickableRow(title = "Email notifications", onClick = { onNavigateToEmailNotifications() })

            HorizontalDivider(color = AppTheme.colors.divider)

            // Log out Section
            Spacer(modifier = Modifier.height(16.dp))
            SettingsClickableRow(
                title = "Log out",
                titleColor = AppTheme.colors.textPrimary,
                titleWeight = FontWeight.Bold,
                onClick = { showLogoutDialog = true }
            )

            HorizontalDivider(color = AppTheme.colors.divider)
            Spacer(modifier = Modifier.height(16.dp))

            // Footer Links Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "FAQ",
                    fontSize = 14.sp,
                    color = AppTheme.colors.primary,
                    modifier = Modifier.clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FAQ_WEB_URL)).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
                )
                Text(
                    text = "Privacy Policy",
                    fontSize = 14.sp,
                    color = AppTheme.colors.primary,
                    modifier = Modifier.clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PRIVACY_WEB_URL)).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
                )
                Text(
                    text = "Terms and Conditions",
                    fontSize = 14.sp,
                    color = AppTheme.colors.primary,
                    modifier = Modifier.clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TERMS_WEB_URL)).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
                )
                Text(
                    text = "Contact us",
                    fontSize = 14.sp,
                    color = AppTheme.colors.primary,
                    modifier = Modifier.clickable { onNavigateToContactUs() }
                )
                Text(
                    text = "Delete account",
                    fontSize = 14.sp,
                    color = Color(0xFFFA5252),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { showDeleteDialog = true }
                )
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text("Log Out", fontWeight = FontWeight.Bold, color = AppTheme.colors.textPrimary)
            },
            text = {
                Text("Are you sure you want to log out?", color = AppTheme.colors.textSecondary, fontSize = 14.sp)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Log Out", color = AppTheme.colors.primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = AppTheme.colors.textSecondary)
                }
            },
            containerColor = AppTheme.colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Delete Account Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Delete Account", fontWeight = FontWeight.Bold, color = Color(0xFFFA5252))
            },
            text = {
                Text(
                    "Are you sure you want to delete your account? All your data will be permanently cleared.",
                    color = Color(0xFF5A5A72),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        if (authViewModel != null) {
                            authViewModel.deleteAccount(onSuccess = onLogout)
                        } else {
                            onLogout()
                        }
                    }
                ) {
                    Text("Delete", color = Color(0xFFFA5252), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// ── Theme Option Pill Component ───────────────────────────────────────────────

@Composable
fun ThemeOptionPill(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) AppTheme.colors.primary else Color.Transparent,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else AppTheme.colors.textSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else AppTheme.colors.textSecondary
            )
        }
    }
}

// ── Settings Section Header Component ─────────────────────────────────────────

@Composable
fun SettingsSectionHeader(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFC03A82),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colors.textSecondary
        )
    }
}

// ── Settings Clickable Row Component ──────────────────────────────────────────

@Composable
fun SettingsClickableRow(
    title: String,
    trailingText: String? = null,
    titleColor: Color = AppTheme.colors.textPrimary,
    titleWeight: FontWeight = FontWeight.Bold,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = titleWeight,
            color = titleColor
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    fontSize = 13.sp,
                    color = AppTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
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
