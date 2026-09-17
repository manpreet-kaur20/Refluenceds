package com.example.refluenceds.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── Auth Requests ────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class SignupRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class SocialLoginRequestDto(
    @Json(name = "provider") val provider: String,
    @Json(name = "provider_id") val providerId: String,
    @Json(name = "email") val email: String? = null,
    @Json(name = "first_name") val firstName: String? = null,
    @Json(name = "last_name") val lastName: String? = null,
    @Json(name = "device_token") val deviceToken: String? = null,
    @Json(name = "device_type") val deviceType: String? = null,
    @Json(name = "latitude") val latitude: String? = null,
    @Json(name = "longitude") val longitude: String? = null
)

@JsonClass(generateAdapter = true)
data class ForgotPasswordRequestDto(
    @Json(name = "email") val email: String
)

@JsonClass(generateAdapter = true)
data class ForgotPasswordDataDto(
    @Json(name = "reset_link") val resetLink: String? = null
)

@JsonClass(generateAdapter = true)
data class ForgotPasswordResponseDto(
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "status") val status: Any? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: ForgotPasswordDataDto? = null
) {
    fun getExtractedToken(): String? {
        val link = data?.resetLink ?: return null
        return try {
            val withoutQuery = link.substringBefore("?")
            val token = withoutQuery.substringAfterLast("/")
            if (token.isNotBlank() && token != link) token else null
        } catch (_: Exception) {
            null
        }
    }

    fun getExtractedEmail(): String? {
        val link = data?.resetLink ?: return null
        return try {
            if (link.contains("email=")) {
                val encodedEmail = link.substringAfter("email=").substringBefore("&")
                java.net.URLDecoder.decode(encodedEmail, "UTF-8")
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}

@JsonClass(generateAdapter = true)
data class ResetPasswordRequestDto(
    @Json(name = "token") val token: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "password_confirmation") val passwordConfirmation: String
)

@JsonClass(generateAdapter = true)
data class ChangePasswordRequestDto(
    @Json(name = "current_password") val currentPassword: String,
    @Json(name = "password") val password: String,
    @Json(name = "password_confirmation") val passwordConfirmation: String
)

@JsonClass(generateAdapter = true)
data class VerifyEmailRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "code") val code: String
)

@JsonClass(generateAdapter = true)
data class SocialVerificationRequestDto(
    @Json(name = "platform") val platform: String,
    @Json(name = "username") val username: String
)

// ── Referral Requests & Responses ───────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class ReferralConfirmRequestDto(
    @Json(name = "code") val code: String
)

@JsonClass(generateAdapter = true)
data class ReferralStatsNestedDto(
    @Json(name = "total_invited") val totalInvited: Int? = 0,
    @Json(name = "pending_first_application") val pendingFirstApplication: Int? = 0,
    @Json(name = "completed_rewards") val completedRewards: Int? = 0,
    @Json(name = "total_earned") val totalEarned: String? = "0.00 CHF"
)

@JsonClass(generateAdapter = true)
data class ReferralStatsDataDto(
    @Json(name = "my_referral_code") val myReferralCode: String? = null,
    @Json(name = "code") val code: String? = null,
    @Json(name = "referral_code") val referralCode: String? = null,
    @Json(name = "my_code") val myCode: String? = null,
    @Json(name = "share_url") val shareUrl: String? = null,
    @Json(name = "share_text") val shareText: String? = null,
    @Json(name = "has_applied") val hasApplied: Boolean? = null,
    @Json(name = "has_skipped") val hasSkipped: Boolean? = null,
    @Json(name = "applied_type") val appliedType: String? = null,
    @Json(name = "applied_code") val appliedCode: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "invitation_link") val invitationLink: String? = null,
    @Json(name = "invite_link") val inviteLink: String? = null,
    @Json(name = "total_invited") val totalInvited: Int? = 0,
    @Json(name = "pending_first_application") val pendingFirstApplication: Int? = 0,
    @Json(name = "rewards_earned") val rewardsEarned: String? = null,
    @Json(name = "stats") val stats: ReferralStatsNestedDto? = null
)

@JsonClass(generateAdapter = true)
data class ReferralStatsDto(
    @Json(name = "my_referral_code") val myReferralCode: String? = null,
    @Json(name = "code") val code: String? = null,
    @Json(name = "referral_code") val referralCode: String? = null,
    @Json(name = "my_code") val myCode: String? = null,
    @Json(name = "share_url") val shareUrl: String? = null,
    @Json(name = "share_text") val shareText: String? = null,
    @Json(name = "has_applied") val hasApplied: Boolean? = null,
    @Json(name = "has_skipped") val hasSkipped: Boolean? = null,
    @Json(name = "applied_type") val appliedType: String? = null,
    @Json(name = "applied_code") val appliedCode: String? = null,
    @Json(name = "status") val status: Any? = null,
    @Json(name = "invitation_link") val invitationLink: String? = null,
    @Json(name = "invite_link") val inviteLink: String? = null,
    @Json(name = "total_invited") val totalInvited: Int? = 0,
    @Json(name = "pending_first_application") val pendingFirstApplication: Int? = 0,
    @Json(name = "rewards_earned") val rewardsEarned: String? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: ReferralStatsDataDto? = null,
    @Json(name = "stats") val stats: ReferralStatsNestedDto? = null
) {
    fun getEffectiveCode(): String? = data?.myReferralCode ?: data?.code ?: data?.referralCode ?: data?.myCode ?: myReferralCode ?: code ?: referralCode ?: myCode
    fun getEffectiveShareUrl(): String? = data?.shareUrl ?: data?.inviteLink ?: data?.invitationLink ?: shareUrl ?: inviteLink ?: invitationLink
    fun getEffectiveShareText(): String? = data?.shareText ?: shareText
    fun getEffectiveTotalInvited(): Int = data?.stats?.totalInvited ?: data?.totalInvited ?: stats?.totalInvited ?: totalInvited ?: 0
    fun getEffectivePending(): Int = data?.stats?.pendingFirstApplication ?: data?.pendingFirstApplication ?: stats?.pendingFirstApplication ?: pendingFirstApplication ?: 0
    fun getEffectiveCompleted(): Int = data?.stats?.completedRewards ?: stats?.completedRewards ?: 0
    fun getEffectiveRewards(): String = data?.stats?.totalEarned ?: data?.rewardsEarned ?: stats?.totalEarned ?: rewardsEarned ?: "0.00 CHF"
}

// ── Countries & Industries ──────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class CountryDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "code") val code: String? = null,
    @Json(name = "iso2") val iso2: String? = null,
    @Json(name = "iso3") val iso3: String? = null,
    @Json(name = "phonecode") val phonecode: String? = null,
    @Json(name = "capital") val capital: String? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "currency_symbol") val currencySymbol: String? = null,
    @Json(name = "emoji") val emoji: String? = null
)

@JsonClass(generateAdapter = true)
data class CountriesResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<CountryDto>? = null
)

@JsonClass(generateAdapter = true)
data class IndustryDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "image") val image: String? = null,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "icon") val icon: String? = null
)

@JsonClass(generateAdapter = true)
data class IndustriesResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<IndustryDto>? = null
)

// ── Terms & Onboarding ──────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class TermsResponseDto(
    @Json(name = "terms_text") val termsText: String? = null,
    @Json(name = "privacy_text") val privacyText: String? = null,
    @Json(name = "terms_url") val termsUrl: String? = null,
    @Json(name = "privacy_url") val privacyUrl: String? = null,
    @Json(name = "version") val version: String? = null
)

@JsonClass(generateAdapter = true)
data class StepDetailDto(
    @Json(name = "step") val step: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "completed") val completed: Boolean? = null,
    @Json(name = "value") val value: Any? = null,
    @Json(name = "count") val count: Int? = null,
    @Json(name = "agreed") val agreed: Boolean? = null,
    @Json(name = "agreed_at") val agreedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class OnboardingStepsDto(
    @Json(name = "step_1_first_name") val step1: StepDetailDto? = null,
    @Json(name = "step_2_last_name") val step2: StepDetailDto? = null,
    @Json(name = "step_3_gender") val step3: StepDetailDto? = null,
    @Json(name = "step_4_country") val step4: StepDetailDto? = null,
    @Json(name = "step_5_industries") val step5: StepDetailDto? = null,
    @Json(name = "step_6_photos") val step6: StepDetailDto? = null,
    @Json(name = "step_7_terms") val step7: StepDetailDto? = null
)

@JsonClass(generateAdapter = true)
data class OnboardingDetailDto(
    @Json(name = "is_completed") val isCompleted: Boolean? = null,
    @Json(name = "completed_steps") val completedSteps: Int? = null,
    @Json(name = "total_steps") val totalSteps: Int? = null,
    @Json(name = "current_step") val currentStep: Int? = null,
    @Json(name = "steps") val steps: OnboardingStepsDto? = null
)

@JsonClass(generateAdapter = true)
data class ReferralDetailDto(
    @Json(name = "my_referral_code") val myReferralCode: String? = null,
    @Json(name = "my_code") val myCode: String? = null,
    @Json(name = "code") val code: String? = null,
    @Json(name = "share_url") val shareUrl: String? = null,
    @Json(name = "share_text") val shareText: String? = null,
    @Json(name = "has_applied") val hasApplied: Boolean? = null,
    @Json(name = "has_skipped") val hasSkipped: Boolean? = null,
    @Json(name = "applied_type") val appliedType: String? = null,
    @Json(name = "applied_code") val appliedCode: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "stats") val stats: ReferralStatsNestedDto? = null
) {
    fun getEffectiveCode(): String? = myReferralCode ?: myCode ?: code
}

@JsonClass(generateAdapter = true)
data class OnboardingStatusResponseDto(
    @Json(name = "completed_steps") val completedSteps: Int? = 0,
    @Json(name = "current_step") val currentStep: Int? = 1,
    @Json(name = "total_steps") val totalSteps: Int? = 7,
    @Json(name = "is_onboarding_completed") val isOnboardingCompleted: Boolean? = false
)

fun String?.normalizeImageUrl(): String? {
    if (this.isNullOrBlank()) return null
    var url = this.trim()
    if (url.contains("/refluenced/storage/") && !url.contains("/refluenced/public/storage/")) {
        url = url.replace("/refluenced/storage/", "/refluenced/public/storage/")
    }
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        val base = "http://162.241.68.61/refluenced/public/storage/"
        val clean = url.removePrefix("/").removePrefix("storage/").removePrefix("public/storage/")
        url = base + clean
    }
    return url
}

@JsonClass(generateAdapter = true)
data class PhotoDto(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "photo_url") val photoUrl: String? = null,
    @Json(name = "photo_path") val photoPath: String? = null,
    @Json(name = "order") val order: Int? = null
) {
    fun getEffectiveUrl(): String? {
        val raw = photoUrl ?: (if (!photoPath.isNullOrBlank()) "http://162.241.68.61/refluenced/public/storage/$photoPath" else null)
        return raw.normalizeImageUrl()
    }
}

// ── User & Auth Responses ───────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "first_name") val firstName: String? = null,
    @Json(name = "last_name") val lastName: String? = null,
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "profile_picture") val profilePicture: String? = null,
    @Json(name = "role") val role: String? = null,
    @Json(name = "gender") val gender: String? = null,
    @Json(name = "dob") val dob: String? = null,
    @Json(name = "bio") val bio: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "phone_number") val phoneNumber: String? = null,
    @Json(name = "phone_country") val phoneCountry: String? = null,
    @Json(name = "phone_code") val phoneCode: String? = null,
    @Json(name = "mobile_prefix") val mobilePrefix: String? = null,
    @Json(name = "street") val street: String? = null,
    @Json(name = "street_number") val streetNumber: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "postal_code") val postalCode: String? = null,
    @Json(name = "instagram") val instagram: String? = null,
    @Json(name = "tiktok") val tiktok: String? = null,
    @Json(name = "country") val country: CountryDto? = null,
    @Json(name = "country_id") val countryId: Any? = null,
    @Json(name = "industries") val industries: List<IndustryDto>? = null,
    @Json(name = "photos") val photos: List<PhotoDto>? = null,
    @Json(name = "completed_steps") val completedSteps: Int? = null,
    @Json(name = "total_steps") val totalSteps: Int? = null,
    @Json(name = "current_step") val currentStep: Int? = null,
    @Json(name = "is_onboarding_completed") val isOnboardingCompleted: Boolean? = null,
    @Json(name = "agreed_to_terms") val agreedToTerms: Boolean? = null,
    @Json(name = "agreed_at") val agreedAt: String? = null,
    @Json(name = "onboarding") val onboarding: OnboardingDetailDto? = null,
    @Json(name = "referral_code") val referralCode: String? = null,
    @Json(name = "referred_by_id") val referredById: Any? = null,
    @Json(name = "referral_code_used") val referralCodeUsed: String? = null,
    @Json(name = "referral_type") val referralType: String? = null,
    @Json(name = "referral_status") val referralStatus: String? = null,
    @Json(name = "has_applied_referral") val hasAppliedReferral: Boolean? = null,
    @Json(name = "has_skipped_referral") val hasSkippedReferral: Boolean? = null,
    @Json(name = "referral") val referral: ReferralDetailDto? = null,
    @Json(name = "theme") val theme: String? = null,
    @Json(name = "push_invitations_from_brands") val pushInvitationsFromBrands: Any? = null,
    @Json(name = "push_recommended_campaigns") val pushRecommendedCampaigns: Any? = null,
    @Json(name = "push_new_rating_received") val pushNewRatingReceived: Any? = null,
    @Json(name = "push_new_badge_received") val pushNewBadgeReceived: Any? = null,
    @Json(name = "push_accepted_to_campaign") val pushAcceptedToCampaign: Any? = null,
    @Json(name = "push_content_creation_reminder") val pushContentCreationReminder: Any? = null,
    @Json(name = "push_chat_notifications") val pushChatNotifications: Any? = null,
    @Json(name = "push_brand_has_sent_product") val pushBrandHasSentProduct: Any? = null,
    @Json(name = "email_invitations_from_brands") val emailInvitationsFromBrands: Any? = null,
    @Json(name = "email_recommended_campaigns") val emailRecommendedCampaigns: Any? = null,
    @Json(name = "email_new_rating_received") val emailNewRatingReceived: Any? = null,
    @Json(name = "email_accepted_to_campaign") val emailAcceptedToCampaign: Any? = null,
    @Json(name = "email_content_creation_reminder") val emailContentCreationReminder: Any? = null,
    @Json(name = "email_chat_notifications") val emailChatNotifications: Any? = null,
    @Json(name = "email_brand_has_sent_product") val emailBrandHasSentProduct: Any? = null,
    @Json(name = "email_newsletter") val emailNewsletter: Any? = null,
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "token_type") val tokenType: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
) {
    val avatar: String? get() = profilePicture.normalizeImageUrl() ?: photos?.firstOrNull()?.getEffectiveUrl()
    val profilePhotos: List<String>? get() = photos?.mapNotNull { it.getEffectiveUrl() }

    val isDetailsComplete: Boolean
        get() {
            val hasName = !firstName.isNullOrBlank() && !lastName.isNullOrBlank()
            val hasBirthdate = !dob.isNullOrBlank()
            val hasGender = !gender.isNullOrBlank()
            val hasPhone = !phone.isNullOrBlank() || !phoneNumber.isNullOrBlank()
            val hasAddress = !street.isNullOrBlank() || !city.isNullOrBlank() || !postalCode.isNullOrBlank()
            return hasName && hasBirthdate && hasGender && hasPhone && hasAddress
        }

    val isSocialConnected: Boolean
        get() = !instagram.isNullOrBlank() || !tiktok.isNullOrBlank()

    fun getAllPhotos(): List<String> {
        val list = mutableListOf<String>()
        val pPic = profilePicture.normalizeImageUrl()
        if (!pPic.isNullOrBlank()) {
            list.add(pPic)
        }
        photos?.forEach { p ->
            val u = p.getEffectiveUrl()
            if (!u.isNullOrBlank() && !list.contains(u)) {
                list.add(u)
            }
        }
        return list
    }

    fun toPushPreferences(): PushPreferencesDto {
        return PushPreferencesDto(
            pushInvitationsFromBrands = pushInvitationsFromBrands,
            pushRecommendedCampaigns = pushRecommendedCampaigns,
            pushNewRatingReceived = pushNewRatingReceived,
            pushNewBadgeReceived = pushNewBadgeReceived,
            pushAcceptedToCampaign = pushAcceptedToCampaign,
            pushContentCreationReminder = pushContentCreationReminder,
            pushChatNotifications = pushChatNotifications,
            pushBrandHasSentProduct = pushBrandHasSentProduct
        )
    }

    fun toEmailPreferences(): EmailPreferencesDto {
        return EmailPreferencesDto(
            emailInvitationsFromBrands = emailInvitationsFromBrands,
            emailRecommendedCampaigns = emailRecommendedCampaigns,
            emailNewRatingReceived = emailNewRatingReceived,
            emailAcceptedToCampaign = emailAcceptedToCampaign,
            emailContentCreationReminder = emailContentCreationReminder,
            emailChatNotifications = emailChatNotifications,
            emailBrandHasSentProduct = emailBrandHasSentProduct,
            emailNewsletter = emailNewsletter
        )
    }
}

@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "token") val token: String? = null,
    @Json(name = "token_type") val tokenType: String? = "Bearer",
    @Json(name = "user") val user: UserDto? = null,
    @Json(name = "data") val data: UserDto? = null,
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null
) {
    fun getEffectiveToken(): String? = accessToken ?: token ?: data?.accessToken
    fun getEffectiveUser(): UserDto? = user ?: data
}

@JsonClass(generateAdapter = true)
data class OnboardingResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "completed_steps") val completedSteps: Int? = null,
    @Json(name = "current_step") val currentStep: Int? = null,
    @Json(name = "is_onboarding_completed") val isOnboardingCompleted: Boolean? = null,
    @Json(name = "agreed_to_terms") val agreedToTerms: Boolean? = null,
    @Json(name = "agreed_at") val agreedAt: String? = null,
    @Json(name = "user") val user: UserDto? = null,
    @Json(name = "data") val data: UserDto? = null
)
