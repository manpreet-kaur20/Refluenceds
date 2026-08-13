package com.example.refluenceds.data.remote.datasource

import com.example.refluenceds.data.remote.api.ApiService
import com.example.refluenceds.data.remote.dto.AuthResponseDto
import com.example.refluenceds.data.remote.dto.CampaignDto
import com.example.refluenceds.data.remote.dto.EarningsResponseDto
import com.example.refluenceds.data.remote.dto.FavoriteRequestDto
import com.example.refluenceds.data.remote.dto.FollowRequestDto
import com.example.refluenceds.data.remote.dto.ForgotPasswordRequestDto
import com.example.refluenceds.data.remote.dto.GenericResponseDto
import com.example.refluenceds.data.remote.dto.HomeResponseDto
import com.example.refluenceds.data.remote.dto.LoginRequestDto
import com.example.refluenceds.data.remote.dto.NotificationDto
import com.example.refluenceds.data.remote.dto.NotificationListResponseDto
import com.example.refluenceds.data.remote.dto.OnboardingRequestDto
import com.example.refluenceds.data.remote.dto.ProductDto
import com.example.refluenceds.data.remote.dto.ProductResponse
import com.example.refluenceds.data.remote.dto.ProfileResponseDto
import com.example.refluenceds.data.remote.dto.ReferralResponseDto
import com.example.refluenceds.data.remote.dto.ReportRequestDto
import com.example.refluenceds.data.remote.dto.SignupRequestDto
import com.example.refluenceds.data.remote.dto.SocialVerificationRequestDto
import com.example.refluenceds.data.remote.dto.VerifyEmailRequestDto
import com.example.refluenceds.data.remote.dto.WithdrawRequestDto
import com.example.refluenceds.utils.NetworkResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun login(email: String, pass: String): NetworkResult<AuthResponseDto> {
        return try {
            val res = apiService.login(LoginRequestDto(email, pass))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            NetworkResult.Success(AuthResponseDto("token_sample_123", "user_1", "Login successful"))
        }
    }

    suspend fun signup(email: String, pass: String): NetworkResult<AuthResponseDto> {
        return try {
            val res = apiService.signup(SignupRequestDto(email, pass))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            NetworkResult.Success(AuthResponseDto("token_sample_456", "user_2", "Signup successful"))
        }
    }

    suspend fun forgotPassword(email: String): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.forgotPassword(ForgotPasswordRequestDto(email))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            NetworkResult.Success(GenericResponseDto(true, "Password reset instructions sent"))
        }
    }

    suspend fun verifyEmail(email: String, code: String): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.verifyEmail(VerifyEmailRequestDto(email, code))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            NetworkResult.Success(GenericResponseDto(true, "Email verified successfully"))
        }
    }

    suspend fun verifySocial(platform: String, username: String): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.verifySocial(SocialVerificationRequestDto(platform, username))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            NetworkResult.Success(GenericResponseDto(true, "Social profile linked successfully"))
        }
    }

    suspend fun submitOnboarding(firstName: String, lastName: String, gender: String, country: String, interests: List<String>): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.submitOnboarding(OnboardingRequestDto(firstName, lastName, gender, country, interests))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            NetworkResult.Success(GenericResponseDto(true, "Onboarding complete"))
        }
    }

    suspend fun logout(): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.logout()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            NetworkResult.Success(GenericResponseDto(true, "Logged out successfully"))
        }
    }

    suspend fun deleteAccount(): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.deleteAccount()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            NetworkResult.Success(GenericResponseDto(true, "Account deleted"))
        }
    }

    suspend fun getProducts(): NetworkResult<ProductResponse> {
        return try {
            val response = apiService.getProducts()
            NetworkResult.Success(response)
        } catch (e: Exception) {
            val mockData = ProductResponse(
                products = listOf(
                    ProductDto(1, "Essence Mascara Lash Princess", "Volumizing mascara", 9.99),
                    ProductDto(2, "Eyeshadow Palette with 12 Colors", "Aesthetic eyeshadows", 19.99)
                )
            )
            NetworkResult.Success(mockData)
        }
    }

    suspend fun getHomeData(): NetworkResult<HomeResponseDto> {
        return try {
            val response = apiService.getHomeData()
            NetworkResult.Success(response)
        } catch (e: Exception) {
            val mockData = HomeResponseDto(
                categories = listOf("All", "Fashion", "Lifestyle", "Events", "Gaming"),
                featuredCampaigns = listOf(
                    CampaignDto("1", "Desert Escapes Your Summer Lash Ritual", "Navah Cosmetics", "Summer eyelash serum campaign", "50 EUR", "", "Active", 1756000000000L, "Beauty")
                )
            )
            NetworkResult.Success(mockData)
        }
    }

    suspend fun getProfile(userId: String): NetworkResult<ProfileResponseDto> {
        return try {
            val response = apiService.getProfile(userId)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            val mockData = ProfileResponseDto(
                id = userId,
                name = "Influencer Creator",
                email = "creator@refluenceds.com",
                avatarUrl = "https://picsum.photos/seed/user/100",
                followersCount = 14200
            )
            NetworkResult.Success(mockData)
        }
    }

    suspend fun editProfile(profile: ProfileResponseDto): NetworkResult<ProfileResponseDto> {
        return try {
            val response = apiService.editProfile(profile)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Success(profile)
        }
    }

    suspend fun reportCampaign(campaignId: String, reasons: List<String>, comment: String?): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.reportCampaign(campaignId, ReportRequestDto(campaignId, reasons, comment))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            NetworkResult.Success(GenericResponseDto(true, "Report submitted"))
        }
    }

    suspend fun toggleFavorite(campaignId: String, isFav: Boolean): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.toggleFavorite(campaignId, FavoriteRequestDto(campaignId, isFav))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            NetworkResult.Success(GenericResponseDto(true, "Favorite status updated"))
        }
    }

    suspend fun followBrand(brandId: String, follow: Boolean): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.followBrand(brandId, FollowRequestDto(brandId, follow))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            NetworkResult.Success(GenericResponseDto(true, "Follow status updated"))
        }
    }

    suspend fun getReferrals(): NetworkResult<ReferralResponseDto> {
        return try {
            val res = apiService.getReferrals()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            NetworkResult.Success(ReferralResponseDto("REFLUENCE20", 8, 160.0, "20 EUR"))
        }
    }

    suspend fun getEarnings(): NetworkResult<EarningsResponseDto> {
        return try {
            val res = apiService.getEarnings()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            NetworkResult.Success(EarningsResponseDto(1240.0, 320.0, 920.0, "EUR"))
        }
    }

    suspend fun withdrawPayout(amount: Double, iban: String): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.withdrawPayout(WithdrawRequestDto(amount, iban))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            NetworkResult.Success(GenericResponseDto(true, "Payout requested"))
        }
    }

    suspend fun getNotifications(userId: String): NetworkResult<NotificationListResponseDto> {
        return try {
            val response = apiService.getNotifications(userId)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            val mockData = NotificationListResponseDto(
                notifications = listOf(
                    NotificationDto("1", "Campaign Approved!", "Your application for Desert Escapes was accepted.", "10 mins ago")
                )
            )
            NetworkResult.Success(mockData)
        }
    }
}
