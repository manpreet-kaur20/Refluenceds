package com.example.refluenceds.data.repository

import com.example.refluenceds.data.remote.datasource.RemoteDataSource
import com.example.refluenceds.data.remote.dto.*
import com.example.refluenceds.domain.model.*
import com.example.refluenceds.domain.repository.AppRepository
import com.example.refluenceds.utils.NetworkResult
import okhttp3.MultipartBody
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : AppRepository {

    override suspend fun register(email: String, pass: String): Result<AuthResponseDto> {
        return when (val res = remoteDataSource.register(email, pass)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun login(email: String, pass: String): Result<AuthResponseDto> {
        return when (val res = remoteDataSource.login(email, pass)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun socialLogin(
        provider: String,
        providerId: String,
        email: String?,
        firstName: String?,
        lastName: String?,
        deviceToken: String?,
        deviceType: String?,
        latitude: String?,
        longitude: String?
    ): Result<AuthResponseDto> {
        return when (val res = remoteDataSource.socialLogin(
            provider, providerId, email, firstName, lastName, deviceToken, deviceType, latitude, longitude
        )) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun forgotPassword(email: String): Result<ForgotPasswordResponseDto> {
        return when (val res = remoteDataSource.forgotPassword(email)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun resetPassword(
        token: String,
        email: String,
        pass: String,
        passConfirmation: String
    ): Result<String> {
        return when (val res = remoteDataSource.resetPassword(token, email, pass, passConfirmation)) {
            is NetworkResult.Success -> Result.success(res.data.message ?: "Password reset successful")
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun changePassword(
        currentPass: String,
        pass: String,
        passConfirmation: String
    ): Result<String> {
        return when (val res = remoteDataSource.changePassword(currentPass, pass, passConfirmation)) {
            is NetworkResult.Success -> Result.success(res.data.message ?: "Password changed successfully")
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun logout(): Result<String> {
        return when (val res = remoteDataSource.logout()) {
            is NetworkResult.Success -> Result.success(res.data.message ?: "Logged out successfully")
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getCurrentUser(): Result<UserDto> {
        return when (val res = remoteDataSource.getCurrentUser()) {
            is NetworkResult.Success -> {
                val user = res.data.getEffectiveUser()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("User data not found"))
                }
            }
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun confirmReferral(code: String): Result<String> {
        return when (val res = remoteDataSource.confirmReferral(code)) {
            is NetworkResult.Success -> Result.success(res.data.message ?: "Referral confirmed")
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun skipReferral(): Result<String> {
        return when (val res = remoteDataSource.skipReferral()) {
            is NetworkResult.Success -> Result.success(res.data.message ?: "Referral skipped")
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getMyReferralCode(): Result<ReferralStatsDto> {
        return when (val res = remoteDataSource.getMyReferralCode()) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getCountries(search: String?): Result<List<CountryDto>> {
        return when (val res = remoteDataSource.getCountries(search)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getCountryById(countryId: Int): Result<CountryDto> {
        return when (val res = remoteDataSource.getCountryById(countryId)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getIndustries(): Result<List<IndustryDto>> {
        return when (val res = remoteDataSource.getIndustries()) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getTerms(): Result<TermsResponseDto> {
        return when (val res = remoteDataSource.getTerms()) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getOnboardingStatus(): Result<OnboardingStatusResponseDto> {
        return when (val res = remoteDataSource.getOnboardingStatus()) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun submitOnboardingStep1(firstName: String): Result<OnboardingResponseDto> {
        return when (val res = remoteDataSource.submitOnboardingStep1(firstName)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun submitOnboardingStep2(lastName: String): Result<OnboardingResponseDto> {
        return when (val res = remoteDataSource.submitOnboardingStep2(lastName)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun submitOnboardingStep3(gender: String?, skip: Boolean): Result<OnboardingResponseDto> {
        return when (val res = remoteDataSource.submitOnboardingStep3(gender, skip)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun submitOnboardingStep4(countryId: Int): Result<OnboardingResponseDto> {
        return when (val res = remoteDataSource.submitOnboardingStep4(countryId)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun submitOnboardingStep5(industryIds: List<Int>): Result<OnboardingResponseDto> {
        return when (val res = remoteDataSource.submitOnboardingStep5(industryIds)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun submitOnboardingStep6(photos: List<MultipartBody.Part>): Result<OnboardingResponseDto> {
        return when (val res = remoteDataSource.submitOnboardingStep6(photos)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun submitOnboardingStep7(agreed: Boolean): Result<OnboardingResponseDto> {
        return when (val res = remoteDataSource.submitOnboardingStep7(agreed)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    // ── User & Profile Endpoints ──────────────────────────────────────────────

    override suspend fun deleteAccount(): Result<String> {
        return when (val res = remoteDataSource.deleteAccount()) {
            is NetworkResult.Success -> Result.success(res.data.message ?: "Account deleted")
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getProducts(): Result<List<Product>> {
        return when (val result = remoteDataSource.getProducts()) {
            is NetworkResult.Success -> Result.success(result.data.products.map { it.toDomain() })
            is NetworkResult.Error -> Result.failure(result.exception ?: Exception(result.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getHomeData(): Result<HomeData> {
        return when (val result = remoteDataSource.getHomeData()) {
            is NetworkResult.Success -> Result.success(result.data.toDomain())
            is NetworkResult.Error -> Result.failure(result.exception ?: Exception(result.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getProfile(): Result<ProfileResponseDto> {
        return when (val res = remoteDataSource.getProfile()) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getProfile(userId: String): Result<UserProfile> {
        return when (val result = remoteDataSource.getProfile(userId)) {
            is NetworkResult.Success -> Result.success(result.data.toDomain())
            is NetworkResult.Error -> Result.failure(result.exception ?: Exception(result.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun editProfile(profile: UserProfile): Result<UserProfile> {
        val fields = mutableMapOf<String, String>()
        fields["name"] = profile.name
        fields["email"] = profile.email
        return when (val res = remoteDataSource.updateProfile(fields)) {
            is NetworkResult.Success -> Result.success(res.data.toDomain())
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun updateProfile(fields: Map<String, String>): Result<ProfileResponseDto> {
        return when (val res = remoteDataSource.updateProfile(fields)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun updateProfileMultipart(
        fields: Map<String, String>,
        profilePicture: MultipartBody.Part?
    ): Result<ProfileResponseDto> {
        return when (val res = remoteDataSource.updateProfileMultipart(fields, profilePicture)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun updateProfileIndustries(industryIds: List<Int>): Result<ProfileResponseDto> {
        return when (val res = remoteDataSource.updateProfileIndustries(industryIds)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun updateProfilePhotos(photos: List<MultipartBody.Part>): Result<String> {
        return when (val res = remoteDataSource.updateProfilePhotos(photos)) {
            is NetworkResult.Success -> Result.success(res.data.message ?: "Photos updated successfully")
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun reportCampaign(campaignId: String, reasons: List<String>, comment: String?): Result<String> {
        return when (val res = remoteDataSource.reportCampaign(campaignId, reasons, comment)) {
            is NetworkResult.Success -> Result.success(res.data.message ?: "Report submitted")
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun toggleFavorite(campaignId: String, isFav: Boolean): Result<String> {
        return when (val res = remoteDataSource.toggleFavorite(campaignId, isFav)) {
            is NetworkResult.Success -> Result.success(res.data.message ?: "Favorite status updated")
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun followBrand(brandId: String, follow: Boolean): Result<String> {
        return when (val res = remoteDataSource.followBrand(brandId, follow)) {
            is NetworkResult.Success -> Result.success(res.data.message ?: "Follow status updated")
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getReferrals(): Result<ReferralInfo> {
        return when (val res = remoteDataSource.getReferrals()) {
            is NetworkResult.Success -> Result.success(res.data.toDomain())
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getEarnings(): Result<EarningsSummary> {
        return when (val res = remoteDataSource.getEarnings()) {
            is NetworkResult.Success -> Result.success(res.data.toDomain())
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun withdrawPayout(amount: Double, iban: String): Result<String> {
        return when (val res = remoteDataSource.withdrawPayout(amount, iban)) {
            is NetworkResult.Success -> Result.success(res.data.message ?: "Payout requested")
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getNotifications(userId: String): Result<List<AppNotification>> {
        return when (val result = remoteDataSource.getNotifications(userId)) {
            is NetworkResult.Success -> Result.success(result.data.getEffectiveList().map { it.toDomain() })
            is NetworkResult.Error -> Result.failure(result.exception ?: Exception(result.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun submitFeedback(feedback: String): Result<String> {
        return when (val res = remoteDataSource.submitFeedback(feedback)) {
            is NetworkResult.Success -> Result.success(res.data.message ?: "Feedback submitted successfully")
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }
}
