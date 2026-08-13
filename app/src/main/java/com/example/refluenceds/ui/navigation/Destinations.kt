package com.example.refluenceds.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object Login : Destination

    @Serializable
    data object Signup : Destination

    @Serializable
    data object ForgotPassword : Destination

    @Serializable
    data object ReferralCode : Destination

    @Serializable
    data object FirstName : Destination

    @Serializable
    data object LastName : Destination

    @Serializable
    data object Gender : Destination

    @Serializable
    data object Country : Destination

    @Serializable
    data object Interests : Destination

    @Serializable
    data object ProfilePhotos : Destination

    @Serializable
    data object TermsAndConditions : Destination

    @Serializable
    data object SocialVerification : Destination

    @Serializable
    data object EmailVerification : Destination

    @Serializable
    data object Home : Destination

    @Serializable
    data object Campaigns : Destination

    @Serializable
    data object Submissions : Destination

    @Serializable
    data object Academy : Destination

    @Serializable
    data object Inbox : Destination

    @Serializable
    data object Brands : Destination

    @Serializable
    data object Social : Destination

    @Serializable
    data object Earnings : Destination

    @Serializable
    data object YourCampaigns : Destination

    @Serializable
    data object YourCollection : Destination

    @Serializable
    data object YourReferrals : Destination

    @Serializable
    data object Settings : Destination

    @Serializable
    data object LanguagePreference : Destination

    @Serializable
    data object ChangePassword : Destination

    @Serializable
    data object PushNotifications : Destination

    @Serializable
    data object EmailNotifications : Destination

    @Serializable
    data object EditProfile : Destination

    @Serializable
    data object BrandGone : Destination

    @Serializable
    data class AcademyDetail(val tutorialId: String = "1") : Destination

    @Serializable
    data class CampaignDetail(val campaignId: String = "1") : Destination

    @Serializable
    data class InfluencerProfile(val creatorName: String = "Lia") : Destination
}
