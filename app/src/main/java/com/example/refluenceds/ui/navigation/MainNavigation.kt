package com.example.refluenceds.ui.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.refluenceds.ui.screens.*
import com.example.refluenceds.ui.screens.auth.*
import com.example.refluenceds.ui.theme.GradientStart
import com.example.refluenceds.ui.viewmodel.AuthViewModel
import com.example.refluenceds.ui.viewmodel.CampaignViewModel

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
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(Destination.Login)
    val email by viewModel.email.collectAsState()

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
                        onLoginSuccess = { viewModel.login() }
                    )
                }
                is Destination.ForgotPassword -> NavEntry(key) {
                    ForgotPasswordScreen(onBack = { backStack.removeAt(backStack.size - 1) })
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

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            secondaryContainer = Color(0xFFF3F3FE),
        ),
        shapes = MaterialTheme.shapes.copy(
            extraLarge = RoundedCornerShape(14.dp)
        )
    ) {
        val itemColors = NavigationSuiteDefaults.itemColors(
            navigationBarItemColors = NavigationBarItemDefaults.colors(
                indicatorColor = Color(0xFFF3F3FE),
                selectedIconColor = Color(0xFF4B4FE4),
                unselectedIconColor = Color(0xFF9E9EB0),
            )
        )
        NavigationSuiteScaffold(
            modifier = Modifier.fillMaxSize(),
            navigationSuiteColors = NavigationSuiteDefaults.colors(
                navigationBarContainerColor = Color.White,
            ),
            navigationSuiteItems = {
                item(
                    selected = currentKey == Destination.Home,
                    colors = itemColors,
                    onClick = {
                        if (currentKey != Destination.Home) {
                            backStack.clear()
                            backStack.add(Destination.Home)
                        }
                    },
                    icon = { 
                        val isSelected = currentKey == Destination.Home
                        Icon(
                            painter = painterResource(id = if (isSelected) R.drawable.ic_nav_home_filled else R.drawable.ic_nav_home_outline), 
                            contentDescription = "Home",
                            modifier = Modifier.size(26.dp),
                            tint = if (isSelected) Color(0xFF4B4FE4) else Color(0xFF9E9EB0)
                        ) 
                    }
                )
                item(
                    selected = currentKey == Destination.Social,
                    colors = itemColors,
                    onClick = {
                        if (currentKey != Destination.Social) {
                            backStack.clear()
                            backStack.add(Destination.Social)
                        }
                    },
                    icon = { 
                        val isSelected = currentKey == Destination.Social
                        Icon(
                            painter = painterResource(id = if (isSelected) R.drawable.ic_nav_users_filled else R.drawable.ic_nav_users_outline), 
                            contentDescription = "Social",
                            modifier = Modifier.size(26.dp),
                            tint = if (isSelected) Color(0xFF4B4FE4) else Color(0xFF9E9EB0)
                        ) 
                    }
                )
                item(
                    selected = currentKey == Destination.Campaigns,
                    colors = itemColors,
                    onClick = {
                        if (currentKey != Destination.Campaigns) {
                            backStack.clear()
                            backStack.add(Destination.Campaigns)
                        }
                    },
                    icon = { 
                        val isSelected = currentKey == Destination.Campaigns
                        Icon(
                            painter = painterResource(id = if (isSelected) R.drawable.ic_nav_megaphone_filled else R.drawable.ic_nav_megaphone_outline), 
                            contentDescription = "Campaigns",
                            modifier = Modifier.size(26.dp),
                            tint = if (isSelected) Color(0xFF4B4FE4) else Color(0xFF9E9EB0)
                        ) 
                    }
                )
                item(
                    selected = currentKey == Destination.Inbox,
                    colors = itemColors,
                    onClick = {
                        if (currentKey != Destination.Inbox) {
                            backStack.clear()
                            backStack.add(Destination.Inbox)
                        }
                    },
                    icon = { 
                        val isSelected = currentKey == Destination.Inbox
                        Icon(
                            painter = painterResource(id = if (isSelected) R.drawable.ic_nav_chat_filled else R.drawable.ic_nav_chat_outline), 
                            contentDescription = "Inbox",
                            modifier = Modifier.size(26.dp),
                            tint = if (isSelected) Color(0xFF4B4FE4) else Color(0xFF9E9EB0)
                        ) 
                    }
                )
                item(
                    selected = currentKey == Destination.Earnings,
                    colors = itemColors,
                    onClick = {
                        if (currentKey != Destination.Earnings) {
                            backStack.clear()
                            backStack.add(Destination.Earnings)
                        }
                    },
                    icon = { 
                        val isSelected = currentKey == Destination.Earnings
                        AsyncImage(
                            model = "https://picsum.photos/seed/user/100",
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, if (isSelected) Color(0xFF4B4FE4) else Color.Transparent, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                )
            }
        ) {
        NavDisplay<NavKey>(
            backStack = backStack,
            entryProvider = { key ->
                when (key) {
                    is Destination.Home -> NavEntry(key) { 
                        HomeScreen(
                            viewModel = campaignViewModel,
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
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.remove(key)
                                }
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
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.remove(key)
                                }
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
                    is Destination.Inbox -> NavEntry(key) { InboxScreen() }
                    is Destination.Social -> NavEntry(key) {
                        SocialScreen(
                            viewModel = campaignViewModel,
                            onNavigateToBrandGone = {
                                backStack.add(Destination.BrandGone)
                            },
                            onNavigateToInfluencerProfile = {
                                backStack.add(Destination.InfluencerProfile())
                            }
                        )
                    }
                    is Destination.InfluencerProfile -> NavEntry(key) {
                        InfluencerProfileScreen(
                            creatorName = key.creatorName,
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.remove(key)
                                }
                            }
                        )
                    }
                    is Destination.Earnings -> NavEntry(key) {
                        EarningsScreen(
                            viewModel = campaignViewModel,
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
                            }
                        )
                    }
                    is Destination.YourCampaigns -> NavEntry(key) {
                        YourCampaignsScreen(
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    is Destination.YourCollection -> NavEntry(key) {
                        YourCollectionScreen(
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    is Destination.YourReferrals -> NavEntry(key) {
                        YourReferralsScreen(
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    is Destination.Settings -> NavEntry(key) {
                        SettingsScreen(
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
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    is Destination.PushNotifications -> NavEntry(key) {
                        PushNotificationsScreen(
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    is Destination.EmailNotifications -> NavEntry(key) {
                        EmailNotificationsScreen(
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    is Destination.EditProfile -> NavEntry(key) {
                        EditProfileScreen(
                            onBack = {
                                if (backStack.size > 1) backStack.remove(key)
                            }
                        )
                    }
                    else -> NavEntry(key) { Text("Unknown") }
                }
            }
        )
    }
}
}
