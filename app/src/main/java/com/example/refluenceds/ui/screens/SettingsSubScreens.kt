package com.example.refluenceds.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.refluenceds.data.remote.dto.EmailPreferencesDto
import com.example.refluenceds.data.remote.dto.PushPreferencesDto
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.viewmodel.AuthViewModel

// ── 1. Language Preference Screen ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePreferenceScreen(
    sessionManager: com.example.refluenceds.data.local.SessionManager? = null,
    onBack: () -> Unit
) {
    val currentLang = sessionManager?.getLanguage() ?: "en"
    var selectedLanguage by remember { mutableStateOf(if (currentLang == "de") "Deutsch (German)" else "English") }

    Scaffold(
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Language Preference", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppTheme.colors.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppTheme.colors.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppTheme.colors.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 16.dp)
        ) {
            LanguageOptionRow(
                language = "English",
                isSelected = selectedLanguage == "English",
                onClick = {
                    selectedLanguage = "English"
                    sessionManager?.setLanguage("en")
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            LanguageOptionRow(
                language = "Deutsch (German)",
                isSelected = selectedLanguage == "Deutsch (German)",
                onClick = {
                    selectedLanguage = "Deutsch (German)"
                    sessionManager?.setLanguage("de")
                }
            )
        }
    }
}

@Composable
fun LanguageOptionRow(language: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = language,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.textPrimary
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ── 2. Change Password Screen ──────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    authViewModel: AuthViewModel? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var retypePassword by remember { mutableStateOf("") }

    var currentVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var retypeVisible by remember { mutableStateOf(false) }

    val isLoading = authViewModel?.isLoading?.collectAsState()?.value ?: false
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isFormValid = currentPassword.isNotEmpty() && newPassword.isNotEmpty() && newPassword == retypePassword

    Scaffold(
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Change password", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppTheme.colors.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppTheme.colors.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppTheme.colors.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            if (errorMessage != null) {
                Text(
                    text = errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Field 1: Current Password
            Text("Your current password", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AppTheme.colors.textPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                placeholder = { Text("Current password", color = AppTheme.colors.textTertiary, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (currentVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { currentVisible = !currentVisible }) {
                        Icon(
                            imageVector = if (currentVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = AppTheme.colors.textSecondary
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppTheme.colors.primary,
                    unfocusedBorderColor = AppTheme.colors.border,
                    focusedTextColor = AppTheme.colors.textPrimary,
                    unfocusedTextColor = AppTheme.colors.textPrimary,
                    focusedContainerColor = AppTheme.colors.inputBackground,
                    unfocusedContainerColor = AppTheme.colors.inputBackground
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Field 2: New Password
            Text("Your new password", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AppTheme.colors.textPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = { Text("New password", color = AppTheme.colors.textTertiary, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { newVisible = !newVisible }) {
                        Icon(
                            imageVector = if (newVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = AppTheme.colors.textSecondary
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppTheme.colors.primary,
                    unfocusedBorderColor = AppTheme.colors.border,
                    focusedTextColor = AppTheme.colors.textPrimary,
                    unfocusedTextColor = AppTheme.colors.textPrimary,
                    focusedContainerColor = AppTheme.colors.inputBackground,
                    unfocusedContainerColor = AppTheme.colors.inputBackground
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Field 3: Retype New Password
            Text("Retype your new password", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AppTheme.colors.textPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = retypePassword,
                onValueChange = { retypePassword = it },
                placeholder = { Text("New password", color = AppTheme.colors.textTertiary, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (retypeVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { retypeVisible = !retypeVisible }) {
                        Icon(
                            imageVector = if (retypeVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = AppTheme.colors.textSecondary
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppTheme.colors.primary,
                    unfocusedBorderColor = AppTheme.colors.border,
                    focusedTextColor = AppTheme.colors.textPrimary,
                    unfocusedTextColor = AppTheme.colors.textPrimary,
                    focusedContainerColor = AppTheme.colors.inputBackground,
                    unfocusedContainerColor = AppTheme.colors.inputBackground
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = {
                    if (isFormValid) {
                        errorMessage = null
                        if (authViewModel != null) {
                            authViewModel.changePassword(
                                currentPass = currentPassword,
                                newPass = newPassword,
                                confirmPass = retypePassword,
                                onSuccess = {
                                    Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                    onBack()
                                },
                                onError = { errorMessage = it }
                            )
                        } else {
                            onBack()
                        }
                    }
                },
                enabled = isFormValid && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.primary,
                    disabledContainerColor = if (AppTheme.isDark) Color(0xFF2B2B3C) else Color(0xFFF1F1F6),
                    disabledContentColor = if (AppTheme.isDark) Color(0xFF6E6E82) else Color(0xFF9E9EB0)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Change password", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                }
            }
        }
    }
}

// ── 3. Push Notifications Screen ───────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushNotificationsScreen(
    authViewModel: AuthViewModel? = null,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        authViewModel?.fetchPushPreferences()
    }

    val serverPrefs = authViewModel?.pushPreferences?.collectAsState()?.value

    var invitations by remember(serverPrefs) { mutableStateOf(serverPrefs?.getInvitations() ?: true) }
    var recommended by remember(serverPrefs) { mutableStateOf(serverPrefs?.getRecommended() ?: true) }
    var ratingReceived by remember(serverPrefs) { mutableStateOf(serverPrefs?.getRatingReceived() ?: true) }
    var badgeReceived by remember(serverPrefs) { mutableStateOf(serverPrefs?.getBadgeReceived() ?: true) }

    fun updateServer() {
        authViewModel?.updatePushPreferences(
            PushPreferencesDto(
                pushInvitationsFromBrands = invitations,
                pushRecommendedCampaigns = recommended,
                pushNewRatingReceived = ratingReceived,
                pushNewBadgeReceived = badgeReceived,
                pushAcceptedToCampaign = true,
                pushContentCreationReminder = true,
                pushChatNotifications = true,
                pushBrandHasSentProduct = true
            )
        )
    }

    Scaffold(
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Push notifications", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppTheme.colors.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppTheme.colors.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppTheme.colors.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            NotificationSwitchRow("Invitations from brands", invitations) {
                invitations = it
                updateServer()
            }
            HorizontalDivider(color = AppTheme.colors.divider)

            NotificationSwitchRow("Recommended campaigns", recommended) {
                recommended = it
                updateServer()
            }
            HorizontalDivider(color = AppTheme.colors.divider)

            NotificationSwitchRow("New rating received", ratingReceived) {
                ratingReceived = it
                updateServer()
            }
            HorizontalDivider(color = AppTheme.colors.divider)

            NotificationSwitchRow("New badge received", badgeReceived) {
                badgeReceived = it
                updateServer()
            }
            HorizontalDivider(color = AppTheme.colors.divider)

            // Mandatory Notifications Section Header (always toggled on, cannot be toggled off)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Mandatory notifications",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.textSecondary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
            HorizontalDivider(color = AppTheme.colors.divider)

            // Mandatory items (always toggled on and disabled / locked)
            NotificationSwitchRow("Accepted to campaign", checked = true, enabled = false, onCheckedChange = {})
            HorizontalDivider(color = AppTheme.colors.divider)

            NotificationSwitchRow("Content creation reminder", checked = true, enabled = false, onCheckedChange = {})
            HorizontalDivider(color = AppTheme.colors.divider)

            NotificationSwitchRow("Chat notifications", checked = true, enabled = false, onCheckedChange = {})
            HorizontalDivider(color = AppTheme.colors.divider)

            NotificationSwitchRow("Brand has sent you the product", checked = true, enabled = false, onCheckedChange = {})
        }
    }
}

// ── 4. Email Notifications Screen ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailNotificationsScreen(
    authViewModel: AuthViewModel? = null,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        authViewModel?.fetchEmailPreferences()
    }

    val serverPrefs = authViewModel?.emailPreferences?.collectAsState()?.value

    var invitations by remember(serverPrefs) { mutableStateOf(serverPrefs?.getInvitations() ?: true) }
    var recommended by remember(serverPrefs) { mutableStateOf(serverPrefs?.getRecommended() ?: true) }
    var ratingReceived by remember(serverPrefs) { mutableStateOf(serverPrefs?.getRatingReceived() ?: true) }
    var accepted by remember(serverPrefs) { mutableStateOf(serverPrefs?.getAccepted() ?: true) }
    var contentReminder by remember(serverPrefs) { mutableStateOf(serverPrefs?.getContentReminder() ?: true) }
    var chatNotifications by remember(serverPrefs) { mutableStateOf(serverPrefs?.getChatNotifications() ?: true) }
    var brandSentProduct by remember(serverPrefs) { mutableStateOf(serverPrefs?.getBrandSentProduct() ?: true) }
    var newsletter by remember(serverPrefs) { mutableStateOf(serverPrefs?.getNewsletter() ?: true) }

    fun updateServer() {
        authViewModel?.updateEmailPreferences(
            EmailPreferencesDto(
                emailInvitationsFromBrands = invitations,
                emailRecommendedCampaigns = recommended,
                emailNewRatingReceived = ratingReceived,
                emailAcceptedToCampaign = accepted,
                emailContentCreationReminder = contentReminder,
                emailChatNotifications = chatNotifications,
                emailBrandHasSentProduct = brandSentProduct,
                emailNewsletter = newsletter
            )
        )
    }

    Scaffold(
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Email notifications", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppTheme.colors.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppTheme.colors.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppTheme.colors.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            NotificationSwitchRow("Invitations from brands", invitations) {
                invitations = it
                updateServer()
            }
            HorizontalDivider(color = AppTheme.colors.divider)

            NotificationSwitchRow("Recommended campaigns", recommended) {
                recommended = it
                updateServer()
            }
            HorizontalDivider(color = AppTheme.colors.divider)

            NotificationSwitchRow("New rating received", ratingReceived) {
                ratingReceived = it
                updateServer()
            }
            HorizontalDivider(color = AppTheme.colors.divider)

            NotificationSwitchRow("Accepted to campaign", accepted) {
                accepted = it
                updateServer()
            }
            HorizontalDivider(color = AppTheme.colors.divider)

            NotificationSwitchRow("Content creation reminder", contentReminder) {
                contentReminder = it
                updateServer()
            }
            HorizontalDivider(color = AppTheme.colors.divider)

            NotificationSwitchRow("Chat notifications", chatNotifications) {
                chatNotifications = it
                updateServer()
            }
            HorizontalDivider(color = AppTheme.colors.divider)

            NotificationSwitchRow("Brand has sent you the product", brandSentProduct) {
                brandSentProduct = it
                updateServer()
            }
            HorizontalDivider(color = AppTheme.colors.divider)

            NotificationSwitchRow("Newsletter", newsletter) {
                newsletter = it
                updateServer()
            }
        }
    }
}

// ── Reusable Notification Switch Row ──────────────────────────────────────────

@Composable
fun NotificationSwitchRow(
    title: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) AppTheme.colors.textPrimary else AppTheme.colors.textPrimary.copy(alpha = 0.65f),
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = if (enabled) onCheckedChange else null,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppTheme.colors.primary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = if (AppTheme.isDark) Color(0xFF38384C) else Color(0xFFD6D7EA),
                disabledCheckedThumbColor = Color.White,
                disabledCheckedTrackColor = AppTheme.colors.primary.copy(alpha = 0.55f),
                disabledUncheckedThumbColor = if (AppTheme.isDark) Color(0xFF4A4A5E) else Color(0xFFB8B9D2),
                disabledUncheckedTrackColor = if (AppTheme.isDark) Color(0xFF282836) else Color(0xFFE2E3F0)
            )
        )
    }
}
