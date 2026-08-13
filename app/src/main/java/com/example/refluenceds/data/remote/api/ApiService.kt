package com.example.refluenceds.data.remote.api

import com.example.refluenceds.data.remote.dto.AuthResponseDto
import com.example.refluenceds.data.remote.dto.CampaignDto
import com.example.refluenceds.data.remote.dto.EarningsResponseDto
import com.example.refluenceds.data.remote.dto.FavoriteRequestDto
import com.example.refluenceds.data.remote.dto.FollowRequestDto
import com.example.refluenceds.data.remote.dto.ForgotPasswordRequestDto
import com.example.refluenceds.data.remote.dto.GenericResponseDto
import com.example.refluenceds.data.remote.dto.HomeResponseDto
import com.example.refluenceds.data.remote.dto.LoginRequestDto
import com.example.refluenceds.data.remote.dto.NotificationListResponseDto
import com.example.refluenceds.data.remote.dto.OnboardingRequestDto
import com.example.refluenceds.data.remote.dto.ProductResponse
import com.example.refluenceds.data.remote.dto.ProfileResponseDto
import com.example.refluenceds.data.remote.dto.ReferralResponseDto
import com.example.refluenceds.data.remote.dto.ReportRequestDto
import com.example.refluenceds.data.remote.dto.SignupRequestDto
import com.example.refluenceds.data.remote.dto.SocialVerificationRequestDto
import com.example.refluenceds.data.remote.dto.VerifyEmailRequestDto
import com.example.refluenceds.data.remote.dto.WithdrawRequestDto
import com.example.refluenceds.utils.Constants
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ── Auth Endpoints ────────────────────────────────────────────────────────
    @POST(Constants.LOGIN_ENDPOINT)
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST(Constants.SIGNUP_ENDPOINT)
    suspend fun signup(@Body request: SignupRequestDto): AuthResponseDto

    @POST(Constants.FORGOT_PASSWORD_ENDPOINT)
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto): GenericResponseDto

    @POST(Constants.EMAIL_VERIFY_ENDPOINT)
    suspend fun verifyEmail(@Body request: VerifyEmailRequestDto): GenericResponseDto

    @POST(Constants.SOCIAL_VERIFY_ENDPOINT)
    suspend fun verifySocial(@Body request: SocialVerificationRequestDto): GenericResponseDto

    @POST(Constants.LOGOUT_ENDPOINT)
    suspend fun logout(): GenericResponseDto

    // ── User & Profile Endpoints ──────────────────────────────────────────────
    @POST(Constants.ONBOARDING_ENDPOINT)
    suspend fun submitOnboarding(@Body request: OnboardingRequestDto): GenericResponseDto

    @GET(Constants.PROFILE_ENDPOINT)
    suspend fun getProfile(@Query("userId") userId: String): ProfileResponseDto

    @PUT(Constants.EDIT_PROFILE_ENDPOINT)
    suspend fun editProfile(@Body request: ProfileResponseDto): ProfileResponseDto

    @DELETE(Constants.DELETE_ACCOUNT_ENDPOINT)
    suspend fun deleteAccount(): GenericResponseDto

    @GET(Constants.NOTIFICATIONS_ENDPOINT)
    suspend fun getNotifications(@Query("userId") userId: String): NotificationListResponseDto

    // ── Home & Sample Products Endpoints ──────────────────────────────────────
    @GET(Constants.HOME_ENDPOINT)
    suspend fun getHomeData(): HomeResponseDto

    @GET(Constants.PRODUCTS_ENDPOINT)
    suspend fun getProducts(): ProductResponse

    // ── Campaigns & Interactive Actions ───────────────────────────────────────
    @GET(Constants.CAMPAIGNS_ENDPOINT)
    suspend fun getCampaigns(): List<CampaignDto>

    @GET(Constants.CAMPAIGN_DETAIL_ENDPOINT)
    suspend fun getCampaignDetail(@Path("id") campaignId: String): CampaignDto

    @POST(Constants.FAVORITE_CAMPAIGN_ENDPOINT)
    suspend fun toggleFavorite(@Path("id") campaignId: String, @Body request: FavoriteRequestDto): GenericResponseDto

    @POST(Constants.REPORT_CAMPAIGN_ENDPOINT)
    suspend fun reportCampaign(@Path("id") campaignId: String, @Body request: ReportRequestDto): GenericResponseDto

    // ── Brands Endpoints ──────────────────────────────────────────────────────
    @POST(Constants.FOLLOW_BRAND_ENDPOINT)
    suspend fun followBrand(@Path("id") brandId: String, @Body request: FollowRequestDto): GenericResponseDto

    // ── Referrals & Earnings Endpoints ───────────────────────────────────────
    @GET(Constants.REFERRALS_ENDPOINT)
    suspend fun getReferrals(): ReferralResponseDto

    @GET(Constants.EARNINGS_ENDPOINT)
    suspend fun getEarnings(): EarningsResponseDto

    @POST(Constants.PAYOUT_WITHDRAW_ENDPOINT)
    suspend fun withdrawPayout(@Body request: WithdrawRequestDto): GenericResponseDto
}
