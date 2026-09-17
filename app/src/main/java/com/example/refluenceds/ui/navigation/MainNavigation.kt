package com.example.refluenceds.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.refluenceds.R
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.refluenceds.ui.screens.*
import com.example.refluenceds.ui.screens.auth.*
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.theme.GradientStart
import com.example.refluenceds.ui.viewmodel.AuthViewModel
import com.example.refluenceds.ui.viewmodel.CampaignViewModel
import com.example.refluenceds.utils.SetStatusBarAppearance

@Composable
fun MainNavigation(sessionManager: com.example.refluenceds.data.local.SessionManager? = null) {
    val authViewModel: AuthViewModel = viewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    if (isLoggedIn) {
        AppNavigation(authViewModel, sessionManager)
    } else {
        AuthNavigation(authViewModel)
    }
}

@Composable
fun AuthNavigation(viewModel: AuthViewModel) {
    SetStatusBarAppearance(isLightStatusBars = true)
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(Destination.Login)
    val email by viewModel.email.collectAsState()

    fun getDestinationForStep(step: Int): Destination {
        return when (step) {
            1 -> Destination.FirstName
            2 -> Destination.LastName
            3 -> Destination.Gender
            4 -> Destination.Country
            5 -> Destination.Interests
            6 -> Destination.ProfilePhotos
            7 -> Destination.TermsAndConditions
            else -> Destination.ReferralCode
        }
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.checkAndResumeOnboarding { step ->
            backStack.add(getDestinationForStep(step))
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = { key ->
            when (key) {
                is Destination.Signup -> NavEntry(key) {
                    SignupScreen(
                        viewModel = viewModel,
                        onNavigateToLogin = {
                            backStack.clear()
                            backStack.add(Destination.Login)
                        },
                        onNavigateToReferral = { backStack.add(Destination.ReferralCode) }
                    )
                }
                is Destination.Login -> NavEntry(key) {
                    LoginScreen(
                        viewModel = viewModel,
                        onNavigateToSignup = {
                            backStack.clear()
                            backStack.add(Destination.Signup)
                        },
                        onNavigateToForgotPassword = { backStack.add(Destination.ForgotPassword) },
                        onLoginSuccess = { isCompleted, currentStep ->
                            if (!isCompleted) {
                                backStack.add(getDestinationForStep(currentStep))
                            }
                        }
                    )
                }
                is Destination.ForgotPassword -> NavEntry(key) {
                    ForgotPasswordScreen(
                        viewModel = viewModel,
                        onBack = { backStack.removeAt(backStack.size - 1) }
                    )
                }
                is Destination.ReferralCode -> NavEntry(key) {
                    ReferralCodeScreen(
                        viewModel = viewModel,
                        onNext = { backStack.add(Destination.FirstName) },
                        onSkip = { backStack.add(Destination.FirstName) },
                        onBack = { backStack.removeAt(backStack.size - 1) }
                    )
                }
                is Destination.FirstName -> NavEntry(key) {
                    FirstNameScreen(
                        viewModel = viewModel,
                        onNext = { backStack.add(Destination.LastName) },
                        onBack = { backStack.removeAt(backStack.size - 1) }
                    )
                }
                is Destination.LastName -> NavEntry(key) {
                    LastNameScreen(
                        viewModel = viewModel,
                        onNext = { backStack.add(Destination.Gender) },
                        onBack = { backStack.removeAt(backStack.size - 1) }
                    )
                }
                is Destination.Gender -> NavEntry(key) {
                    GenderScreen(
                        viewModel = viewModel,
                        onNext = { backStack.add(Destination.Country) },
                        onBack = { backStack.removeAt(backStack.size - 1) },
                        onSkip = { backStack.add(Destination.Country) }
                    )
                }
                is Destination.Country -> NavEntry(key) {
                    CountryScreen(
                        viewModel = viewModel,
                        onNext = { backStack.add(Destination.Interests) },
                        onBack = { backStack.removeAt(backStack.size - 1) }
                    )
                }
                is Destination.Interests -> NavEntry(key) {
                    InterestsScreen(
                        viewModel = viewModel,
                        onNext = { backStack.add(Destination.ProfilePhotos) },
                        onBack = { backStack.removeAt(backStack.size - 1) }
                    )
                }
                is Destination.ProfilePhotos -> NavEntry(key) {
                    ProfilePhotosScreen(
                        viewModel = viewModel,
                        onNext = { backStack.add(Destination.TermsAndConditions) },
                        onBack = { backStack.removeAt(backStack.size - 1) }
                    )
                }
                is Destination.TermsAndConditions -> NavEntry(key) {
                    TermsAndConditionsScreen(
                        viewModel = viewModel,
                        onAgree = { backStack.add(Destination.SocialVerification) },
                        onBack = { backStack.removeAt(backStack.size - 1) }
                    )
                }
                is Destination.SocialVerification -> NavEntry(key) {
                    SocialVerificationScreen(
                        onNext = { backStack.add(Destination.EmailVerification) },
                        onBack = { backStack.removeAt(backStack.size - 1) }
                    )
                }
                is Destination.EmailVerification -> NavEntry(key) {
                    EmailVerificationScreen(
                        email = email,
                        onVerify = { viewModel.signup() },
                        onLater = { viewModel.signup() }
                    )
                }
                else -> NavEntry(key) { Text("Unknown") }
            }
        }
    )
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    sessionManager: com.example.refluenceds.data.local.SessionManager? = null
) {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(Destination.Home)
    val currentKey = backStack.lastOrNull() ?: Destination.Home
    val campaignViewModel: CampaignViewModel = viewModel()

    val showNavSuite = currentKey in listOf(
        Destination.Home,
        Destination.Social,
        Destination.Campaigns,
        Destination.Inbox,
        Destination.Earnings
    )

    val isDarkScreen = currentKey is Destination.ContactUs ||
            currentKey is Destination.Social ||
            currentKey is Destination.BrandGone

    if (currentKey !is Destination.Earnings) {
        SetStatusBarAppearance(isLightStatusBars = !isDarkScreen)
    }

    val userProfile by authViewModel.userProfile.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        NavDisplay<NavKey>(
            backStack = backStack,
            modifier = Modifier.fillMaxSize(),
            entryProvider = { key ->
                when (key) {
                    is Destination.Home -> NavEntry(key) { 
                        HomeScreen(
                            viewModel = campaignViewModel,
                            authViewModel = authViewModel,
                            onViewAcademyAll = {
                                if (currentKey != Destination.Academy) {
                                    backStack.add(Destination.Academy)
                                }
                            },
                            onViewCampaignsAll = {
                                if (currentKey != Destination.Campaigns) {
                                    backStack.clear()
                                    backStack.add(Destination.Campaigns)
                                }
                            },
                            onViewBrandsAll = {
                                if (currentKey != Destination.Brands) {
                                    backStack.add(Destination.Brands)
                                }
                            },
                            onNavigateToAcademyDetail = { tutorialId ->
                                campaignViewModel.triggerLoading()
                                backStack.add(Destination.AcademyDetail(tutorialId))
                            },
                            onNavigateToCampaignDetail = { campaignId ->
                                backStack.add(Destination.CampaignDetail(campaignId))
                            },
                            onNavigateToYourReferrals = {
                                if (currentKey != Destination.YourReferrals) {
                                    backStack.add(Destination.YourReferrals)
                                }
                            },
                            onNavigateToBrandDetail = { brandId, brandName ->
                                backStack.add(Destination.BrandDetail(brandId = brandId, brandName = brandName))
                            }
                        ) 
                    }
                    is Destination.Campaigns -> NavEntry(key) { 
                        CampaignsScreen(
                            viewModel = campaignViewModel,
                            onNavigateToCampaignDetail = { campaignId ->
                                backStack.add(Destination.CampaignDetail(campaignId))
                            }
                        ) 
                    }
                    is Destination.CampaignDetail -> NavEntry(key) {
                        CampaignDetailScreen(
                            campaignId = key.campaignId,
                            viewModel = campaignViewModel,
                            authViewModel = authViewModel,
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.remove(key)
                                }
                            },
                            onNavigateToBrandDetail = { brandId, brandName ->
                                backStack.add(Destination.BrandDetail(brandId = brandId, brandName = brandName))
                            },
                            onNavigateToEditProfile = {
                                backStack.add(Destination.EditProfile)
                            },
                            onNavigateToConnectInstagram = {
                                backStack.add(Destination.ConnectInstagram)
                            }
                        )
                    }
                    is Destination.Submissions -> NavEntry(key) { SubmissionsScreen(campaignViewModel) }
                    is Destination.Academy -> NavEntry(key) { 
                        AcademyScreen(
                            viewModel = campaignViewModel,
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.remove(key)
                                }
                            },
                            onNavigateToAcademyDetail = { tutorialId ->
                                campaignViewModel.triggerLoading()
                                backStack.add(Destination.AcademyDetail(tutorialId))
                            }
                        ) 
                    }
                    is Destination.AcademyDetail -> NavEntry(key) {
                        AcademyDetailScreen(
                            tutorialId = key.tutorialId,
                            viewModel = campaignViewModel,
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.remove(key)
                                }
                            }
                        )
                    }
                    is Destination.Brands -> NavEntry(key) {
                        MyBrandsScreen(
                            viewModel = campaignViewModel,
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.remove(key)
                                }
                            },
                            onNavigateToBrandDetail = { brandId, brandName ->
                                backStack.add(Destination.BrandDetail(brandId = brandId, brandName = brandName))
                            }
                        )
                    }
                    is Destination.BrandDetail -> NavEntry(key) {
                        BrandDetailScreen(
                            brandId = key.brandId,
                            brandName = key.brandName,
                            viewModel = campaignViewModel,
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.remove(key)
                                }
                            },
                            onNavigateToCampaignDetail = { campaignId ->
                                backStack.add(Destination.CampaignDetail(campaignId))
                            },
                            onNavigateToReviews = { brandId, brandName ->
                                backStack.add(Destination.BrandReviews(brandId = brandId, brandName = brandName))
                            }
                        )
                    }
                    is Destination.BrandReviews -> NavEntry(key) {
                        BrandReviewsScreen(
                            brandId = key.brandId,
                            brandName = key.brandName,
                            isCreator = key.isCreator,
                            viewModel = campaignViewModel,
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.remove(key)
                                }
                            },
                            onNavigateToCampaignDetail = { campaignId ->
                                backStack.add(Destination.CampaignDetail(campaignId))
                            }
                        )
                    }
                    is Destination.BrandGone -> NavEntry(key) {
                        BrandGoneScreen(
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.remove(key)
                                }
                            }
                        )
                    }
                    is Destination.Inbox -> NavEntry(key) { InboxScreen(viewModel = campaignViewModel) }
                    is Destination.Social -> NavEntry(key) {
                        SocialScreen(
                            viewModel = campaignViewModel,
                            onNavigateToBrandGone = { brandId, brandName ->
                                backStack.add(Destination.BrandGone)
                            },
                            onNavigateToInfluencerProfile = { creatorId, creatorName ->
                                backStack.add(Destination.InfluencerProfile(creatorId = creatorId, creatorName = creatorName))
                            },
                            onNavigateToBrandDetail = { brandId, brandName ->
                                backStack.add(Destination.BrandDetail(brandId = brandId, brandName = brandName))
                            }
                        )
                    }
                    is Destination.InfluencerProfile -> NavEntry(key) {
                        InfluencerProfileScreen(
                            creatorId = key.creatorId,
                            creatorName = key.creatorName,
                            viewModel = campaignViewModel,
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.remove(key)
                                }
                            },
                            onNavigateToReviews = { creatorId, creatorName ->
                                backStack.add(
                                    Destination.BrandReviews(
                                        brandId = creatorId,
                                        brandName = creatorName,
                                        isCreator = true
                                    )
                                )
                            },
                            onNavigateToCampaignDetail = { campaignId ->
                                backStack.add(Destination.CampaignDetail(campaignId))
                            }
                        )
                    }
                    is Destination.Earnings -> NavEntry(key) {
                        EarningsScreen(
                            viewModel = campaignViewModel,
                            authViewModel = authViewModel,
                            onNavigateToCampaigns = {
                                if (currentKey != Destination.Campaigns) {
                                    backStack.clear()
                                    backStack.add(Destination.Campaigns)
                                }
                            },
                            onNavigateToYourCampaigns = {
                                backStack.add(Destination.YourCampaigns)
                            },
                            onNavigateToYourCollection = {
                                backStack.add(Destination.YourCollection)
                            },
                            onNavigateToMyBrands = {
                                if (currentKey != Destination.Brands) {
                                    backStack.add(Destination.Brands)
                                }
                            },
                            onNavigateToAcademy = {
                                if (currentKey != Destination.Academy) {
                                    backStack.add(Destination.Academy)
                                }
                            },
                            onNavigateToYourReferrals = {
                                if (currentKey != Destination.YourReferrals) {
                                    backStack.add(Destination.YourReferrals)
                                }
                            },
                            onNavigateToSettings = {
                                if (currentKey != Destination.Settings) {
                                    backStack.add(Destination.Settings)
                                }
                            },
                            onNavigateToEditProfile = {
                                if (currentKey != Destination.EditProfile) {
                                    backStack.add(Destination.EditProfile)
                                }
                            },
                            onNavigateToContactUs = {
                                if (currentKey != Destination.ContactUs) {
                                    backStack.add(Destination.ContactUs)
                                }
                            },
                            onNavigateToCashEarned = {
                                if (currentKey != Destination.CashEarned) {
                                    backStack.add(Destination.CashEarned)
                                }
                            },
                            onNavigateToWaysToEarn = {
                                if (currentKey != Destination.WaysToEarn) {
                                    backStack.add(Destination.WaysToEarn)
                                }
                            },
                            onNavigateToUgcInfo = {
                                if (currentKey != Destination.UgcVideoInfo) {
                                    backStack.add(Destination.UgcVideoInfo)
                                }
                            }
                        )
                    }
                    is Destination.YourCampaigns -> NavEntry(key) {
                        YourCampaignsScreen(
                            viewModel = campaignViewModel,
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            },
                            onNavigateToCampaignDetail = { campaignId ->
                                backStack.add(Destination.CampaignDetail(campaignId))
                            }
                        )
                    }
                    is Destination.YourCollection -> NavEntry(key) {
                        YourCollectionScreen(
                            viewModel = campaignViewModel,
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            },
                            onNavigateToSocialFeed = {
                                backStack.add(Destination.Social)
                            },
                            onNavigateToAcademy = {
                                backStack.add(Destination.Academy)
                            },
                            onNavigateToAcademyDetail = { tutorialId ->
                                backStack.add(Destination.AcademyDetail(tutorialId))
                            }
                        )
                    }
                    is Destination.YourReferrals -> NavEntry(key) {
                        YourReferralsScreen(
                            authViewModel = authViewModel,
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    is Destination.Settings -> NavEntry(key) {
                        SettingsScreen(
                            authViewModel = authViewModel,
                            sessionManager = sessionManager,
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            },
                            onLogout = {
                                authViewModel.logout()
                            },
                            onNavigateToLanguage = {
                                if (currentKey != Destination.LanguagePreference) {
                                    backStack.add(Destination.LanguagePreference)
                                }
                            },
                            onNavigateToChangePassword = {
                                if (currentKey != Destination.ChangePassword) {
                                    backStack.add(Destination.ChangePassword)
                                }
                            },
                            onNavigateToPushNotifications = {
                                if (currentKey != Destination.PushNotifications) {
                                    backStack.add(Destination.PushNotifications)
                                }
                            },
                            onNavigateToEmailNotifications = {
                                if (currentKey != Destination.EmailNotifications) {
                                    backStack.add(Destination.EmailNotifications)
                                }
                            },
                            onNavigateToEditProfile = {
                                if (currentKey != Destination.EditProfile) {
                                    backStack.add(Destination.EditProfile)
                                }
                            },
                            onNavigateToContactUs = {
                                if (currentKey != Destination.ContactUs) {
                                    backStack.add(Destination.ContactUs)
                                }
                            }
                        )
                    }
                    is Destination.ContactUs -> NavEntry(key) {
                        ContactUsScreen(
                            authViewModel = authViewModel,
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    is Destination.LanguagePreference -> NavEntry(key) {
                        LanguagePreferenceScreen(
                            sessionManager = sessionManager,
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    is Destination.ChangePassword -> NavEntry(key) {
                        ChangePasswordScreen(
                            authViewModel = authViewModel,
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    is Destination.PushNotifications -> NavEntry(key) {
                        PushNotificationsScreen(
                            authViewModel = authViewModel,
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    is Destination.EmailNotifications -> NavEntry(key) {
                        EmailNotificationsScreen(
                            authViewModel = authViewModel,
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    is Destination.EditProfile -> NavEntry(key) {
                        EditProfileScreen(
                            authViewModel = authViewModel,
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            },
                            onNavigateToContactUs = {
                                if (currentKey != Destination.ContactUs) {
                                    backStack.add(Destination.ContactUs)
                                }
                            }
                        )
                    }
                    is Destination.ConnectInstagram -> NavEntry(key) {
                        ConnectInstagramScreen(
                            onClose = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    is Destination.CashEarned -> NavEntry(key) {
                        CashEarnedScreen(
                            authViewModel = authViewModel,
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            },
                            onNavigateToWaysToEarn = {
                                if (currentKey != Destination.WaysToEarn) {
                                    backStack.add(Destination.WaysToEarn)
                                }
                            },
                            onNavigateToContactUs = {
                                if (currentKey != Destination.ContactUs) {
                                    backStack.add(Destination.ContactUs)
                                }
                            }
                        )
                    }
                    is Destination.WaysToEarn -> NavEntry(key) {
                        WaysToEarnScreen(
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            },
                            onNavigateToReferrals = {
                                if (currentKey != Destination.YourReferrals) {
                                    backStack.add(Destination.YourReferrals)
                                }
                            },
                            onNavigateToUgcInfo = {
                                if (currentKey != Destination.UgcVideoInfo) {
                                    backStack.add(Destination.UgcVideoInfo)
                                }
                            },
                            onNavigateToCampaigns = {
                                if (currentKey != Destination.Campaigns) {
                                    backStack.clear()
                                    backStack.add(Destination.Campaigns)
                                }
                            }
                        )
                    }
                    is Destination.UgcVideoInfo -> NavEntry(key) {
                        UgcVideoInfoScreen(
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            },
                            onApply = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    else -> NavEntry(key) { Text("Unknown") }
                }
            }
        )

        if (showNavSuite) {
            FloatingBottomNavBar(
                currentKey = currentKey,
                userProfile = userProfile,
                onNavigate = { destination ->
                    when (destination) {
                        Destination.Home -> {
                            campaignViewModel.fetchHomeData()
                            campaignViewModel.fetchInboxSummary()
                            authViewModel.fetchUserProfile()
                            authViewModel.fetchMyReferralCodeAndStats()
                        }
                        Destination.Social -> {
                            campaignViewModel.fetchContentFeed()
                        }
                        Destination.Campaigns -> {
                            campaignViewModel.fetchCampaigns()
                        }
                        Destination.Inbox -> {
                            campaignViewModel.fetchUnifiedInbox("chat")
                            campaignViewModel.fetchInboxSummary()
                        }
                        Destination.Earnings -> {
                            authViewModel.fetchUserProfile()
                            authViewModel.fetchMyReferralCodeAndStats()
                            campaignViewModel.fetchMyBadges()
                        }
                        else -> {}
                    }
                    if (currentKey != destination) {
                        backStack.clear()
                        backStack.add(destination)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun FloatingBottomNavBar(
    currentKey: NavKey,
    userProfile: com.example.refluenceds.data.remote.dto.UserDto?,
    onNavigate: (Destination) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(percent = 50),
        color = AppTheme.colors.navBarBackground,
        shadowElevation = if (AppTheme.isDark) 0.dp else 8.dp,
        border = BorderStroke(1.dp, if (AppTheme.isDark) AppTheme.colors.border else Color(0xFFF1F1F6))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 0: Home
            NavPillItem(
                isSelected = currentKey == Destination.Home,
                iconRes = if (currentKey == Destination.Home) R.drawable.ic_nav_home_filled else R.drawable.ic_nav_home_outline,
                contentDescription = "Home",
                onClick = { onNavigate(Destination.Home) }
            )

            // Tab 1: Social
            NavPillItem(
                isSelected = currentKey == Destination.Social,
                iconRes = if (currentKey == Destination.Social) R.drawable.ic_nav_users_filled else R.drawable.ic_nav_users_outline,
                contentDescription = "Social",
                onClick = { onNavigate(Destination.Social) }
            )

            // Tab 2: Campaigns (Center)
            NavPillItem(
                isSelected = currentKey == Destination.Campaigns,
                iconRes = if (currentKey == Destination.Campaigns) R.drawable.ic_nav_megaphone_filled else R.drawable.ic_nav_megaphone_outline,
                contentDescription = "Campaigns",
                onClick = { onNavigate(Destination.Campaigns) }
            )

            // Tab 3: Inbox
            NavPillItem(
                isSelected = currentKey == Destination.Inbox,
                iconRes = if (currentKey == Destination.Inbox) R.drawable.ic_nav_chat_filled else R.drawable.ic_nav_chat_outline,
                contentDescription = "Inbox",
                onClick = { onNavigate(Destination.Inbox) }
            )

            // Tab 4: Earnings / Profile
            val isProfileSelected = currentKey == Destination.Earnings
            val avatarUrl = userProfile?.avatar

            Box(
                modifier = Modifier
                    .size(width = 56.dp, height = 46.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(if (isProfileSelected) (if (AppTheme.isDark) Color(0xFF2E2E44) else Color(0xFFEEF0FE)) else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNavigate(Destination.Earnings) },
                contentAlignment = Alignment.Center
            ) {
                if (!avatarUrl.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(avatarUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (isProfileSelected) 1.5.dp else 1.dp,
                                color = if (isProfileSelected) AppTheme.colors.primary else if (AppTheme.isDark) Color(0xFF3E3E56) else Color(0xFFDCDCE8),
                                shape = CircleShape
                            ),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(AppTheme.colors.inputBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isProfileSelected) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = if (isProfileSelected) AppTheme.colors.primary else AppTheme.colors.navBarIconTint
                                )
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(AppTheme.colors.inputBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isProfileSelected) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = if (isProfileSelected) AppTheme.colors.primary else AppTheme.colors.navBarIconTint
                                )
                            }
                        }
                    )
                } else {
                    Icon(
                        imageVector = if (isProfileSelected) Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(24.dp),
                        tint = if (isProfileSelected) AppTheme.colors.primary else AppTheme.colors.navBarIconTint
                    )
                }
            }
        }
    }
}

@Composable
private fun NavPillItem(
    isSelected: Boolean,
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 56.dp, height = 46.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(if (isSelected) (if (AppTheme.isDark) Color(0xFF2E2E44) else Color(0xFFEEF0FE)) else Color.Transparent)
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp),
            tint = if (isSelected) AppTheme.colors.primary else AppTheme.colors.navBarIconTint
        )
    }
}
