package com.example.refluenceds.ui.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.refluenceds.R
import com.example.refluenceds.ui.theme.GradientEnd
import com.example.refluenceds.ui.theme.GradientStart
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.example.refluenceds.ui.theme.RefluencedsTheme
import com.example.refluenceds.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToSignup: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(validationError) {
        if (validationError != null) {
            delay(2000)
            validationError = null
        }
    }

    Box(modifier = Modifier.fillMaxSize().imePadding()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            LogoBox()
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (it.isNotEmpty()) validationError = null
                },
                placeholder = { Text("Email address", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5B5BD6),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (it.isNotEmpty()) validationError = null
                },
                placeholder = { Text("Password", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Text(
                        text = if (passwordVisible) "Hide" else "Show",
                        modifier = Modifier
                            .clickable { passwordVisible = !passwordVisible }
                            .padding(end = 16.dp),
                        color = Color(0xFFC4C4C4),
                        fontSize = 14.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5B5BD6),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(26.dp))

            GradientButton(
                text = "Login",
                onClick = {
                    when {
                        email.isEmpty() -> validationError = "Email is required"
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> validationError = "Invalid email format"
                        password.isEmpty() -> validationError = "Password is required"
                        email == "Test@yopmail.com" && password == "Test1234" -> onLoginSuccess()
                        else -> showErrorDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Forgot your password?",
                color = Color(0xFF5B5BD6),
                modifier = Modifier.clickable { onNavigateToForgotPassword() },
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(25.dp))

            AuthDivider(text = "or")

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "Create A New Account",
                color = Color(0xFF5B5BD6),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onNavigateToSignup() },
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (validationError != null) {
            ValidationToast(
                message = validationError!!,
                onDismiss = { validationError = null },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
            )
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("Ok")
                }
            },
            title = {
                Text(
                    "Account not found or password incorrect",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun ValidationToast(message: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth().clickable { onDismiss() },
        color = Color(0xFFFDE8E8),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFF8B4B4))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = Color(0xFFC81E1E),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = Color(0xFFC81E1E),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SignupScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToReferral: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showAlreadyExistsDialog by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(validationError) {
        if (validationError != null) {
            delay(2000)
            validationError = null
        }
    }

    Box(modifier = Modifier.fillMaxSize().imePadding()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            LogoBox()
            Spacer(modifier = Modifier.height(22.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (it.isNotEmpty()) validationError = null
                },
                placeholder = { Text("Your email address", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5B5BD6),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (it.isNotEmpty()) validationError = null
                },
                placeholder = { Text("Password", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Text(
                        text = if (passwordVisible) "Hide" else "Show",
                        modifier = Modifier
                            .clickable { passwordVisible = !passwordVisible }
                            .padding(end = 16.dp),
                        color = Color(0xFFC4C4C4),
                        fontSize = 14.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5B5BD6),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true
            )
            Text(
                "Use at least 8 characters",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            GradientButton(
                text = "Create Account",
                onClick = {
                    when {
                        email.isEmpty() -> validationError = "Email is required"
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> validationError = "Invalid email format"
                        password.isEmpty() -> validationError = "Password is required"
                        password.length < 8 -> validationError = "Password length must be at least 8 characters long"
                        email == "Test@yopmail.com" -> showAlreadyExistsDialog = true
                        else -> {
                            viewModel.email.value = email
                            viewModel.password.value = password
                            onNavigateToReferral()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthDivider(text = "or sign in with:")

            Spacer(modifier = Modifier.height(24.dp))

            SocialButton(text = "Continue with TikTok", iconRes = null)
            Spacer(modifier = Modifier.height(12.dp))
            SocialButton(text = "Continue with Google", iconRes = null)

            Spacer(modifier = Modifier.height(32.dp))

            AuthDivider(text = "or")

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Log in with an existing account",
                color = Color(0xFF5B5BD6),
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
            Spacer(modifier = Modifier.height(40.dp))
        }

        if (validationError != null) {
            ValidationToast(
                message = validationError!!,
                onDismiss = { validationError = null },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
            )
        }
    }

    if (showAlreadyExistsDialog) {
        AlertDialog(
            onDismissRequest = { showAlreadyExistsDialog = false },
            confirmButton = {
                TextButton(onClick = { showAlreadyExistsDialog = false }) {
                    Text("Ok")
                }
            },
            title = {
                Text(
                    "Account already exists",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(validationError) {
        if (validationError != null) {
            delay(2000)
            validationError = null
        }
    }

    Box(modifier = Modifier.fillMaxSize().imePadding()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            LogoBox()
            Spacer(modifier = Modifier.height(22.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (it.isNotEmpty()) validationError = null
                },
                label = { Text("Email address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            GradientButton(
                text = "Recover my password",
                onClick = {
                    when {
                        email.isEmpty() -> validationError = "Email is required"
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> validationError = "Invalid email format"
                        else -> { /* Handle recovery */ }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Back",
                color = Color(0xFF5B5BD6),
                modifier = Modifier.clickable { onBack() }
            )
        }

        if (validationError != null) {
            ValidationToast(
                message = validationError!!,
                onDismiss = { validationError = null },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralCodeScreen(
    viewModel: AuthViewModel,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit
) {
    var referralCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(validationError) {
        if (validationError != null) {
            delay(2000)
            validationError = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.referal),
                                contentDescription = null,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Referral code", fontWeight = FontWeight.Bold)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "If you have a referral code, enter it here.",
                    modifier = Modifier.align(Alignment.Start),
                    color = Color(0xFF1A1A2E),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = referralCode,
                    onValueChange = { if (it.length <= 6) referralCode = it },
                    placeholder = { Text("Referral code goes here", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5B5BD6),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Text(
                    "Not required",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp, start = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                InfoBox(
                    title = "Friend invited you?",
                    description = "Make sure to use their code to earn cash for you and your friend.\nOnly earned after first application."
                )

                Spacer(modifier = Modifier.height(16.dp))

                InfoBox(
                    title = "Brand invited you?",
                    description = "Make sure to use their code to become a brand ambassador and unlock all the benefits."
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            delay(2000)
                            isLoading = false
                            validationError = "Invalid referral code"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = referralCode.length >= 4 && !isLoading,
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (referralCode.length >= 4) Color(0xFF5B5BD6) else Color(0xFFEEEEEE),
                        contentColor = if (referralCode.length >= 4) Color.White else Color(0xFF9EA3AE),
                        disabledContainerColor = Color(0xFFEEEEEE),
                        disabledContentColor = Color(0xFF9EA3AE)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Confirm code", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onSkip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(1.dp, Color(0xFFE9E9EB), RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF5B5BD6)
                    ),
                    elevation = null
                ) {
                    Text("Skip", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (validationError != null) {
            ValidationToast(
                message = validationError!!,
                onDismiss = { validationError = null },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingStepScreen(
    title: String,
    step: Int,
    totalSteps: Int = 6,
    onBack: () -> Unit,
    onNext: () -> Unit,
    nextButtonEnabled: Boolean = true,
    nextButtonText: String = "Next",
    showSkip: Boolean = false,
    onSkip: () -> Unit = {},
    showSpacer: Boolean = true,
    isScrollable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (showSkip) {
                        TextButton(onClick = onSkip) {
                            Text("Skip", color = Color(0xFF5B5BD6))
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(if (isScrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..totalSteps) {
                        StepCircle(step = i, currentStep = step)
                        if (i < totalSteps) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }

                if (showSpacer) {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                content()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = nextButtonEnabled,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4B4FE4),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFEEEEEE),
                    disabledContentColor = Color.Gray
                )
            ) {
                Text(nextButtonText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun FirstNameScreen(viewModel: AuthViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf(viewModel.firstName.value) }
    OnboardingStepScreen(
        title = "First name",
        step = 1,
        onBack = onBack,
        onNext = {
            viewModel.firstName.value = name
            onNext()
        },
        nextButtonEnabled = name.isNotEmpty()
    ) {
        Text("First name", modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("First name", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5B5BD6),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Composable
fun LastNameScreen(viewModel: AuthViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf(viewModel.lastName.value) }
    OnboardingStepScreen(
        title = "Last name",
        step = 2,
        onBack = onBack,
        onNext = {
            viewModel.lastName.value = name
            onNext()
        },
        nextButtonEnabled = name.isNotEmpty()
    ) {
        Text("Last name", modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Last name", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5B5BD6),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Composable
fun GenderScreen(viewModel: AuthViewModel, onNext: () -> Unit, onBack: () -> Unit, onSkip: () -> Unit) {
    var selectedGender by remember { mutableStateOf(viewModel.gender.value) }
    val genders = listOf("Female", "Male", "Diverse")

    OnboardingStepScreen(
        title = "Gender",
        step = 3,
        onBack = onBack,
        onNext = {
            viewModel.gender.value = selectedGender
            onNext()
        },
        nextButtonEnabled = selectedGender.isNotEmpty(),
        showSkip = true,
        onSkip = onSkip
    ) {
        Text("What gender do you identify as?", modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(16.dp))
        genders.forEach { gender ->
            SelectableOption(
                text = gender,
                isSelected = selectedGender == gender,
                onSelect = { selectedGender = gender }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun CountryScreen(viewModel: AuthViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    var selectedCountry by remember { mutableStateOf(viewModel.country.value) }
    val countries = listOf("Switzerland", "Germany", "France", "Italy", "Liechtenstein", "Austria", "United States", "United Kingdom")

    OnboardingStepScreen(
        title = "Country",
        step = 4,
        onBack = onBack,
        onNext = {
            viewModel.country.value = selectedCountry
            onNext()
        },
        nextButtonEnabled = selectedCountry.isNotEmpty()
    ) {
        Text("The country you live in", modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(16.dp))
        countries.forEach { country ->
            SelectableOption(
                text = country,
                isSelected = selectedCountry == country,
                onSelect = { selectedCountry = country }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun InterestsScreen(viewModel: AuthViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val selectedInterests = remember { mutableStateListOf<String>().apply { addAll(viewModel.selectedInterests.value) } }
    val industries = listOf(
        "Beauty", "Fashion", "Gastronomy", "Food & Drink", "Travel", "Sports",
        "Jewelry", "Technology", "Events", "Lifestyle", "Sustainability", "Home",
        "Finances", "Cars", "Cooking", "Entertainment", "Family", "Health",
        "Outdoors", "Pets", "Plant Based"
    )

    OnboardingStepScreen(
        title = "Industries you're interested in",
        step = 5,
        onBack = onBack,
        onNext = {
            viewModel.selectedInterests.value = selectedInterests
            onNext()
        },
        nextButtonEnabled = selectedInterests.isNotEmpty(),
        showSpacer = true,
        isScrollable = false
    ) {
        Surface(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFFF1F1F1))
        ) {
            Text(
                text = "${selectedInterests.size} / 5",
                color = Color(0xFF4B4FE4),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(industries) { industry ->
                val imageRes = when (industry) {
                    "Beauty" -> R.drawable.beauty
                    "Fashion" -> R.drawable.fashion
                    "Gastronomy" -> R.drawable.gastronomy
                    "Food & Drink" -> R.drawable.food_drink
                    "Travel" -> R.drawable.travel
                    "Sports" -> R.drawable.sports
                    "Events" -> R.drawable.events
                    "Lifestyle" -> R.drawable.lifestyle
                    "Sustainability" -> R.drawable.sustainability
                    "Home" -> R.drawable.home
                    "Finances" -> R.drawable.finances
                    "Cars" -> R.drawable.cars
                    else -> R.drawable.app_icon
                }
                IndustryItem(
                    name = industry,
                    imageRes = imageRes,
                    isSelected = selectedInterests.contains(industry),
                    onToggle = {
                        if (selectedInterests.contains(industry)) {
                            selectedInterests.remove(industry)
                        } else if (selectedInterests.size < 5) {
                            selectedInterests.add(industry)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePhotosScreen(viewModel: AuthViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val photos = remember { mutableStateListOf<Any>().apply { addAll(viewModel.profilePhotos.value) } }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            photos.add(it)
            viewModel.profilePhotos.value = photos.toList()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            photos.add(it)
            viewModel.profilePhotos.value = photos.toList()
        }
    }

    OnboardingStepScreen(
        title = "Profile photos",
        step = 6,
        onBack = onBack,
        onNext = {
            viewModel.profilePhotos.value = photos.toList()
            onNext()
        },
        nextButtonEnabled = photos.isNotEmpty(),
        showSpacer = true,
        isScrollable = false
    ) {
        Text("Show us your favorite pictures of yourself!", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(6) { index ->
                if (index < photos.size) {
                    PhotoItem(url = photos[index], onDelete = { 
                        photos.removeAt(index)
                        viewModel.profilePhotos.value = photos.toList()
                    })
                } else {
                    AddPhotoItem(onAdd = { showBottomSheet = true })
                }
            }
        }
    }

    if (showBottomSheet) {
        ImagePickerBottomSheet(
            onDismiss = { showBottomSheet = false },
            onTakePhoto = {
                cameraLauncher.launch()
                showBottomSheet = false
            },
            onPickFromGallery = {
                galleryLauncher.launch("image/*")
                showBottomSheet = false
            },
            sheetState = sheetState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerBottomSheet(
    onDismiss: () -> Unit,
    onTakePhoto: () -> Unit,
    onPickFromGallery: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(width = 40.dp) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Upload photo from",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            BottomSheetOption(
                text = "Camera",
                icon = Icons.Default.PhotoCamera,
                onClick = onTakePhoto
            )
            Spacer(modifier = Modifier.height(12.dp))
            BottomSheetOption(
                text = "Gallery",
                icon = Icons.Default.Image,
                onClick = onPickFromGallery
            )
        }
    }
}

@Composable
fun BottomSheetOption(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color(0xFFEAEAFF)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF4B4FE4),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = Color(0xFF4B4FE4),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(onAgree: () -> Unit, onBack: () -> Unit) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                title = { Text("Terms and Conditions", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(24.dp)) {
                Text("General Terms and Conditions (GTC) of Refluenced AG", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Version: April 29, 2024", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Text("I) General part", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("1. Scope", fontWeight = FontWeight.Bold)
                Text("(1) These General Terms and Conditions (\"Terms and Conditions\") apply between users and Refluenced AG (\"Refluenced\"). Users are advertising companies; hereinafter referred to as \"brand\"* or advertising medium; hereinafter \"Influencer\".", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("(2) These General Terms and Conditions consist of the general part, the special conditions for influencers and the special conditions for brands. Together with the individual contractual documents, these General Terms and Conditions represent the final agreement (hereinafter referred to as the \"Agreement\"*) between the users and Refluenced.", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("(3) General terms and conditions of the brand and/or the influencer are hereby explicitly excluded. They only become part of the contract if Refluenced has agreed to their validity in the offer or contract.", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("(4) Refluenced products are websites, mobile apps, web apps or the platform. By using a product from Refluenced, the user agrees to these terms and conditions. These...", fontSize = 14.sp)
            }
            
            Surface(
                shadowElevation = 20.dp,
                color = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Privacy Policy",
                        color = Color(0xFF5B5BD6),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "To learn more about how Refluenced collects, uses, shares and protects your personal data, please see the Refluenced privacy policy.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    SwipeToAgreeButton(onAgree = onAgree)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeToAgreeButton(onAgree: () -> Unit) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val containerWidth = 300.dp // Approximate width, will be fillMaxWidth in practice
    val thumbSize = 56.dp
    
    // Using a simpler approach with anchoredDraggable if available, 
    // but for compatibility we'll use a custom state with pointerInput or standard Swipeable
    // Given the environment, let's use a simple state-based swipe
    
    var offsetX by remember { mutableStateOf(0f) }
    val maxOffset = with(density) { (310.dp - 64.dp).toPx() } // Rough calculation for fillMaxWidth
    
    val scope = rememberCoroutineScope()
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFFF0F2F8), RoundedCornerShape(32.dp))
            .padding(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Agree to the terms, conditions & privacy",
            modifier = Modifier.fillMaxWidth().padding(start = 60.dp),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            color = Color(0xFF5B5BD6),
            fontWeight = FontWeight.Medium
        )
        
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.toInt(), 0) }
                .size(56.dp)
                .background(Color.White, CircleShape)
                .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX > maxOffset * 0.7f) {
                                offsetX = maxOffset
                                onAgree()
                            } else {
                                offsetX = 0f
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount).coerceIn(0f, maxOffset)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                contentDescription = null,
                tint = Color(0xFF5B5BD6),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialVerificationScreen(onNext: () -> Unit, onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf("Instagram") }
    val tabs = listOf("Instagram", "TikTok", "UGC")

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                title = { Text("Social Verification", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tab Selector
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                color = Color(0xFFF1F1F8),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    tabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        Surface(
                            onClick = { selectedTab = tab },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            color = if (isSelected) Color(0xFF4B4FE4) else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = tab,
                                    color = if (isSelected) Color.White else Color.Gray,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (selectedTab) {
                "Instagram", "TikTok" -> {
                    // Social Icon
                    Surface(
                        modifier = Modifier.size(140.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (selectedTab == "Instagram") Icons.Default.CameraAlt else Icons.Default.MusicNote,
                                contentDescription = selectedTab,
                                modifier = Modifier.size(70.dp),
                                tint = if (selectedTab == "Instagram") Color(0xFFE4405F) else Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Title with Gradient
                    Text(
                        text = "Connect your\n$selectedTab",
                        textAlign = TextAlign.Center,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 38.sp,
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF6C63FF), Color(0xFFFA5252))
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Admission Standards Table
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        color = Color.White
                    ) {
                        Column {
                            Text(
                                text = "Our Minimum Admission Standards",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

                            // Table Header
                            Row(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                                Row(
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (selectedTab == "Instagram") Icons.Default.Groups else Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        if (selectedTab == "Instagram") "Follower count" else "Followers",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                VerticalDivider(
                                    thickness = 1.dp,
                                    color = Color(0xFFE0E0E0),
                                    modifier = Modifier.fillMaxHeight().width(1.dp)
                                )
                                Row(
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (selectedTab == "Instagram") Icons.Default.ChatBubbleOutline else Icons.Default.Timeline,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        if (selectedTab == "Instagram") "Engagement rate" else "Videos/Month",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

                            // Table Rows
                            val tableData = if (selectedTab == "Instagram") {
                                listOf("1,000" to "10%", "2,000" to "7.5%", "5,000" to "5%")
                            } else {
                                listOf("1,000" to "10")
                            }
                            tableData.forEachIndexed { index, pair ->
                                Row(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                                    Box(
                                        modifier = Modifier.weight(1f).fillMaxHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(pair.first, fontSize = 13.sp)
                                    }
                                    VerticalDivider(
                                        thickness = 1.dp,
                                        color = Color(0xFFE0E0E0),
                                        modifier = Modifier.fillMaxHeight().width(1.dp)
                                    )
                                    Box(
                                        modifier = Modifier.weight(1f).fillMaxHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(pair.second, fontSize = 13.sp)
                                    }
                                }
                                if (index < tableData.size - 1) {
                                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
                                }
                            }
                        }
                    }
                }

                "UGC" -> {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            "Examples of successful video applications",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(3) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(width = 110.dp, height = 180.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.LightGray)
                                ) {
                                    AsyncImage(
                                        model = "https://picsum.photos/seed/ugc$index/200/300",
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    // Video play duration overlay
                                    Surface(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(8.dp),
                                        color = Color.Black.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.PlayArrow,
                                                null,
                                                modifier = Modifier.size(12.dp),
                                                tint = Color.White
                                            )
                                            Text(
                                                "00:30",
                                                color = Color.White,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // UGC Title
                        Text(
                            text = "Become UGC Verified\nwith a video\napplication",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 34.sp,
                            style = TextStyle(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF6C63FF), Color(0xFFFA5252))
                                )
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Another way to participate in campaigns is to send us a video application. This will grant you access to our UGC campaigns. these are video based campaigns the brand posts on their own social media. You just need to apply with a video and one of our admins will review your application",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 13.sp,
                            color = Color.Gray,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Button(
                onClick = { /* Connect or Upload */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B4FE4))
            ) {
                Text(
                    if (selectedTab == "UGC") "Upload video" else "Connect now",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Text(
                    "Continue to next step",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B4FE4)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun EmailVerificationScreen(email: String, onVerify: () -> Unit, onLater: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Email Icon in circular shadow container
        Surface(
            modifier = Modifier.size(160.dp),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Unsubscribe, // Using this as it looks similar to email with cross
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFFE4405F)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Email not verified",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = TextStyle(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF6C63FF), Color(0xFFFA5252))
                )
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "We need you to verify your account. We sent an email to $email. Also check your spam folder. Please verify your account through your email to continue.",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        GradientButton(
            text = "Verify",
            onClick = onVerify,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLater,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Text(
                text = "Later",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4FE4),
                fontSize = 16.sp
            )
        }
    }
}

// Helper components

@Composable
fun LogoBox() {
    Image(
        painter = painterResource(id = R.drawable.app_icon),
        contentDescription = "App Logo",
        modifier = Modifier.size(100.dp)
    )
}

@Composable
fun AuthDivider(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color(0xFFEEEEEE)
        )
        Text(
            text = text,
            color = Color(0xFFC4C4C4),
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 14.sp
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color(0xFFEEEEEE)
        )
    }
}

@Composable
fun GradientButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(listOf(GradientStart, GradientEnd)),
                    shape = RoundedCornerShape(28.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SocialButton(text: String, iconRes: Int?, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = { },
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).align(Alignment.CenterStart)
                )
            } else {
                // Placeholder for icon to maintain alignment if needed, or just skip
            }
            Text(
                text = text,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center),
                color = Color.Black
            )
        }
    }
}

@Composable
fun InfoBox(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F7F9)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(24.dp).background(Color(0xFF7A7E85), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("i", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, fontSize = 14.sp, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun StepCircle(step: Int, currentStep: Int) {
    val isCompleted = step < currentStep
    val isCurrent = step == currentStep
    Box(
        modifier = Modifier.size(32.dp).background(
            color = when {
                isCompleted -> Color(0xFF3EBD8F)
                isCurrent -> Color(0xFF4B4FE4)
                else -> Color(0xFFEAEAFF)
            },
            shape = CircleShape
        ),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        } else {
            Text(text = step.toString(), color = if (isCurrent) Color.White else Color(0xFF4B4FE4), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun SelectableOption(text: String, isSelected: Boolean, onSelect: () -> Unit) {
    Surface(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFFEAEAFF) else Color(0xFFF6F7F9)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            fontSize = 16.sp,
            color = Color(0xFF1A1A2E)
        )
    }
}

@Composable
fun IndustryItem(name: String, imageRes: Int, isSelected: Boolean, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggle() }
            .then(if (isSelected) Modifier.border(2.5.dp, Color(0xFF4B4FE4), RoundedCornerShape(12.dp)) else Modifier)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Subtle dark overlay for readability
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.15f)))
        // Label at bottom-left
        Text(
            text = name,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        // Selection indicator: white circle top-right (filled blue when selected, solid white when not)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(24.dp)
                .background(
                    color = if (isSelected) Color(0xFF4B4FE4) else Color.White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun PhotoItem(url: Any, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF4B4FE4), RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                .size(24.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun AddPhotoItem(onAdd: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .border(1.dp, Color(0xFF4B4FE4), RoundedCornerShape(12.dp))
            .clickable { onAdd() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF4B4FE4), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    RefluencedsTheme {
        Surface {
            LoginScreen(
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
                onNavigateToSignup = {},
                onNavigateToForgotPassword = {},
                onLoginSuccess = {}
            )
        }
    }
}
