package com.example.refluenceds.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.refluenceds.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf("Photos") }
    var showSaveDialog by remember { mutableStateOf(false) }
    val tabs = listOf("Photos", "Details", "Interests", "Socials")

    // Handle system back press
    BackHandler(enabled = true) {
        showSaveDialog = true
    }

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1D1B36)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { showSaveDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1D1B36)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showSaveDialog = true }) {
                        Text(
                            text = "Save",
                            color = Color(0xFF4B4FE4),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selection Bar
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                items(tabs) { tab ->
                    val isSelected = selectedTab == tab
                    Surface(
                        modifier = Modifier.clickable { selectedTab = tab },
                        shape = RoundedCornerShape(50),
                        color = if (isSelected) Color(0xFF4B4FE4) else Color.White,
                        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE2E2EC))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (tab == "Details") {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = "Warning",
                                    tint = if (isSelected) Color.White else Color(0xFFF59E0B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            if (tab == "Socials") {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_social_instagram),
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else Color.Unspecified,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_social_tiktok),
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else Color.Unspecified,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = tab,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF1D1B36)
                            )
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                "Photos" -> PhotosTabContent()
                "Details" -> DetailsTabContent()
                "Interests" -> InterestsTabContent()
                "Socials" -> SocialsTabContent()
            }
        }
    }

    // "Save changes?" Confirmation Dialog (Matching Screenshot 2)
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(
                    text = "Save changes?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF1D1B36),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = null,
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Don't save Button
                    Button(
                        onClick = {
                            showSaveDialog = false
                            onBack()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEFF1FE),
                            contentColor = Color(0xFF4B4FE4)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Don't save",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Save Button
                    Button(
                        onClick = {
                            showSaveDialog = false
                            onBack()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4B4FE4),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Save",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// ── Tab 1: Photos ─────────────────────────────────────────────────────────────

@Composable
fun PhotosTabContent() {
    val photoSlots = listOf(
        "https://picsum.photos/seed/tomcruise/800/1000",
        null, null, null, null
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(photoSlots.size) { index ->
            val photoUrl = photoSlots[index]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        border = BorderStroke(1.5.dp, Color(0xFF4B4FE4)),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (photoUrl != null) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Camera Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4B4FE4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    // Plus Add Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4B4FE4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Photo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Tab 2: Details ────────────────────────────────────────────────────────────

@Composable
fun DetailsTabContent() {
    var firstName by remember { mutableStateOf("Test") }
    var lastName by remember { mutableStateOf("Ass") }
    var email by remember { mutableStateOf("android@yopmail.com") }
    var birthdate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var bio by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("Germany") }
    var phoneCountry by remember { mutableStateOf("Germany") }
    var mobilePrefix by remember { mutableStateOf("+49") }
    var mobileNumber by remember { mutableStateOf("12 34 56789") }
    var street by remember { mutableStateOf("") }
    var streetNumber by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 32.dp)
    ) {
        // Avatar Photo Header
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = "https://picsum.photos/seed/tomcruise/800/1000",
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color(0xFF4B4FE4), CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // First Name
        DetailsFieldLabel(label = "First name")
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE2E2EC),
                unfocusedBorderColor = Color(0xFFE2E2EC)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Last Name
        DetailsFieldLabel(label = "Last name")
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE2E2EC),
                unfocusedBorderColor = Color(0xFFE2E2EC)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Address
        DetailsFieldLabel(label = "Email address")
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                Text(
                    text = "Verify",
                    color = Color(0xFF4B4FE4),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickable { }
                        .padding(end = 12.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE2E2EC),
                unfocusedBorderColor = Color(0xFFE2E2EC)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Birthdate (Warning Required)
        DetailsFieldLabel(label = "Birthdate", showWarning = true)
        OutlinedTextField(
            value = birthdate,
            onValueChange = { birthdate = it },
            placeholder = { Text("Birthdate", color = Color.LightGray, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = "Calendar",
                    tint = Color(0xFF4B4FE4)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFF59E0B),
                unfocusedBorderColor = Color(0xFFF59E0B)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Gender Dropdown
        DetailsFieldLabel(label = "Gender")
        OutlinedTextField(
            value = gender,
            onValueChange = { gender = it },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF4B4FE4))
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE2E2EC),
                unfocusedBorderColor = Color(0xFFE2E2EC)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bio (Warning Required)
        DetailsFieldLabel(label = "Bio", showWarning = true)
        OutlinedTextField(
            value = bio,
            onValueChange = { if (it.length <= 512) bio = it },
            placeholder = { Text("Tell our brands a little bit about yourself.", color = Color.LightGray, fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFF59E0B),
                unfocusedBorderColor = Color(0xFFF59E0B)
            )
        )
        Text(
            text = "${bio.length}/512",
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Country Dropdown
        DetailsFieldLabel(label = "Country")
        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF4B4FE4))
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE2E2EC),
                unfocusedBorderColor = Color(0xFFE2E2EC)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Phone Number Container (Warning Required)
        DetailsFieldLabel(label = "Phone Number", showWarning = true)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFF59E0B)),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Phone", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B36))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(phoneCountry, fontSize = 14.sp, color = Color(0xFF1D1B36))
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF4B4FE4))
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E2EC), modifier = Modifier.padding(vertical = 10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mobile", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B36))
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(mobilePrefix, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B36))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(mobileNumber, fontSize = 14.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Street (Warning Required)
        DetailsFieldLabel(label = "Street", showWarning = true)
        OutlinedTextField(
            value = street,
            onValueChange = { street = it },
            placeholder = { Text("Street", color = Color.LightGray, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFF59E0B),
                unfocusedBorderColor = Color(0xFFF59E0B)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Street Number (Warning Required)
        DetailsFieldLabel(label = "Street number", showWarning = true)
        OutlinedTextField(
            value = streetNumber,
            onValueChange = { streetNumber = it },
            placeholder = { Text("Street number", color = Color.LightGray, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFF59E0B),
                unfocusedBorderColor = Color(0xFFF59E0B)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // City (Warning Required)
        DetailsFieldLabel(label = "City", showWarning = true)
        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            placeholder = { Text("City", color = Color.LightGray, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFF59E0B),
                unfocusedBorderColor = Color(0xFFF59E0B)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Postal Code (Warning Required)
        DetailsFieldLabel(label = "Postal code", showWarning = true)
        OutlinedTextField(
            value = postalCode,
            onValueChange = { postalCode = it },
            placeholder = { Text("Postal code", color = Color.LightGray, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFF59E0B),
                unfocusedBorderColor = Color(0xFFF59E0B)
            ),
            singleLine = true
        )
    }
}

@Composable
fun DetailsFieldLabel(label: String, showWarning: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        if (showWarning) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = "Required",
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF1D1B36)
        )
    }
}

// ── Tab 3: Interests ──────────────────────────────────────────────────────────

data class InterestCardItem(val title: String, val imageUrl: String, val isSelected: Boolean)

@Composable
fun InterestsTabContent() {
    val interests = remember {
        mutableStateListOf(
            InterestCardItem("Beauty", "https://picsum.photos/seed/beauty/500/600", true),
            InterestCardItem("Fashion", "https://picsum.photos/seed/fashion/500/600", true),
            InterestCardItem("Gastronomy", "https://picsum.photos/seed/food/500/600", true),
            InterestCardItem("Food & Drink", "https://picsum.photos/seed/drink/500/600", true),
            InterestCardItem("Travel", "https://picsum.photos/seed/travel/500/600", false),
            InterestCardItem("Sports", "https://picsum.photos/seed/sports/500/600", false),
            InterestCardItem("Home & Living", "https://picsum.photos/seed/home/500/600", false),
            InterestCardItem("Technology", "https://picsum.photos/seed/tech/500/600", false)
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(interests.size) { index ->
            val item = interests[index]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        border = BorderStroke(
                            1.5.dp,
                            if (item.isSelected) Color(0xFF4B4FE4) else Color(0xFFE2E2EC)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        interests[index] = item.copy(isSelected = !item.isSelected)
                    }
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.15f))
                )

                // Check Circle at Top Right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (item.isSelected) Color(0xFF4B4FE4) else Color.White.copy(alpha = 0.8f))
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Title Pill at Bottom Left
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

// ── Tab 4: Socials ────────────────────────────────────────────────────────────

@Composable
fun SocialsTabContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar Photo Header
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = "https://picsum.photos/seed/tomcruise/800/1000",
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color(0xFF4B4FE4), CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Instagram Card
        SocialConnectItemRow(
            title = "Instagram",
            iconRes = R.drawable.ic_social_instagram
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TikTok Card
        SocialConnectItemRow(
            title = "TikTok",
            iconRes = R.drawable.ic_social_tiktok
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Support Contact Link
        Text(
            text = "Any issues? Contact our support.",
            color = Color(0xFF4B4FE4),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { }
                .align(Alignment.Start)
        )
    }
}

@Composable
fun SocialConnectItemRow(title: String, iconRes: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1D1B36)
                )
            }

            Text(
                text = "Connect ⇄",
                color = Color(0xFF4B4FE4),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF8F8FC)
        ) {}
    }
}
