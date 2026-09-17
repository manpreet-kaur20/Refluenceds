package com.example.refluenceds.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import com.example.refluenceds.ui.components.AppPullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.theme.GradientEnd
import com.example.refluenceds.ui.theme.GradientStart
import com.example.refluenceds.ui.viewmodel.CampaignViewModel
import com.example.refluenceds.utils.SetStatusBarAppearance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    viewModel: CampaignViewModel? = null
) {
    SetStatusBarAppearance(isLightStatusBars = true)

    var selectedTab by remember { mutableStateOf("Chat") }
    val tabs = listOf("Chat", "Notifications")

    val inboxItems by viewModel?.inboxItems?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val inboxSummary by viewModel?.inboxSummary?.collectAsState() ?: remember { mutableStateOf(null) }
    val isLoading by viewModel?.isInboxLoading?.collectAsState() ?: remember { mutableStateOf(false) }

    LaunchedEffect(selectedTab) {
        if (selectedTab == "Chat") {
            viewModel?.fetchConversations()
            viewModel?.fetchUnifiedInbox("chat")
        } else {
            viewModel?.fetchUnifiedInbox("notifications")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Inbox",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )

                    if (selectedTab == "Notifications") {
                        Text(
                            text = "Mark all as seen",
                            color = AppTheme.colors.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                viewModel?.markAllNotificationsAsRead()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Custom Tab Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    tabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        val count = if (tab == "Chat") inboxSummary?.effectiveUnreadChats ?: 0 else inboxSummary?.effectiveUnreadNotifications ?: 0

                        Surface(
                            onClick = { selectedTab = tab },
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .height(40.dp)
                                .wrapContentWidth(),
                            color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.surface,
                            shape = RoundedCornerShape(20.dp),
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border) else null,
                            shadowElevation = if (!isSelected) 0.dp else 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tab,
                                    color = if (isSelected) Color.White else AppTheme.colors.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                if (count > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White else AppTheme.colors.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$count",
                                            color = if (isSelected) AppTheme.colors.primary else Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.divider)
            }
        }
    ) { padding ->
        AppPullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = {
                if (selectedTab == "Chat") {
                    viewModel?.fetchConversations()
                    viewModel?.fetchUnifiedInbox("chat")
                } else {
                    viewModel?.fetchUnifiedInbox("notifications")
                }
                viewModel?.fetchInboxSummary()
            },
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppTheme.colors.primary)
                    }
                } else if (inboxItems.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        items(inboxItems, key = { it.id?.toString() ?: it.hashCode().toString() }) { item ->
                            Surface(
                                onClick = {
                                    item.id?.let { viewModel?.markNotificationAsRead(it) }
                                },
                                modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = AppTheme.colors.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val avatar = item.avatar ?: item.imageUrl
                                if (!avatar.isNullOrBlank()) {
                                    AsyncImage(
                                        model = avatar,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(AppTheme.colors.primary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (selectedTab == "Chat") Icons.Default.ChatBubbleOutline else Icons.Default.NotificationsNone,
                                            contentDescription = null,
                                            tint = AppTheme.colors.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.effectiveTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = AppTheme.colors.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.effectiveMessage,
                                        fontSize = 13.sp,
                                        color = AppTheme.colors.textSecondary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (item.isRead == false) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(AppTheme.colors.primary)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = AppTheme.colors.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        val text = if (selectedTab == "Chat") {
                            "You have no chat messages yet, apply for campaigns and soon you will see messages here."
                        } else {
                            "No new notifications. We will notify you if anything comes up."
                        }

                        Text(
                            text = text,
                            modifier = Modifier.padding(24.dp),
                            style = TextStyle(
                                brush = Brush.horizontalGradient(listOf(GradientStart, GradientEnd)),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp
                            )
                        )
                    }
                }
            }
        }
    }
    }
}
