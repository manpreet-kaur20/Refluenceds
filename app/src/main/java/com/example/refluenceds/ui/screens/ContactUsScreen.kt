package com.example.refluenceds.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.refluenceds.R
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.viewmodel.AuthViewModel
import com.example.refluenceds.utils.SetStatusBarAppearance

enum class ContactViewMode {
    Home,
    Messages,
    Chat
}

data class ChatMessageItem(
    val id: String,
    val sender: String, // "Lia" or "User"
    val text: String,
    val timestamp: String
)

private const val LIA_AVATAR_URL = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150"
private const val COLLEAGUE_AVATAR_URL = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen(
    authViewModel: AuthViewModel? = null,
    onBack: () -> Unit
) {
    SetStatusBarAppearance(isLightStatusBars = !AppTheme.isDark)
    val userProfile by authViewModel?.userProfile?.collectAsState() ?: remember { mutableStateOf(null) }
    val firstName = userProfile?.firstName?.takeIf { it.isNotBlank() }
        ?: userProfile?.fullName?.split(" ")?.firstOrNull()?.takeIf { it.isNotBlank() }
        ?: "Tom"

    var currentView by remember { mutableStateOf(ContactViewMode.Home) }
    var navigationSource by remember { mutableStateOf(ContactViewMode.Home) }
    var showAiAgentSheet by remember { mutableStateOf(false) }

    BackHandler(enabled = currentView != ContactViewMode.Home) {
        if (currentView == ContactViewMode.Chat) {
            currentView = navigationSource
        } else if (currentView == ContactViewMode.Messages) {
            currentView = ContactViewMode.Home
        }
    }

    val chatMessages = remember {
        mutableStateListOf(
            ChatMessageItem(
                id = "1",
                sender = "Lia",
                text = "Hello, hello! \uD83D\uDC8C\n\nI am Lia and support you with all questions about campaigns, applications, and the app ✨",
                timestamp = "Just now"
            ),
            ChatMessageItem(
                id = "2",
                sender = "Lia",
                text = "If I need support from my team for your request, processing may take 1–2 business days.\n\nMaybe these links will help you in the meantime:\n• Content-Synchronisation: Instagram | TikTok\n• Ad Code: Instagram | TikTok\n• Creator Broadcast Channel (Insights & Updates)",
                timestamp = "Just now"
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        AnimatedContent(
            targetState = currentView,
            transitionSpec = {
                if (targetState.ordinal < initialState.ordinal) {
                    slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                } else {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                }
            },
            label = "ContactUsViewAnimation"
        )
 { mode ->
            when (mode) {
                ContactViewMode.Home -> {
                    ContactHomeView(
                        userName = firstName,
                        onClose = onBack,
                        onOpenMessages = {
                            navigationSource = ContactViewMode.Home
                            currentView = ContactViewMode.Messages
                        },
                        onOpenChat = {
                            navigationSource = ContactViewMode.Home
                            currentView = ContactViewMode.Chat
                        },
                        onShowAiInfo = { showAiAgentSheet = true }
                    )
                }
                ContactViewMode.Messages -> {
                    ContactMessagesView(
                        onBack = { currentView = ContactViewMode.Home },
                        onOpenChat = {
                            navigationSource = ContactViewMode.Messages
                            currentView = ContactViewMode.Chat
                        }
                    )
                }
                ContactViewMode.Chat -> {
                    ContactChatView(
                        messages = chatMessages,
                        onSendMessage = { text ->
                            chatMessages.add(
                                ChatMessageItem(
                                    id = System.currentTimeMillis().toString(),
                                    sender = "User",
                                    text = text,
                                    timestamp = "Just now"
                                )
                            )
                        },
                        onBack = { currentView = navigationSource },
                        onOpenAiInfo = { showAiAgentSheet = true }
                    )
                }
            }
        }

        if (showAiAgentSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAiAgentSheet = false },
                containerColor = AppTheme.colors.surface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 20.dp)
                            .size(width = 36.dp, height = 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(AppTheme.colors.textPrimary)
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 44.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Overlapping Agent Avatars
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy((-12).dp)
                    ) {
                        AsyncImage(
                            model = COLLEAGUE_AVATAR_URL,
                            contentDescription = "Support Team",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, AppTheme.colors.surface, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        AsyncImage(
                            model = LIA_AVATAR_URL,
                            contentDescription = "Lia",
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, AppTheme.colors.surface, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "AI Agent answers instantly",
                        color = AppTheme.colors.textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Ask for the team if needed",
                        color = AppTheme.colors.textSecondary,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. CONTACT HOME VIEW (Screenshot 1)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ContactHomeView(
    userName: String,
    onClose: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenChat: () -> Unit,
    onShowAiInfo: () -> Unit
) {
    val isDark = AppTheme.isDark
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDark) {
                    Brush.verticalGradient(
                        0.0f to Color(0xFF1A1A2E),
                        1.0f to Color(0xFF0F0F1A)
                    )
                } else {
                    Brush.verticalGradient(
                        0.0f to Color(0xFFDAC4FC),
                        0.28f to Color(0xFFEFE8FC),
                        0.65f to AppTheme.colors.background,
                        1.0f to AppTheme.colors.background
                    )
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            // Top Bar: Logo on Left, Close on Right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Icon
                AsyncImage(
                    model = R.drawable.app_icon,
                    contentDescription = "Refluenced",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                // Close Button 'X'
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = AppTheme.colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Greeting Header
            Text(
                text = "Hi $userName \uD83D\uDC9C",
                color = AppTheme.colors.textSecondary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Let's catch up!",
                color = AppTheme.colors.textPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 1. Messages Card with Recent Message
            Surface(
                onClick = onOpenMessages,
                shape = RoundedCornerShape(18.dp),
                color = AppTheme.colors.surface,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    // "Messages" Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Messages",
                            color = AppTheme.colors.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Messages",
                            tint = AppTheme.colors.textPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recent message card (standalone rounded card)
            Surface(
                onClick = onOpenChat,
                shape = RoundedCornerShape(18.dp),
                color = AppTheme.colors.surface,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Recent message",
                        color = AppTheme.colors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = LIA_AVATAR_URL,
                            contentDescription = "Lia",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Lia",
                                    color = AppTheme.colors.textPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "1m",
                                    color = AppTheme.colors.textTertiary,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Lia: Hey there! How can I help you today...",
                                color = AppTheme.colors.textSecondary,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Ask a Question Card
            Surface(
                onClick = {
                    onShowAiInfo()
                    onOpenChat()
                },
                shape = RoundedCornerShape(18.dp),
                color = AppTheme.colors.surface,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Ask a question",
                        color = AppTheme.colors.textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Overlapping Agent Avatars + Question badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy((-8).dp)
                        ) {
                            AsyncImage(
                                model = LIA_AVATAR_URL,
                                contentDescription = "Lia",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, AppTheme.colors.surface, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            AsyncImage(
                                model = COLLEAGUE_AVATAR_URL,
                                contentDescription = "Support Agent",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, AppTheme.colors.surface, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Question icon circle
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(AppTheme.colors.textPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "?",
                                color = AppTheme.colors.background,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. CONTACT MESSAGES VIEW (Screenshot 2)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ContactMessagesView(
    onBack: () -> Unit,
    onOpenChat: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar with back arrow and "Messages" title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AppTheme.colors.textPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Messages",
                    color = AppTheme.colors.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.divider)

            // Conversation Thread Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenChat() }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = LIA_AVATAR_URL,
                    contentDescription = "Lia",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lia",
                            color = AppTheme.colors.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "1m",
                            color = AppTheme.colors.textSecondary,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Lia: Hey there! How can I help you today? \uD83D\uDE0A",
                        color = AppTheme.colors.textSecondary,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = AppTheme.colors.divider,
                modifier = Modifier.padding(start = 78.dp)
            )
        }

        // Floating Action Button (Edit / Pencil)
        FloatingActionButton(
            onClick = onOpenChat,
            shape = CircleShape,
            containerColor = AppTheme.colors.surface,
            contentColor = AppTheme.colors.textPrimary,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(24.dp)
                .size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "New Message",
                tint = AppTheme.colors.textPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. CONTACT CHAT CONVERSATION VIEW (Screenshot 3 & 4)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ContactChatView(
    messages: List<ChatMessageItem>,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    onOpenAiInfo: () -> Unit
) {
    SetStatusBarAppearance(isLightStatusBars = !AppTheme.isDark)
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .statusBarsPadding()
            .imePadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenAiInfo() }
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = AppTheme.colors.textPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Lia profile pic + name
            AsyncImage(
                model = LIA_AVATAR_URL,
                contentDescription = "Lia",
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Lia",
                    color = AppTheme.colors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "The team can also help",
                    color = AppTheme.colors.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.divider)

        // Chat Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isLia = msg.sender == "Lia"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isLia) Alignment.Start else Alignment.End
                ) {
                    if (isLia) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = AppTheme.colors.surfaceVariant,
                            modifier = Modifier.widthIn(max = 330.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                FormattedSupportMessage(text = msg.text)
                            }
                        }
                    } else {
                        Text(
                            text = msg.text,
                            color = AppTheme.colors.textPrimary,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (isLia) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lia • 1m",
                            color = AppTheme.colors.textSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }
        }

        // Bottom Message Composer Bar matching Screenshot 3
        Surface(
            shape = RoundedCornerShape(26.dp),

            color = AppTheme.colors.surface,

            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = AppTheme.colors.surface,
                border = BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Top Input Text
                    BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        textStyle = TextStyle(
                            color = AppTheme.colors.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        cursorBrush = SolidColor(AppTheme.colors.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 24.dp, max = 100.dp),
                        decorationBox = { innerTextField ->
                            if (inputText.isEmpty()) {
                                Text(
                                    text = "Message...",
                                    color = AppTheme.colors.textTertiary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                            innerTextField()
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bottom Row: Action Icons on Left, Send Button on Right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left action icons (Paperclip, GIF, Mic)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Paperclip
                            Icon(
                                imageVector = Icons.Outlined.AttachFile,
                                contentDescription = "Attach",
                                tint = AppTheme.colors.textSecondary,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { }
                            )

                            // GIF badge
                            Box(
                                modifier = Modifier
                                    .border(1.2.dp, AppTheme.colors.textSecondary, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                    .clickable { },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "GIF",
                                    color = AppTheme.colors.textSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            // Mic
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = "Voice",
                                tint = AppTheme.colors.textSecondary,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { }
                            )
                        }

                        // Send Button (circular)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (inputText.isNotBlank()) AppTheme.colors.primary else AppTheme.colors.surfaceVariant
                                )
                                .clickable {
                                    if (inputText.isNotBlank()) {
                                        onSendMessage(inputText.trim())
                                        inputText = ""
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Send",
                                tint = if (inputText.isNotBlank()) Color.White else AppTheme.colors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// FORMATTED SUPPORT MESSAGE (STYLED TEXT WITH UNDERLINED LINKS)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FormattedSupportMessage(
    text: String
) {
    val textColor = AppTheme.colors.textPrimary
    val annotatedString = remember(text, textColor) {
        buildAnnotatedString {
            val fullText = text
            append(fullText)

            val linkStyle = SpanStyle(
                color = textColor,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Medium
            )

            fun markLink(target: String) {
                var startIndex = 0
                while (startIndex < fullText.length) {
                    val idx = fullText.indexOf(target, startIndex)
                    if (idx == -1) break
                    val endIdx = idx + target.length
                    addStyle(linkStyle, idx, endIdx)
                    startIndex = endIdx
                }
            }

            markLink("Instagram")
            markLink("TikTok")
            markLink("Creator Broadcast Channel (Insights & Updates)")
        }
    }

    Text(
        text = annotatedString,
        color = AppTheme.colors.textPrimary,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
}

