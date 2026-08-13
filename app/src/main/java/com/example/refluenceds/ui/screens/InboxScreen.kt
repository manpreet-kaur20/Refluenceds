package com.example.refluenceds.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.refluenceds.ui.theme.GradientEnd
import com.example.refluenceds.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen() {
    var selectedTab by remember { mutableStateOf("Chat") }
    val tabs = listOf("Chat", "Notifications")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
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
                        color = Color(0xFF1A1A2E)
                    )

                    if (selectedTab == "Notifications") {
                        Text(
                            text = "Mark all as seen",
                            color = Color(0xFF4B4FE4),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
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
                        Surface(
                            onClick = { selectedTab = tab },
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .height(40.dp)
                                .wrapContentWidth(),
                            color = if (isSelected) Color(0xFF4B4FE4) else Color.White,
                            shape = RoundedCornerShape(20.dp),
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)) else null,
                            shadowElevation = if (!isSelected) 0.dp else 2.dp
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 28.dp)
                            ) {
                                Text(
                                    text = tab,
                                    color = if (isSelected) Color.White else Color(0xFF4B4FE4),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F1F1))
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF1F1FF),
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
