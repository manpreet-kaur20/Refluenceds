package com.example.refluenceds.domain.repository

import com.example.refluenceds.data.remote.dto.*
import com.example.refluenceds.domain.model.AppNotification
import com.example.refluenceds.domain.model.EarningsSummary
import com.example.refluenceds.domain.model.HomeData
import com.example.refluenceds.domain.model.Product
import com.example.refluenceds.domain.model.ReferralInfo
import com.example.refluenceds.domain.model.UserProfile
import okhttp3.MultipartBody

interface AppRepository {

    // ── Auth Endpoints ────────────────────────────────────────────────────────
    suspend fun register(email: String, pass: String): Result<AuthResponseDto>

    suspend fun login(email: String, pass: String): Result<AuthResponseDto>

    suspend fun socialLogin(
        provider: String,
        providerId: String,
        email: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        deviceToken: String? = null,
        deviceType: String? = null,
        latitude: String? = null,
        longitude: String? = null
    ): Result<AuthResponseDto>

    suspend fun forgotPassword(email: String): Result<ForgotPasswordResponseDto>

    suspend fun resetPassword(
        token: String,
        email: String,
        pass: String,
        passConfirmation: String
    ): Result<String>

    suspend fun changePassword(
        currentPass: String,
        pass: String,
        passConfirmation: String
    ): Result<String>

    suspend fun logout(): Result<String>

    suspend fun getCurrentUser(): Result<UserDto>

    // ── Referral Endpoints ───────────────────────────────────────────────────
    suspend fun confirmReferral(code: String): Result<String>

    suspend fun skipReferral(): Result<String>

    suspend fun getMyReferralCode(): Result<ReferralStatsDto>

    // ── Countries & Industries & Terms ────────────────────────────────────────
    suspend fun getCountries(search: String? = null): Result<List<CountryDto>>

    suspend fun getCountryById(countryId: Int): Result<CountryDto>

    suspend fun getIndustries(): Result<List<IndustryDto>>

    suspend fun getTerms(): Result<TermsResponseDto>

    // ── Onboarding Multi-step Endpoints ──────────────────────────────────────
    suspend fun getOnboardingStatus(): Result<OnboardingStatusResponseDto>

    suspend fun submitOnboardingStep1(firstName: String): Result<OnboardingResponseDto>

    suspend fun submitOnboardingStep2(lastName: String): Result<OnboardingResponseDto>

    suspend fun submitOnboardingStep3(gender: String?, skip: Boolean = false): Result<OnboardingResponseDto>

    suspend fun submitOnboardingStep4(countryId: Int): Result<OnboardingResponseDto>

    suspend fun submitOnboardingStep5(industryIds: List<Int>): Result<OnboardingResponseDto>

    suspend fun submitOnboardingStep6(photos: List<MultipartBody.Part>): Result<OnboardingResponseDto>

    suspend fun submitOnboardingStep7(agreed: Boolean = true): Result<OnboardingResponseDto>

    // ── User & Profile Endpoints ──────────────────────────────────────────────
    suspend fun getProfile(): Result<ProfileResponseDto>

    suspend fun getProfile(userId: String): Result<UserProfile>

    suspend fun editProfile(profile: UserProfile): Result<UserProfile>

    suspend fun updateProfile(fields: Map<String, String>): Result<ProfileResponseDto>

    suspend fun updateProfileMultipart(fields: Map<String, String>, profilePicture: MultipartBody.Part? = null): Result<ProfileResponseDto>

    suspend fun updateProfileIndustries(industryIds: List<Int>): Result<ProfileResponseDto>

    suspend fun updateProfilePhotos(photos: List<MultipartBody.Part>): Result<String>

    suspend fun deleteAccount(): Result<String>

    suspend fun reportCampaign(campaignId: String, reasons: List<String>, comment: String?): Result<String>

    suspend fun toggleFavorite(campaignId: String, isFav: Boolean): Result<String>

    suspend fun followBrand(brandId: String, follow: Boolean): Result<String>

    suspend fun getReferrals(): Result<ReferralInfo>

    suspend fun getEarnings(): Result<EarningsSummary>

    suspend fun withdrawPayout(amount: Double, iban: String): Result<String>

    suspend fun getProducts(): Result<List<Product>>

    suspend fun getHomeData(): Result<HomeData>

    suspend fun getNotifications(userId: String): Result<List<AppNotification>>
 
    suspend fun submitFeedback(feedback: String): Result<String>
}
