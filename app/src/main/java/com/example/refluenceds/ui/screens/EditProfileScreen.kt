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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.refluenceds.R
import com.example.refluenceds.ui.theme.AppTheme

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.platform.LocalContext
import com.example.refluenceds.data.remote.dto.*
import com.example.refluenceds.ui.viewmodel.AuthViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    authViewModel: AuthViewModel? = null,
    onBack: () -> Unit,
    onNavigateToContactUs: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Photos") }
    var showSaveDialog by remember { mutableStateOf(false) }
    val tabs = listOf("Photos", "Details", "Interests", "Socials")

    LaunchedEffect(Unit) {
        authViewModel?.fetchUserProfile()
        authViewModel?.fetchCountries()
    }

    val userProfile = authViewModel?.userProfile?.collectAsState()?.value
    val countriesList = authViewModel?.countriesList?.collectAsState()?.value ?: emptyList()
    val isLoading = authViewModel?.isLoading?.collectAsState()?.value ?: false

    var selectedAvatarUri by remember { mutableStateOf<Uri?>(null) }
    var selectedAvatarBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showAvatarSourceSheet by remember { mutableStateOf(false) }
    var showCountrySheet by remember { mutableStateOf(false) }
    var showPhoneCountrySheet by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedAvatarUri = it
            selectedAvatarBitmap = null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            selectedAvatarBitmap = it
            selectedAvatarUri = null
        }
    }

    // Editable profile fields
    var firstName by remember { mutableStateOf(userProfile?.firstName.orEmpty()) }
    var lastName by remember { mutableStateOf(userProfile?.lastName.orEmpty()) }
    var email by remember { mutableStateOf(userProfile?.email.orEmpty()) }
    var birthdate by remember { mutableStateOf(userProfile?.dob.orEmpty()) }
    var gender by remember { mutableStateOf(userProfile?.gender ?: "Male") }
    var bio by remember { mutableStateOf(userProfile?.bio.orEmpty()) }
    var country by remember { mutableStateOf(userProfile?.country?.name ?: "Germany") }
    var selectedCountryId by remember { mutableStateOf<Int?>(userProfile?.country?.id ?: userProfile?.countryId?.let { (it as? Number)?.toInt() ?: it.toString().toIntOrNull() } ?: 82) }
    var phoneCountry by remember { mutableStateOf(userProfile?.phoneCountry ?: userProfile?.country?.name ?: "Germany") }
    var mobilePrefix by remember { mutableStateOf(userProfile?.phoneCode ?: userProfile?.mobilePrefix ?: userProfile?.country?.phonecode?.let { if (it.startsWith("+")) it else "+$it" } ?: "+49") }
    var mobileNumber by remember { mutableStateOf(userProfile?.phone ?: userProfile?.phoneNumber.orEmpty()) }
    var street by remember { mutableStateOf(userProfile?.street.orEmpty()) }
    var streetNumber by remember { mutableStateOf(userProfile?.streetNumber.orEmpty()) }
    var city by remember { mutableStateOf(userProfile?.city.orEmpty()) }
    var postalCode by remember { mutableStateOf(userProfile?.postalCode.orEmpty()) }

    val hasDetailsError = remember(birthdate, bio, mobileNumber, street, streetNumber, city, postalCode) {
        birthdate.isEmpty() || bio.isEmpty() || mobileNumber.isEmpty() ||
        street.isEmpty() || streetNumber.isEmpty() || city.isEmpty() || postalCode.isEmpty()
    }

    LaunchedEffect(userProfile) {
        userProfile?.let { u ->
            if (firstName.isEmpty()) firstName = u.firstName.orEmpty()
            if (lastName.isEmpty()) lastName = u.lastName.orEmpty()
            if (email.isEmpty()) email = u.email.orEmpty()
            if (birthdate.isEmpty()) birthdate = u.dob.orEmpty()
            if (gender.isEmpty()) gender = u.gender ?: "Male"
            if (bio.isEmpty()) bio = u.bio.orEmpty()
            if (u.country?.name != null) country = u.country.name
            if (u.country?.id != null) selectedCountryId = u.country.id
            else if (u.countryId != null) selectedCountryId = (u.countryId as? Number)?.toInt() ?: u.countryId.toString().toIntOrNull()
            if (u.phoneCountry != null) phoneCountry = u.phoneCountry
            if (u.phoneCode != null) mobilePrefix = u.phoneCode
            else if (u.mobilePrefix != null) mobilePrefix = u.mobilePrefix
            if (mobileNumber.isEmpty()) mobileNumber = u.phone ?: u.phoneNumber.orEmpty()
            if (street.isEmpty()) street = u.street.orEmpty()
            if (streetNumber.isEmpty()) streetNumber = u.streetNumber.orEmpty()
            if (city.isEmpty()) city = u.city.orEmpty()
            if (postalCode.isEmpty()) postalCode = u.postalCode.orEmpty()
        }
    }

    fun performSave() {
        val fields = mutableMapOf<String, String>()
        fields["first_name"] = firstName
        fields["last_name"] = lastName
        fields["dob"] = birthdate
        fields["gender"] = gender
        fields["bio"] = bio
        fields["country_id"] = (selectedCountryId ?: 82).toString()
        fields["country"] = country
        fields["phone_code"] = mobilePrefix
        fields["mobile_prefix"] = mobilePrefix
        fields["phone"] = mobileNumber
        fields["phone_number"] = mobileNumber
        fields["phone_country"] = phoneCountry
        fields["street"] = street
        fields["street_number"] = streetNumber
        fields["city"] = city
        fields["postal_code"] = postalCode

        val avatarFile = when {
            selectedAvatarUri != null -> {
                try {
                    val temp = File.createTempFile("avatar_", ".jpg", context.cacheDir)
                    context.contentResolver.openInputStream(selectedAvatarUri!!)?.use { input ->
                        temp.outputStream().use { output -> input.copyTo(output) }
                    }
                    if (temp.length() > 0) temp else null
                } catch (_: Exception) { null }
            }
            selectedAvatarBitmap != null -> {
                try {
                    val temp = File.createTempFile("avatar_", ".jpg", context.cacheDir)
                    temp.outputStream().use { output ->
                        selectedAvatarBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, output)
                    }
                    if (temp.length() > 0) temp else null
                } catch (_: Exception) { null }
            }
            else -> null
        }

        if (authViewModel != null) {
            authViewModel.updateProfileDetails(
                fields = fields,
                profilePictureFile = avatarFile,
                onSuccess = {
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    onBack()
                },
                onError = { err ->
                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            onBack()
        }
    }

    // Handle system back press
    BackHandler(enabled = true) {
        showSaveDialog = true
    }

    Scaffold(
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppTheme.colors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { showSaveDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppTheme.colors.textPrimary
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showSaveDialog = true }) {
                        Text(
                            text = "Save",
                            color = AppTheme.colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppTheme.colors.surface
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
                        color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.surface,
                        border = if (isSelected) null else BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (tab == "Details" && hasDetailsError) {
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
                                    tint = if (isSelected) Color.White else AppTheme.colors.textPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_social_tiktok),
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else AppTheme.colors.textPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = tab,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else AppTheme.colors.textPrimary
                            )
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                "Photos" -> PhotosTabContent(userProfile = userProfile)
                "Details" -> DetailsTabContent(
                    userProfile = userProfile,
                    selectedAvatar = selectedAvatarBitmap ?: selectedAvatarUri,
                    onChangePhotoClick = { showAvatarSourceSheet = true },
                    firstName = firstName, onFirstNameChange = { firstName = it },
                    lastName = lastName, onLastNameChange = { lastName = it },
                    email = email, onEmailChange = { email = it },
                    birthdate = birthdate, onBirthdateChange = { birthdate = it },
                    gender = gender, onGenderChange = { gender = it },
                    bio = bio, onBioChange = { bio = it },
                    country = country,
                    onCountryClick = { showCountrySheet = true },
                    phoneCountry = phoneCountry,
                    mobilePrefix = mobilePrefix,
                    onPhoneCountryClick = { showPhoneCountrySheet = true },
                    mobileNumber = mobileNumber, onMobileNumberChange = { mobileNumber = it },
                    street = street, onStreetChange = { street = it },
                    streetNumber = streetNumber, onStreetNumberChange = { streetNumber = it },
                    city = city, onCityChange = { city = it },
                    postalCode = postalCode, onPostalCodeChange = { postalCode = it }
                )
                "Interests" -> InterestsTabContent(authViewModel = authViewModel)
                "Socials" -> SocialsTabContent(
                    userProfile = userProfile,
                    onNavigateToContactUs = onNavigateToContactUs
                )
            }
        }
    }

    // Country Picker Bottom Sheet
    if (showCountrySheet) {
        EditProfileCountryPickerSheet(
            countries = countriesList,
            selectedCountry = country,
            onCountrySelected = { selected ->
                country = selected.name
                selectedCountryId = selected.id
                showCountrySheet = false
            },
            onDismiss = { showCountrySheet = false }
        )
    }

    // Phone Country / Dial Code Picker Bottom Sheet
    if (showPhoneCountrySheet) {
        EditProfilePhoneCountryPickerSheet(
            countries = countriesList,
            selectedCountry = phoneCountry,
            onCountrySelected = { selected ->
                phoneCountry = selected.name
                val dialCode = selected.phonecode?.let { if (it.startsWith("+")) it else "+$it" } ?: "+49"
                mobilePrefix = dialCode
                showPhoneCountrySheet = false
            },
            onDismiss = { showPhoneCountrySheet = false }
        )
    }

    // Avatar Source Picker Bottom Sheet (Camera / Gallery)
    if (showAvatarSourceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAvatarSourceSheet = false },
            containerColor = AppTheme.colors.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Select Profile Picture",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(18.dp))

                Surface(
                    onClick = {
                        showAvatarSourceSheet = false
                        cameraLauncher.launch(null)
                    },
                    shape = RoundedCornerShape(14.dp),
                    color = AppTheme.colors.surfaceVariant,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (AppTheme.isDark) Color(0xFF2E2E48) else Color(0xFFEEF0FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, null, tint = AppTheme.colors.primary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text("Take Photo with Camera", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = AppTheme.colors.textPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    onClick = {
                        showAvatarSourceSheet = false
                        galleryLauncher.launch("image/*")
                    },
                    shape = RoundedCornerShape(14.dp),
                    color = AppTheme.colors.surfaceVariant,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (AppTheme.isDark) Color(0xFF2E2E48) else Color(0xFFEEF0FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhotoLibrary, null, tint = AppTheme.colors.primary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text("Choose from Gallery", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = AppTheme.colors.textPrimary)
                    }
                }
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
                    color = AppTheme.colors.textPrimary,
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
                            containerColor = AppTheme.colors.surfaceVariant,
                            contentColor = AppTheme.colors.primary
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
                            performSave()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colors.primary,
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
            containerColor = AppTheme.colors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// ── Tab 1: Photos ─────────────────────────────────────────────────────────────

@Composable
fun PhotosTabContent(userProfile: UserDto? = null) {
    val serverPhotos = userProfile?.photos?.mapNotNull { it.getEffectiveUrl() } ?: emptyList()
    val photoSlots = remember(serverPhotos, userProfile) {
        val list = MutableList<String?>(5) { null }
        serverPhotos.forEachIndexed { i, url ->
            if (i < 5) list[i] = url
        }
        if (serverPhotos.isEmpty() && !userProfile?.profilePicture.isNullOrEmpty()) {
            list[0] = userProfile?.profilePicture.normalizeImageUrl()
        }
        list
    }

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
                        border = BorderStroke(1.5.dp, AppTheme.colors.primary),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .background(AppTheme.colors.surface),
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
                            .background(AppTheme.colors.primary),
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
                            .background(AppTheme.colors.primary),
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
fun DetailsTabContent(
    userProfile: UserDto? = null,
    selectedAvatar: Any? = null,
    onChangePhotoClick: () -> Unit = {},
    firstName: String, onFirstNameChange: (String) -> Unit,
    lastName: String, onLastNameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    birthdate: String, onBirthdateChange: (String) -> Unit,
    gender: String, onGenderChange: (String) -> Unit,
    bio: String, onBioChange: (String) -> Unit,
    country: String,
    onCountryClick: () -> Unit = {},
    phoneCountry: String,
    mobilePrefix: String,
    onPhoneCountryClick: () -> Unit = {},
    mobileNumber: String, onMobileNumberChange: (String) -> Unit,
    street: String, onStreetChange: (String) -> Unit,
    streetNumber: String, onStreetNumberChange: (String) -> Unit,
    city: String, onCityChange: (String) -> Unit,
    postalCode: String, onPostalCodeChange: (String) -> Unit
) {
    val avatarModel = selectedAvatar
        ?: userProfile?.avatar
        ?: userProfile?.photos?.firstOrNull()?.getEffectiveUrl()
        ?: userProfile?.profilePicture.normalizeImageUrl()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 32.dp)
    ) {
        // Avatar Photo Header with Camera Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(126.dp)
                    .clickable { onChangePhotoClick() }
            ) {
                if (avatarModel != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(avatarModel)
                            .crossfade(true)
                            .error(R.drawable.ic_broken_image)
                            .fallback(R.drawable.ic_broken_image)
                            .build(),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .border(3.dp, AppTheme.colors.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(AppTheme.colors.surfaceVariant)
                            .border(3.dp, AppTheme.colors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar Placeholder",
                            tint = AppTheme.colors.primary,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                // Edit Camera Badge at bottom-right of avatar
                Surface(
                    onClick = onChangePhotoClick,
                    shape = CircleShape,
                    color = AppTheme.colors.primary,
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change Profile Picture",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // First Name
        DetailsFieldLabel(label = "First name")
        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppTheme.colors.primary,
                unfocusedBorderColor = AppTheme.colors.border,
                focusedTextColor = AppTheme.colors.textPrimary,
                unfocusedTextColor = AppTheme.colors.textPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Last Name
        DetailsFieldLabel(label = "Last name")
        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppTheme.colors.primary,
                unfocusedBorderColor = AppTheme.colors.border,
                focusedTextColor = AppTheme.colors.textPrimary,
                unfocusedTextColor = AppTheme.colors.textPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Address
        DetailsFieldLabel(label = "Email address")
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppTheme.colors.primary,
                unfocusedBorderColor = AppTheme.colors.border,
                focusedTextColor = AppTheme.colors.textPrimary,
                unfocusedTextColor = AppTheme.colors.textPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Birthdate (Warning Required)
        DetailsFieldLabel(label = "Birthdate", showWarning = birthdate.isEmpty())
        OutlinedTextField(
            value = birthdate,
            onValueChange = onBirthdateChange,
            placeholder = { Text("YYYY-MM-DD", color = AppTheme.colors.textSecondary, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = "Calendar",
                    tint = AppTheme.colors.primary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (birthdate.isEmpty()) Color(0xFFF59E0B) else AppTheme.colors.primary,
                unfocusedBorderColor = if (birthdate.isEmpty()) Color(0xFFF59E0B) else AppTheme.colors.border,
                focusedTextColor = AppTheme.colors.textPrimary,
                unfocusedTextColor = AppTheme.colors.textPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Gender Dropdown
        DetailsFieldLabel(label = "Gender")
        OutlinedTextField(
            value = gender,
            onValueChange = onGenderChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = AppTheme.colors.primary)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppTheme.colors.primary,
                unfocusedBorderColor = AppTheme.colors.border,
                focusedTextColor = AppTheme.colors.textPrimary,
                unfocusedTextColor = AppTheme.colors.textPrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bio (Warning Required)
        DetailsFieldLabel(label = "Bio", showWarning = bio.isEmpty())
        OutlinedTextField(
            value = bio,
            onValueChange = { if (it.length <= 512) onBioChange(it) },
            placeholder = { Text("Tell our brands a little bit about yourself.", color = AppTheme.colors.textSecondary, fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (bio.isEmpty()) Color(0xFFF59E0B) else AppTheme.colors.primary,
                unfocusedBorderColor = if (bio.isEmpty()) Color(0xFFF59E0B) else AppTheme.colors.border,
                focusedTextColor = AppTheme.colors.textPrimary,
                unfocusedTextColor = AppTheme.colors.textPrimary
            )
        )
        Text(
            text = "${bio.length}/512",
            fontSize = 11.sp,
            color = AppTheme.colors.textSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Country Dropdown
        DetailsFieldLabel(label = "Country")
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCountryClick() },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AppTheme.colors.border),
            color = AppTheme.colors.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = country.ifEmpty { "Select Country" },
                    fontSize = 15.sp,
                    color = if (country.isEmpty()) AppTheme.colors.textSecondary else AppTheme.colors.textPrimary
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Country",
                    tint = AppTheme.colors.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Phone Number Container
        DetailsFieldLabel(label = "Phone Number", showWarning = mobileNumber.isEmpty())
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, if (mobileNumber.isEmpty()) Color(0xFFF59E0B) else AppTheme.colors.border),
            color = AppTheme.colors.surface
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPhoneCountryClick() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Phone", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.textPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(phoneCountry.ifEmpty { "Select Country" }, fontSize = 14.sp, color = AppTheme.colors.textPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Phone Country", tint = AppTheme.colors.primary)
                    }
                }

                HorizontalDivider(color = AppTheme.colors.divider, modifier = Modifier.padding(vertical = 10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mobile", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.textPrimary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Surface(
                        modifier = Modifier.clickable { onPhoneCountryClick() },
                        color = AppTheme.colors.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = mobilePrefix.ifEmpty { "+49" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { input -> onMobileNumberChange(input.filter { it.isDigit() }) },
                        placeholder = { Text("12 34 56789", color = AppTheme.colors.textSecondary) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = AppTheme.colors.textPrimary,
                            unfocusedTextColor = AppTheme.colors.textPrimary
                        ),
                        singleLine = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Street
        DetailsFieldLabel(label = "Street", showWarning = street.isEmpty())
        OutlinedTextField(
            value = street,
            onValueChange = onStreetChange,
            placeholder = { Text("Street", color = AppTheme.colors.textSecondary, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (street.isEmpty()) Color(0xFFF59E0B) else AppTheme.colors.primary,
                unfocusedBorderColor = if (street.isEmpty()) Color(0xFFF59E0B) else AppTheme.colors.border,
                focusedTextColor = AppTheme.colors.textPrimary,
                unfocusedTextColor = AppTheme.colors.textPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Street Number
        DetailsFieldLabel(label = "Street number", showWarning = streetNumber.isEmpty())
        OutlinedTextField(
            value = streetNumber,
            onValueChange = { input -> onStreetNumberChange(input.filter { it.isDigit() }) },
            placeholder = { Text("Street number", color = AppTheme.colors.textSecondary, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (streetNumber.isEmpty()) Color(0xFFF59E0B) else AppTheme.colors.primary,
                unfocusedBorderColor = if (streetNumber.isEmpty()) Color(0xFFF59E0B) else AppTheme.colors.border,
                focusedTextColor = AppTheme.colors.textPrimary,
                unfocusedTextColor = AppTheme.colors.textPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // City
        DetailsFieldLabel(label = "City", showWarning = city.isEmpty())
        OutlinedTextField(
            value = city,
            onValueChange = onCityChange,
            placeholder = { Text("City", color = AppTheme.colors.textSecondary, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (city.isEmpty()) Color(0xFFF59E0B) else AppTheme.colors.primary,
                unfocusedBorderColor = if (city.isEmpty()) Color(0xFFF59E0B) else AppTheme.colors.border,
                focusedTextColor = AppTheme.colors.textPrimary,
                unfocusedTextColor = AppTheme.colors.textPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Postal Code
        DetailsFieldLabel(label = "Postal code", showWarning = postalCode.isEmpty())
        OutlinedTextField(
            value = postalCode,
            onValueChange = { input -> onPostalCodeChange(input.filter { it.isDigit() }) },
            placeholder = { Text("Postal code", color = AppTheme.colors.textSecondary, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (postalCode.isEmpty()) Color(0xFFF59E0B) else AppTheme.colors.primary,
                unfocusedBorderColor = if (postalCode.isEmpty()) Color(0xFFF59E0B) else AppTheme.colors.border,
                focusedTextColor = AppTheme.colors.textPrimary,
                unfocusedTextColor = AppTheme.colors.textPrimary
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
            color = AppTheme.colors.textPrimary
        )
    }
}

// ── Tab 3: Interests ──────────────────────────────────────────────────────────

data class InterestCardItem(val id: Int = 0, val title: String, val imageUrl: String, val isSelected: Boolean)

@Composable
fun InterestsTabContent(authViewModel: AuthViewModel? = null) {
    val serverIndustries = authViewModel?.industriesList?.collectAsState()?.value ?: emptyList()
    val userProfile = authViewModel?.userProfile?.collectAsState()?.value
    val userIndustryIds = userProfile?.industries?.mapNotNull { it.id } ?: emptyList()

    val fallbackInterests = remember {
        listOf(
            InterestCardItem(1, "Beauty", "", true),
            InterestCardItem(2, "Fashion", "", true),
            InterestCardItem(3, "Gastronomy", "", true),
            InterestCardItem(4, "Food & Drink", "", true),
            InterestCardItem(5, "Travel", "", false),
            InterestCardItem(6, "Sports", "", false),
            InterestCardItem(7, "Home & Living", "", false),
            InterestCardItem(8, "Technology", "", false)
        )
    }

    val displayList = remember(serverIndustries, userIndustryIds) {
        if (serverIndustries.isNotEmpty()) {
            serverIndustries.map { ind ->
                InterestCardItem(
                    id = ind.id,
                    title = ind.name,
                    imageUrl = ind.icon ?: ind.imageUrl ?: ind.image ?: "",
                    isSelected = userIndustryIds.contains(ind.id)
                )
            }.toMutableStateList()
        } else {
            fallbackInterests.toMutableStateList()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(displayList.size) { index ->
            val item = displayList[index]
            val fallbackRes = com.example.refluenceds.utils.IndustryUtils.getIndustryDrawable(item.title)
            val hasValidUrl = !item.imageUrl.isNullOrBlank() &&
                (item.imageUrl.startsWith("http://") || item.imageUrl.startsWith("https://"))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        border = BorderStroke(
                            1.5.dp,
                            if (item.isSelected) AppTheme.colors.primary else AppTheme.colors.border
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        val updated = item.copy(isSelected = !item.isSelected)
                        displayList[index] = updated
                        val selectedIds = displayList.filter { it.isSelected }.map { it.id }
                        authViewModel?.updateProfileIndustries(selectedIds)
                    }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(if (hasValidUrl) item.imageUrl else fallbackRes)
                        .crossfade(true)
                        .placeholder(fallbackRes)
                        .error(fallbackRes)
                        .fallback(fallbackRes)
                        .build(),
                    contentDescription = item.title,
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
                        .background(if (item.isSelected) AppTheme.colors.primary else Color.White.copy(alpha = 0.8f))
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
fun SocialsTabContent(
    userProfile: UserDto? = null,
    onNavigateToContactUs: () -> Unit = {}
) {
    val avatarUrl = userProfile?.avatar
        ?: userProfile?.photos?.firstOrNull()?.getEffectiveUrl()
        ?: userProfile?.profilePicture.normalizeImageUrl()

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
            if (avatarUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUrl)
                        .crossfade(true)
                        .error(R.drawable.ic_broken_image)
                        .fallback(R.drawable.ic_broken_image)
                        .build(),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(3.dp, AppTheme.colors.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(AppTheme.colors.surfaceVariant)
                        .border(3.dp, AppTheme.colors.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar Placeholder",
                        tint = AppTheme.colors.primary,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Instagram Card
        SocialConnectItemRow(
            title = "Instagram",
            handle = userProfile?.instagram,
            iconRes = R.drawable.ic_social_instagram
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TikTok Card
        SocialConnectItemRow(
            title = "TikTok",
            handle = userProfile?.tiktok,
            iconRes = R.drawable.ic_social_tiktok
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Support Contact Link
        Text(
            text = "Any issues? Contact our support.",
            color = AppTheme.colors.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onNavigateToContactUs() }
                .align(Alignment.Start)
        )
    }
}

@Composable
fun SocialConnectItemRow(title: String, handle: String? = null, iconRes: Int) {
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
                    tint = AppTheme.colors.textPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = AppTheme.colors.textPrimary
                )
            }

            Text(
                text = if (handle.isNullOrEmpty()) "Connect ⇄" else "Connected ✓",
                color = if (handle.isNullOrEmpty()) AppTheme.colors.primary else Color(0xFF10B981),
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
            color = AppTheme.colors.surfaceVariant
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
                Text(
                    text = if (!handle.isNullOrEmpty()) "@$handle" else "No account connected",
                    color = if (!handle.isNullOrEmpty()) AppTheme.colors.textPrimary else AppTheme.colors.textSecondary,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileCountryPickerSheet(
    countries: List<CountryDto>,
    selectedCountry: String,
    onCountrySelected: (CountryDto) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val effectiveList = remember(countries) {
        if (countries.isNotEmpty()) countries else listOf(
            CountryDto(82, "Germany", "DE", "DE", "DEU", "49", "Berlin", "EUR", "€", "🇩🇪"),
            CountryDto(205, "Switzerland", "CH", "CH", "CHE", "41", "Bern", "CHF", "CHF", "🇨🇭"),
            CountryDto(75, "France", "FR", "FR", "FRA", "33", "Paris", "EUR", "€", "🇫🇷"),
            CountryDto(107, "Italy", "IT", "IT", "ITA", "39", "Rome", "EUR", "€", "🇮🇹"),
            CountryDto(14, "Austria", "AT", "AT", "AUT", "43", "Vienna", "EUR", "€", "🇦🇹"),
            CountryDto(124, "Liechtenstein", "LI", "LI", "LIE", "423", "Vaduz", "CHF", "CHF", "🇱🇮"),
            CountryDto(226, "United States", "US", "US", "USA", "1", "Washington", "USD", "$", "🇺🇸"),
            CountryDto(228, "United Kingdom", "GB", "GB", "GBR", "44", "London", "GBP", "£", "🇬🇧")
        )
    }

    val filteredList = remember(effectiveList, searchQuery) {
        if (searchQuery.isBlank()) effectiveList else {
            effectiveList.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                (it.code?.contains(searchQuery, ignoreCase = true) == true) ||
                (it.iso2?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppTheme.colors.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Select Country",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search country...", color = AppTheme.colors.textSecondary) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = AppTheme.colors.textSecondary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = AppTheme.colors.textSecondary)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppTheme.colors.primary,
                    unfocusedBorderColor = AppTheme.colors.border,
                    focusedTextColor = AppTheme.colors.textPrimary,
                    unfocusedTextColor = AppTheme.colors.textPrimary,
                    focusedContainerColor = AppTheme.colors.surfaceVariant,
                    unfocusedContainerColor = AppTheme.colors.surfaceVariant
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredList.isEmpty()) {
                    item {
                        Text(
                            text = "No countries found",
                            color = AppTheme.colors.textSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(filteredList) { countryItem ->
                        val isSelected = selectedCountry.equals(countryItem.name, ignoreCase = true)
                        val emoji = countryItem.emoji?.takeIf { it.isNotBlank() } ?: ""
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCountrySelected(countryItem) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isSelected) AppTheme.colors.primary else AppTheme.colors.border),
                            color = if (isSelected) AppTheme.colors.surfaceVariant else AppTheme.colors.surface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (emoji.isNotEmpty()) "$emoji  ${countryItem.name}" else countryItem.name,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textPrimary
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = AppTheme.colors.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfilePhoneCountryPickerSheet(
    countries: List<CountryDto>,
    selectedCountry: String,
    onCountrySelected: (CountryDto) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val effectiveList = remember(countries) {
        if (countries.isNotEmpty()) countries else listOf(
            CountryDto(82, "Germany", "DE", "DE", "DEU", "49", "Berlin", "EUR", "€", "🇩🇪"),
            CountryDto(205, "Switzerland", "CH", "CH", "CHE", "41", "Bern", "CHF", "CHF", "🇨🇭"),
            CountryDto(75, "France", "FR", "FR", "FRA", "33", "Paris", "EUR", "€", "🇫🇷"),
            CountryDto(107, "Italy", "IT", "IT", "ITA", "39", "Rome", "EUR", "€", "🇮🇹"),
            CountryDto(14, "Austria", "AT", "AT", "AUT", "43", "Vienna", "EUR", "€", "🇦🇹"),
            CountryDto(124, "Liechtenstein", "LI", "LI", "LIE", "423", "Vaduz", "CHF", "CHF", "🇱🇮"),
            CountryDto(226, "United States", "US", "US", "USA", "1", "Washington", "USD", "$", "🇺🇸"),
            CountryDto(228, "United Kingdom", "GB", "GB", "GBR", "44", "London", "GBP", "£", "🇬🇧")
        )
    }

    val filteredList = remember(effectiveList, searchQuery) {
        if (searchQuery.isBlank()) effectiveList else {
            effectiveList.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                (it.phonecode?.contains(searchQuery, ignoreCase = true) == true) ||
                (it.code?.contains(searchQuery, ignoreCase = true) == true) ||
                (it.iso2?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppTheme.colors.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Select Country Code",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search country or dial code...", color = AppTheme.colors.textSecondary) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = AppTheme.colors.textSecondary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = AppTheme.colors.textSecondary)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppTheme.colors.primary,
                    unfocusedBorderColor = AppTheme.colors.border,
                    focusedTextColor = AppTheme.colors.textPrimary,
                    unfocusedTextColor = AppTheme.colors.textPrimary,
                    focusedContainerColor = AppTheme.colors.surfaceVariant,
                    unfocusedContainerColor = AppTheme.colors.surfaceVariant
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredList.isEmpty()) {
                    item {
                        Text(
                            text = "No countries found",
                            color = AppTheme.colors.textSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(filteredList) { countryItem ->
                        val isSelected = selectedCountry.equals(countryItem.name, ignoreCase = true)
                        val emoji = countryItem.emoji?.takeIf { it.isNotBlank() } ?: ""
                        val dialCode = countryItem.phonecode?.let { if (it.startsWith("+")) it else "+$it" } ?: ""
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCountrySelected(countryItem) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isSelected) AppTheme.colors.primary else AppTheme.colors.border),
                            color = if (isSelected) AppTheme.colors.surfaceVariant else AppTheme.colors.surface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (emoji.isNotEmpty()) {
                                        Text(emoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text(
                                        text = countryItem.name,
                                        fontSize = 15.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textPrimary
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = dialCode,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textSecondary
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = AppTheme.colors.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
