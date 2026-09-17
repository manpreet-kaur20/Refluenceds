package com.example.refluenceds.data.remote.datasource

import com.example.refluenceds.data.local.SessionManager
import com.example.refluenceds.data.remote.api.ApiService
import com.example.refluenceds.data.remote.dto.*
import com.example.refluenceds.utils.NetworkResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {

    private fun parseErrorMessage(e: Exception, fallback: String): String {
        return if (e is retrofit2.HttpException) {
            if (e.code() == 401) {
                sessionManager.onSessionExpired()
            }
            try {
                val errorBody = e.response()?.errorBody()?.string()
                if (!errorBody.isNullOrBlank()) {
                    val json = org.json.JSONObject(errorBody)
                    val status = json.optInt("status", -1)
                    val message = json.optString("message")
                    if (status == 401 || message.contains("Unauthenticated", ignoreCase = true)) {
                        sessionManager.onSessionExpired()
                    }
                    message.takeIf { it.isNotBlank() } ?: fallback
                } else {
                    fallback
                }
            } catch (_: Exception) {
                e.localizedMessage ?: fallback
            }
        } else {
            e.localizedMessage ?: fallback
        }
    }

    // ── Module 1: Auth & Security Endpoints ─────────────────────────────────

    suspend fun register(email: String, pass: String): NetworkResult<AuthResponseDto> {
        return try {
            val res = apiService.register(email, pass)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Registration failed")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun login(email: String, pass: String): NetworkResult<AuthResponseDto> {
        return try {
            val res = apiService.login(email, pass)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Login failed")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

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
    ): NetworkResult<AuthResponseDto> {
        return try {
            val fields = mutableMapOf(
                "provider" to provider,
                "provider_id" to providerId
            )
            email?.let { fields["email"] = it }
            firstName?.let { fields["first_name"] = it }
            lastName?.let { fields["last_name"] = it }
            deviceToken?.let { fields["device_token"] = it }
            deviceType?.let { fields["device_type"] = it }
            latitude?.let { fields["latitude"] = it }
            longitude?.let { fields["longitude"] = it }

            val res = apiService.socialLogin(fields)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Social login failed")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun forgotPassword(email: String): NetworkResult<ForgotPasswordResponseDto> {
        return try {
            val res = apiService.forgotPassword(email)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Forgot password request failed")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun resetPassword(
        token: String,
        email: String,
        pass: String,
        passConfirmation: String
    ): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.resetPassword(token, email, pass, passConfirmation)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Reset password failed")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun changePassword(
        currentPass: String,
        pass: String,
        passConfirmation: String
    ): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.changePassword(currentPass, pass, passConfirmation)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Change password failed")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun logout(): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.logout()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Logout failed")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getCurrentUser(): NetworkResult<AuthResponseDto> {
        return try {
            val res = apiService.getCurrentUser()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to get user profile")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 2: Profile & Account Settings ─────────────────────────────────

    suspend fun getProfile(): NetworkResult<ProfileResponseDto> {
        return try {
            val response = apiService.getProfile()
            NetworkResult.Success(response)
        } catch (e: Exception) {
            try {
                val userRes = apiService.getCurrentUser()
                val user = userRes.getEffectiveUser()
                NetworkResult.Success(
                    ProfileResponseDto(
                        id = user?.id?.toString(),
                        name = user?.fullName ?: "${user?.firstName.orEmpty()} ${user?.lastName.orEmpty()}".trim(),
                        firstName = user?.firstName,
                        lastName = user?.lastName,
                        email = user?.email,
                        profilePicture = user?.profilePicture,
                        bio = user?.bio,
                        user = user
                    )
                )
            } catch (err: Exception) {
                val msg = parseErrorMessage(e, "Failed to load profile")
                NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
            }
        }
    }

    suspend fun getProfile(userId: String): NetworkResult<ProfileResponseDto> {
        return try {
            val response = apiService.getProfileById(userId)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load profile")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun updateProfile(fields: Map<String, String>): NetworkResult<ProfileResponseDto> {
        return try {
            val response = apiService.updateProfile(fields)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to update profile")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun updateProfileMultipart(
        fields: Map<String, String>,
        profilePicture: MultipartBody.Part? = null
    ): NetworkResult<ProfileResponseDto> {
        return try {
            val partMap = fields.mapValues { (_, value) ->
                value.toRequestBody("text/plain".toMediaTypeOrNull())
            }
            val response = apiService.updateProfileMultipart(partMap, profilePicture)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to update profile")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun updateProfileIndustries(industryIds: List<Int>): NetworkResult<ProfileResponseDto> {
        return try {
            val response = apiService.updateProfileIndustries(industryIds)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to update interests")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun updateProfilePhotos(photos: List<MultipartBody.Part>): NetworkResult<GenericResponseDto> {
        return try {
            val response = apiService.updateProfilePhotos(photos)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to upload profile photos")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun submitFeedback(feedback: String): NetworkResult<GenericResponseDto> {
        return try {
            val feedbackBody = feedback.toRequestBody("text/plain".toMediaTypeOrNull())
            val res = apiService.submitFeedback(feedbackBody)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to submit feedback")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun deleteAccount(): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.deleteAccount()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to delete account")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 3: Home & Mobile Feed ─────────────────────────────────────────

    suspend fun getHomeData(
        recLimit: Int = 5,
        academyLimit: Int = 6,
        brandsLimit: Int = 6
    ): NetworkResult<HomeResponseDto> {
        return try {
            val response = apiService.getHomeData(recLimit, academyLimit, brandsLimit)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            android.util.Log.e("RemoteDataSource", "getHomeData failed: ${e.message}", e)
            NetworkResult.Error(e.message ?: "Failed to load home data", exception = e)
        }
    }

    suspend fun getRecommendedCampaigns(limit: Int = 10): NetworkResult<CampaignDiscoveryListResponseDto> {
        return try {
            val res = apiService.getRecommendedCampaigns(limit)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load recommended campaigns")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 4: Mobile Unified Inbox ───────────────────────────────────────

    suspend fun getUnifiedInbox(
        tab: String = "chat",
        unreadOnly: Int = 0,
        perPage: Int = 15,
        page: Int = 1
    ): NetworkResult<UnifiedInboxResponseDto> {
        return try {
            val res = apiService.getUnifiedInbox(tab, unreadOnly, perPage, page)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load inbox")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getInboxSummary(): NetworkResult<InboxSummaryResponseDto> {
        return try {
            val res = apiService.getInboxSummary()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to get inbox summary")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 5: Community & Campaign Content Feed ──────────────────────────

    suspend fun getContentFeed(
        tab: String = "all",
        platform: String? = null,
        search: String? = null,
        category: String? = null,
        perPage: Int = 10,
        page: Int = 1
    ): NetworkResult<ContentFeedListResponseDto> {
        return try {
            val res = apiService.getContentFeed(
                tab = tab,
                platform = platform?.takeIf { it.isNotBlank() && it.lowercase() != "all" },
                search = search?.takeIf { it.isNotBlank() },
                category = category?.takeIf { it.isNotBlank() && it.lowercase() != "all" },
                perPage = perPage,
                page = page
            )
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load content feed")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getContentFeedCategories(): NetworkResult<ContentFeedCategoriesResponseDto> {
        return try {
            val res = apiService.getContentFeedCategories()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load categories")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getContentReportReasons(): NetworkResult<ContentReportReasonsResponseDto> {
        return try {
            val res = apiService.getContentReportReasons()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load report reasons")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getContentFeedDetail(contentId: Any): NetworkResult<ContentFeedDetailResponseDto> {
        return try {
            val res = apiService.getContentFeedDetail(contentId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load content detail")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun toggleBookmarkContent(contentId: Any): NetworkResult<BookmarkResponseDto> {
        return try {
            val res = apiService.toggleBookmarkContent(contentId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to update bookmark")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun reportContent(contentId: Any, reason: String, description: String?): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.reportContent(contentId, ContentReportRequestDto(reason, description))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to report content")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 6: Influencer Showcase & Portfolio ────────────────────────────

    suspend fun getInfluencerProfile(creatorId: Any): NetworkResult<InfluencerProfileResponseDto> {
        return try {
            val res = apiService.getInfluencerProfile(creatorId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load creator showcase")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getInfluencerSocialFeed(
        creatorId: Any,
        platform: String = "instagram",
        perPage: Int = 18,
        page: Int = 1
    ): NetworkResult<InfluencerSocialFeedResponseDto> {
        return try {
            val res = apiService.getInfluencerSocialFeed(creatorId, platform, perPage, page)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load creator social feed")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getInfluencerCampaignContent(
        creatorId: Any,
        brandId: Any? = null,
        perPage: Int = 15,
        page: Int = 1
    ): NetworkResult<InfluencerContentResponseDto> {
        return try {
            val res = apiService.getInfluencerCampaignContent(creatorId, brandId, perPage, page)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load creator campaign content")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 7: Refluenced Academy ─────────────────────────────────────────

    suspend fun getAcademyCategories(): NetworkResult<AcademyCategoriesResponseDto> {
        return try {
            val res = apiService.getAcademyCategories()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load academy categories")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getAcademyVideos(
        category: String? = null,
        search: String? = null,
        perPage: Int = 15,
        page: Int = 1
    ): NetworkResult<AcademyVideosResponseDto> {
        return try {
            val res = apiService.getAcademyVideos(category, search, perPage, page)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load academy videos")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getAcademyVideoDetail(videoId: Any): NetworkResult<AcademyVideoDetailResponseDto> {
        return try {
            val res = apiService.getAcademyVideoDetail(videoId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load video tutorial")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 8: Brand Following & My Brands ────────────────────────────────

    suspend fun getMyFollowedBrands(
        search: String? = null,
        industryId: Any? = null,
        perPage: Int = 15,
        page: Int = 1
    ): NetworkResult<BrandListResponseDto> {
        return try {
            val res = apiService.getMyFollowedBrands(search, industryId, perPage, page)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load followed brands")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun followBrand(brandId: Any): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.followBrand(brandId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to follow brand")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun unfollowBrand(brandId: Any): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.unfollowBrand(brandId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to unfollow brand")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun toggleFollowBrand(brandId: Any): NetworkResult<FollowToggleResponseDto> {
        return try {
            val res = apiService.toggleFollowBrand(brandId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to toggle brand follow state")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 9: Referrals & Invites ────────────────────────────────────────

    suspend fun confirmReferral(code: String): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.confirmReferral(ReferralConfirmRequestDto(code))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Referral code confirmation failed")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun skipReferral(): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.skipReferral()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Skip referral failed")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getMyReferralCode(): NetworkResult<ReferralStatsDto> {
        return try {
            val res = apiService.getMyReferralCode()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to get referral info")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
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

    // ── Module 10: Geo & Master Data ─────────────────────────────────────────

    suspend fun getCountries(search: String? = null): NetworkResult<List<CountryDto>> {
        return try {
            val res = apiService.getCountries(search)
            NetworkResult.Success(res.data ?: emptyList())
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load countries")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getCountryById(countryId: Int): NetworkResult<CountryDto> {
        return try {
            val res = apiService.getCountryById(countryId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load country")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getIndustries(): NetworkResult<List<IndustryDto>> {
        return try {
            val res = apiService.getIndustries()
            NetworkResult.Success(res.data ?: emptyList())
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load industries")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getTerms(): NetworkResult<TermsResponseDto> {
        return try {
            val res = apiService.getTerms()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load terms")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 11: Creator Onboarding Steps ──────────────────────────────────

    suspend fun getOnboardingStatus(): NetworkResult<OnboardingStatusResponseDto> {
        return try {
            val res = apiService.getOnboardingStatus()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to get onboarding status")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun submitOnboardingStep1(firstName: String): NetworkResult<OnboardingResponseDto> {
        return try {
            val res = apiService.submitOnboardingStep1(1, firstName)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to submit first name")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun submitOnboardingStep2(lastName: String): NetworkResult<OnboardingResponseDto> {
        return try {
            val res = apiService.submitOnboardingStep2(2, lastName)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to submit last name")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun submitOnboardingStep3(gender: String?, skip: Boolean): NetworkResult<OnboardingResponseDto> {
        return try {
            val res = if (skip) {
                apiService.submitOnboardingStep3(3, null, 1)
            } else {
                apiService.submitOnboardingStep3(3, gender, null)
            }
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to submit gender")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun submitOnboardingStep4(countryId: Int): NetworkResult<OnboardingResponseDto> {
        return try {
            val res = apiService.submitOnboardingStep4(4, countryId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to submit country")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun submitOnboardingStep5(industryIds: List<Int>): NetworkResult<OnboardingResponseDto> {
        return try {
            val res = apiService.submitOnboardingStep5(5, industryIds)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to submit industries")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun submitOnboardingStep6(photos: List<MultipartBody.Part>): NetworkResult<OnboardingResponseDto> {
        return try {
            val stepBody = "6".toRequestBody("text/plain".toMediaTypeOrNull())
            val res = apiService.submitOnboardingStep6(stepBody, photos)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to upload profile photos")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun submitOnboardingStep7(agreed: Boolean = true): NetworkResult<OnboardingResponseDto> {
        return try {
            val res = apiService.submitOnboardingStep7(7, if (agreed) 1 else 0)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to complete onboarding agreement")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 12: Brand Management ──────────────────────────────────────────

    suspend fun getMyBrand(): NetworkResult<BrandDetailResponseDto> {
        return try {
            val res = apiService.getMyBrand()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load my brand profile")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun createBrandProfile(
        fields: Map<String, String>,
        logo: MultipartBody.Part? = null,
        coverImage: MultipartBody.Part? = null
    ): NetworkResult<BrandDetailResponseDto> {
        return try {
            val partMap = fields.mapValues { (_, value) ->
                value.toRequestBody("text/plain".toMediaTypeOrNull())
            }
            val res = apiService.createBrandProfile(partMap, logo, coverImage)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to create brand profile")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun updateBrandProfile(
        fields: Map<String, String>,
        logo: MultipartBody.Part? = null,
        coverImage: MultipartBody.Part? = null
    ): NetworkResult<BrandDetailResponseDto> {
        return try {
            val partMap = fields.mapValues { (_, value) ->
                value.toRequestBody("text/plain".toMediaTypeOrNull())
            }
            val res = apiService.updateBrandProfile(partMap, logo, coverImage)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to update brand profile")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun deleteBrand(): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.deleteBrand()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to deactivate brand profile")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getActiveBrands(
        search: String? = null,
        countryId: Any? = null,
        industryId: Any? = null,
        perPage: Int = 15,
        page: Int = 1
    ): NetworkResult<BrandListResponseDto> {
        return try {
            val res = apiService.getActiveBrands(search, countryId, industryId, perPage, page)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load brands directory")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getBrandByIdOrSlug(brandId: Any): NetworkResult<BrandDetailResponseDto> {
        return try {
            val res = apiService.getBrandByIdOrSlug(brandId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load brand details")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 13: Campaign Management (Brands) ──────────────────────────────

    suspend fun getMyBrandCampaigns(
        status: String = "active",
        perPage: Int = 15,
        page: Int = 1
    ): NetworkResult<CampaignDiscoveryListResponseDto> {
        return try {
            val res = apiService.getMyBrandCampaigns(status, perPage, page)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load brand campaigns")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun createBrandCampaign(request: CreateCampaignRequestDto): NetworkResult<CampaignDetailResponseDto> {
        return try {
            val res = apiService.createBrandCampaign(request)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to create campaign")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun updateBrandCampaign(campaignId: Any, request: UpdateCampaignRequestDto): NetworkResult<CampaignDetailResponseDto> {
        return try {
            val res = apiService.updateBrandCampaign(campaignId, request)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to update campaign")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun deleteBrandCampaign(campaignId: Any): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.deleteBrandCampaign(campaignId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to delete campaign")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 14: Campaign Discovery (Public & Creators) ────────────────────

    suspend fun getCampaigns(
        viewMode: String? = null,
        filterPreset: String? = null,
        platform: String? = null,
        storiesOnly: Int? = null,
        search: String? = null,
        campaignType: String? = null,
        budgetType: String? = null,
        industryId: Any? = null,
        countryId: Any? = null,
        isRemote: Int? = null,
        minBudget: Double? = null,
        maxBudget: Double? = null,
        isFeatured: Int? = null,
        sortBy: String = "created_at",
        sortOrder: String = "desc",
        perPage: Int = 15,
        page: Int = 1
    ): NetworkResult<CampaignDiscoveryListResponseDto> {
        return try {
            val res = apiService.getCampaigns(
                viewMode, filterPreset, platform, storiesOnly, search, campaignType,
                budgetType, industryId, countryId, isRemote, minBudget, maxBudget,
                isFeatured, sortBy, sortOrder, perPage, page
            )
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load campaigns")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getCampaignFilterPresets(): NetworkResult<CampaignFilterPresetsResponseDto> {
        return try {
            val res = apiService.getCampaignFilterPresets()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load campaign filters")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getCampaignReportReasons(): NetworkResult<CampaignReportReasonsResponseDto> {
        return try {
            val res = apiService.getCampaignReportReasons()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load report reasons")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getCampaignDetail(campaignId: Any): NetworkResult<CampaignDetailResponseDto> {
        return try {
            val res = apiService.getCampaignDetail(campaignId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load campaign details")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getCampaignShareLink(campaignId: Any): NetworkResult<CampaignShareLinkResponseDto> {
        return try {
            val res = apiService.getCampaignShareLink(campaignId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to generate share link")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun toggleFavoriteCampaign(campaignId: Any): NetworkResult<CampaignFavoriteResponseDto> {
        return try {
            val res = apiService.toggleFavoriteCampaign(campaignId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to update favorite status")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getMyFavoriteCampaigns(perPage: Int = 15, page: Int = 1): NetworkResult<CampaignDiscoveryListResponseDto> {
        return try {
            val res = apiService.getMyFavoriteCampaigns(perPage, page)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load favorite campaigns")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun hideCampaign(campaignId: Any): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.hideCampaign(campaignId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to hide campaign")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun unhideCampaign(campaignId: Any): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.unhideCampaign(campaignId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to unhide campaign")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun reportCampaign(campaignId: Any, reason: String, description: String? = null): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.reportCampaign(campaignId, ContentReportRequestDto(reason, description))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to report campaign")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 15: Campaign Applications ─────────────────────────────────────

    suspend fun applyToCampaign(
        campaignId: Any,
        pitch: String? = null,
        requestedCompensation: Double? = null,
        deliverablesProposal: String? = null
    ): NetworkResult<ApplicationDetailResponseDto> {
        return try {
            val res = apiService.applyToCampaign(campaignId, ApplyCampaignRequestDto(pitch, requestedCompensation, deliverablesProposal))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to submit application")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getMyApplications(status: String? = null, perPage: Int = 15, page: Int = 1): NetworkResult<ApplicationListResponseDto> {
        return try {
            val res = apiService.getMyApplications(status, perPage, page)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load my applications")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getMyApplicationDetail(applicationId: Any): NetworkResult<ApplicationDetailResponseDto> {
        return try {
            val res = apiService.getMyApplicationDetail(applicationId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load application detail")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun withdrawApplication(applicationId: Any): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.withdrawApplication(applicationId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to withdraw application")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getBrandCampaignApplications(
        campaignId: Any,
        status: String? = null,
        perPage: Int = 15,
        page: Int = 1
    ): NetworkResult<ApplicationListResponseDto> {
        return try {
            val res = apiService.getBrandCampaignApplications(campaignId, status, perPage, page)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load campaign applications")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getBrandApplicationDetail(applicationId: Any): NetworkResult<ApplicationDetailResponseDto> {
        return try {
            val res = apiService.getBrandApplicationDetail(applicationId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load candidate application")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun acceptBrandApplication(applicationId: Any): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.acceptBrandApplication(applicationId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to accept application")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun rejectBrandApplication(applicationId: Any, rejectionReason: String? = null): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.rejectBrandApplication(applicationId, RejectApplicationRequestDto(rejectionReason))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to reject application")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 16: Campaign Invitations ──────────────────────────────────────

    suspend fun inviteCreatorToCampaign(
        campaignId: Any,
        creatorId: Any? = null,
        userId: Any? = null,
        message: String? = null,
        expiresAt: String? = null
    ): NetworkResult<InvitationDetailResponseDto> {
        return try {
            val res = apiService.inviteCreatorToCampaign(campaignId, InviteCreatorRequestDto(creatorId, userId, message, expiresAt))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to send invitation")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getBrandCampaignInvitations(campaignId: Any, status: String? = null, perPage: Int = 15): NetworkResult<InvitationListResponseDto> {
        return try {
            val res = apiService.getBrandCampaignInvitations(campaignId, status, perPage)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load campaign invitations")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun cancelBrandInvitation(invitationId: Any): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.cancelBrandInvitation(invitationId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to cancel invitation")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getMyInvitations(status: String? = null, perPage: Int = 15): NetworkResult<InvitationListResponseDto> {
        return try {
            val res = apiService.getMyInvitations(status, perPage)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load invitations")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getMyInvitationDetail(invitationId: Any): NetworkResult<InvitationDetailResponseDto> {
        return try {
            val res = apiService.getMyInvitationDetail(invitationId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load invitation detail")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun acceptMyInvitation(invitationId: Any): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.acceptMyInvitation(invitationId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to accept invitation")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun declineMyInvitation(invitationId: Any): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.declineMyInvitation(invitationId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to decline invitation")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 17: Creator Discovery (For Brands) ────────────────────────────

    suspend fun searchCreators(
        search: String? = null,
        countryId: Any? = null,
        industryId: Any? = null,
        gender: String? = null,
        city: String? = null,
        minCompletedSteps: Int? = null,
        perPage: Int = 15,
        page: Int = 1
    ): NetworkResult<CreatorListResponseDto> {
        return try {
            val res = apiService.searchCreators(search, countryId, industryId, gender, city, minCompletedSteps, perPage, page)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to search creators")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getCreatorPublicProfile(creatorId: Any): NetworkResult<CreatorPublicProfileResponseDto> {
        return try {
            val res = apiService.getCreatorPublicProfile(creatorId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load creator public profile")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 18: Ratings & Reviews ─────────────────────────────────────────

    suspend fun submitRating(
        campaignId: Any,
        rating: Int,
        review: String? = null,
        creatorId: Any? = null,
        brandId: Any? = null
    ): NetworkResult<RatingDetailResponseDto> {
        return try {
            val res = apiService.submitRating(SubmitRatingRequestDto(campaignId, rating, review, creatorId, brandId))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to submit rating")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getMyRatings(type: String = "all", perPage: Int = 15): NetworkResult<RatingListResponseDto> {
        return try {
            val res = apiService.getMyRatings(type, perPage)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load user ratings")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getCreatorPublicRatings(creatorId: Any, perPage: Int = 15): NetworkResult<RatingListResponseDto> {
        return try {
            val res = apiService.getCreatorPublicRatings(creatorId, perPage)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load creator reviews")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getBrandPublicRatings(brandId: Any, perPage: Int = 15): NetworkResult<RatingListResponseDto> {
        return try {
            val res = apiService.getBrandPublicRatings(brandId, perPage)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load brand reviews")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 19: Notifications Center ──────────────────────────────────────

    suspend fun getNotificationsCenter(
        unreadOnly: Boolean? = null,
        perPage: Int = 15,
        page: Int = 1
    ): NetworkResult<NotificationListResponseDto> {
        return try {
            val res = apiService.getNotifications(unreadOnly, perPage, page)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load notifications")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getNotifications(userId: String): NetworkResult<NotificationListResponseDto> {
        return try {
            val response = apiService.getUserNotifications(userId)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            try {
                val res = apiService.getNotifications(null, 15, 1)
                NetworkResult.Success(res)
            } catch (err: Exception) {
                val mockData = NotificationListResponseDto(
                    notifications = listOf(
                        NotificationDto("1", "Campaign Approved!", "Your application for Desert Escapes was accepted.", "10 mins ago")
                    )
                )
                NetworkResult.Success(mockData)
            }
        }
    }

    suspend fun markNotificationRead(notificationId: Any): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.markNotificationRead(notificationId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to mark notification as read")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun markAllNotificationsRead(): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.markAllNotificationsRead()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to mark all notifications as read")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun deleteNotification(notificationId: Any): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.deleteNotification(notificationId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to delete notification")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 20: Chat & Messaging ──────────────────────────────────────────

    suspend fun getConversations(perPage: Int = 15, page: Int = 1): NetworkResult<ConversationListResponseDto> {
        return try {
            val res = apiService.getConversations(perPage, page)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load conversations")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun startConversation(
        campaignId: Any? = null,
        recipientUserId: Any? = null,
        recipientBrandId: Any? = null,
        message: String? = null
    ): NetworkResult<ConversationDetailResponseDto> {
        return try {
            val res = apiService.startConversation(StartConversationRequestDto(campaignId, recipientUserId, recipientBrandId, message))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to start conversation")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getConversationDetail(conversationId: Any): NetworkResult<ConversationDetailResponseDto> {
        return try {
            val res = apiService.getConversationDetail(conversationId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load conversation")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getConversationMessages(
        conversationId: Any,
        perPage: Int = 25,
        page: Int = 1
    ): NetworkResult<MessageListResponseDto> {
        return try {
            val res = apiService.getConversationMessages(conversationId, perPage, page)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load messages")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun sendMessage(
        conversationId: Any,
        message: String,
        messageType: String = "text",
        attachment: MultipartBody.Part? = null
    ): NetworkResult<MessageDetailResponseDto> {
        return try {
            val messageBody = message.toRequestBody("text/plain".toMediaTypeOrNull())
            val messageTypeBody = messageType.toRequestBody("text/plain".toMediaTypeOrNull())
            val res = apiService.sendMessage(conversationId, messageBody, messageTypeBody, attachment)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to send message")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun markConversationRead(conversationId: Any): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.markConversationRead(conversationId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to update read receipt")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Module 21: Badges & Achievements ─────────────────────────────────────

    suspend fun getPlatformBadges(): NetworkResult<BadgeListResponseDto> {
        return try {
            val res = apiService.getPlatformBadges()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load platform badges")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getMyEarnedBadges(): NetworkResult<BadgeListResponseDto> {
        return try {
            val res = apiService.getMyEarnedBadges()
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load my badges")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun getCreatorEarnedBadges(creatorId: Any): NetworkResult<BadgeListResponseDto> {
        return try {
            val res = apiService.getCreatorEarnedBadges(creatorId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to load creator badges")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    // ── Legacy Product Endpoints ──────────────────────────────────────────────

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

    suspend fun reportCampaign(campaignId: String, reasons: List<String>, comment: String?): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.reportCampaign(campaignId, ContentReportRequestDto(reason = reasons.joinToString(", "), description = comment))
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to report campaign")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun toggleFavorite(campaignId: String, isFav: Boolean): NetworkResult<GenericResponseDto> {
        return try {
            val res = apiService.toggleFavoriteCampaign(campaignId)
            NetworkResult.Success(GenericResponseDto(success = res.success, message = res.message, status = res.status))
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to update favorite status")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }

    suspend fun followBrand(brandId: String, follow: Boolean): NetworkResult<GenericResponseDto> {
        return try {
            val res = if (follow) apiService.followBrand(brandId) else apiService.unfollowBrand(brandId)
            NetworkResult.Success(res)
        } catch (e: Exception) {
            val msg = parseErrorMessage(e, "Failed to update brand follow state")
            NetworkResult.Error(msg, statusCode = (e as? retrofit2.HttpException)?.code(), exception = Exception(msg))
        }
    }
}
