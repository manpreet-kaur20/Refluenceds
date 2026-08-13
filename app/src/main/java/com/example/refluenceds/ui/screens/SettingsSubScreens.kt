package com.example.refluenceds.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── 1. Language Preference Screen ─────────────────────────────────────────────

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
        containerColor = Color.White,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Language Preference", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1D1B36)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1D1B36))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
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
            color = Color(0xFF1D1B36)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color(0xFF4B4FE4),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ── 2. Change Password Screen ──────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(onBack: () -> Unit) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var retypePassword by remember { mutableStateOf("") }

    var currentVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var retypeVisible by remember { mutableStateOf(false) }

    val isFormValid = currentPassword.isNotEmpty() && newPassword.isNotEmpty() && newPassword == retypePassword

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Change password", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1D1B36)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1D1B36))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Field 1: Current Password
            Text("Your current password", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1D1B36))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                placeholder = { Text("Current password", color = Color.LightGray, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (currentVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { currentVisible = !currentVisible }) {
                        Icon(
                            imageVector = if (currentVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE2E2EC),
                    unfocusedBorderColor = Color(0xFFE2E2EC)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Field 2: New Password
            Text("Your new password", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1D1B36))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = { Text("New password", color = Color.LightGray, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { newVisible = !newVisible }) {
                        Icon(
                            imageVector = if (newVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE2E2EC),
                    unfocusedBorderColor = Color(0xFFE2E2EC)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Field 3: Retype New Password
            Text("Retype your new password", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1D1B36))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = retypePassword,
                onValueChange = { retypePassword = it },
                placeholder = { Text("New password", color = Color.LightGray, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (retypeVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { retypeVisible = !retypeVisible }) {
                        Icon(
                            imageVector = if (retypeVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE2E2EC),
                    unfocusedBorderColor = Color(0xFFE2E2EC)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = { if (isFormValid) onBack() },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4B4FE4),
                    disabledContainerColor = Color(0xFFF1F1F6),
                    disabledContentColor = Color(0xFF9E9EB0)
                )
            ) {
                Text("Change password", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

// ── 3. Push Notifications Screen ───────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushNotificationsScreen(onBack: () -> Unit) {
    var invitations by remember { mutableStateOf(true) }
    var recommended by remember { mutableStateOf(true) }
    var ratingReceived by remember { mutableStateOf(true) }
    var badgeReceived by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Push notifications", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1D1B36)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1D1B36))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            NotificationSwitchRow("Invitations from brands", invitations) { invitations = it }
            HorizontalDivider(color = Color(0xFFF6F6FA))

            NotificationSwitchRow("Recommended campaigns", recommended) { recommended = it }
            HorizontalDivider(color = Color(0xFFF6F6FA))

            NotificationSwitchRow("New rating received", ratingReceived) { ratingReceived = it }
            HorizontalDivider(color = Color(0xFFF6F6FA))

            NotificationSwitchRow("New badge received", badgeReceived) { badgeReceived = it }
            HorizontalDivider(color = Color(0xFFF6F6FA))

            // Mandatory Notifications Section Header
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Mandatory notifications",
                fontSize = 12.sp,
                color = Color(0xFF9E9EB0),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
            HorizontalDivider(color = Color(0xFFF6F6FA))

            // Mandatory items (disabled state)
            NotificationSwitchRow("Accepted to campaign", checked = false, enabled = false, onCheckedChange = {})
            HorizontalDivider(color = Color(0xFFF6F6FA))

            NotificationSwitchRow("Content creation reminder", checked = false, enabled = false, onCheckedChange = {})
            HorizontalDivider(color = Color(0xFFF6F6FA))

            NotificationSwitchRow("Chat notifications", checked = false, enabled = false, onCheckedChange = {})
            HorizontalDivider(color = Color(0xFFF6F6FA))

            NotificationSwitchRow("Brand has sent you the product", checked = false, enabled = false, onCheckedChange = {})
        }
    }
}

// ── 4. Email Notifications Screen ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailNotificationsScreen(onBack: () -> Unit) {
    var invitations by remember { mutableStateOf(true) }
    var recommended by remember { mutableStateOf(true) }
    var ratingReceived by remember { mutableStateOf(true) }
    var accepted by remember { mutableStateOf(true) }
    var contentReminder by remember { mutableStateOf(true) }
    var chatNotifications by remember { mutableStateOf(true) }
    var brandSentProduct by remember { mutableStateOf(true) }
    var newsletter by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Email notifications", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1D1B36)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1D1B36))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            NotificationSwitchRow("Invitations from brands", invitations) { invitations = it }
            HorizontalDivider(color = Color(0xFFF6F6FA))

            NotificationSwitchRow("Recommended campaigns", recommended) { recommended = it }
            HorizontalDivider(color = Color(0xFFF6F6FA))

            NotificationSwitchRow("New rating received", ratingReceived) { ratingReceived = it }
            HorizontalDivider(color = Color(0xFFF6F6FA))

            NotificationSwitchRow("Accepted to campaign", accepted) { accepted = it }
            HorizontalDivider(color = Color(0xFFF6F6FA))

            NotificationSwitchRow("Content creation reminder", contentReminder) { contentReminder = it }
            HorizontalDivider(color = Color(0xFFF6F6FA))

            NotificationSwitchRow("Chat notifications", chatNotifications) { chatNotifications = it }
            HorizontalDivider(color = Color(0xFFF6F6FA))

            NotificationSwitchRow("Brand has sent you the product", brandSentProduct) { brandSentProduct = it }
            HorizontalDivider(color = Color(0xFFF6F6FA))

            NotificationSwitchRow("Newsletter", newsletter) { newsletter = it }
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
            color = if (enabled) Color(0xFF1D1B36) else Color(0xFF1D1B36).copy(alpha = 0.6f),
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = if (enabled) onCheckedChange else null,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF5E65F4),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD6D7EA),
                disabledUncheckedThumbColor = Color(0xFFB8B9D2),
                disabledUncheckedTrackColor = Color(0xFFE2E3F0)
            )
        )
    }
}
