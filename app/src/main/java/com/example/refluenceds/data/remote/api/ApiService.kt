package com.example.refluenceds.data.remote.api

import com.example.refluenceds.data.remote.dto.*
import com.example.refluenceds.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    // ── Module 1: Authentication & Security ─────────────────────────────────
    @FormUrlEncoded
    @POST(Constants.REGISTER_ENDPOINT)
    suspend fun register(
        @Field("email") email: String,
        @Field("password") pass: String
    ): AuthResponseDto

    @FormUrlEncoded
    @POST(Constants.LOGIN_ENDPOINT)
    suspend fun login(
        @Field("email") email: String,
        @Field("password") pass: String
    ): AuthResponseDto

    @POST(Constants.LOGIN_ENDPOINT)
    suspend fun loginWithJson(
        @Body request: LoginRequestDto
    ): AuthResponseDto

    @POST(Constants.REGISTER_ENDPOINT)
    suspend fun registerWithJson(
        @Body request: SignupRequestDto
    ): AuthResponseDto

    @FormUrlEncoded
    @POST(Constants.SOCIAL_LOGIN_ENDPOINT)
    suspend fun socialLogin(
        @FieldMap fields: Map<String, String>
    ): AuthResponseDto

    @POST(Constants.SOCIAL_LOGIN_ENDPOINT)
    suspend fun socialLoginWithJson(
        @Body request: SocialLoginRequestDto
    ): AuthResponseDto

    @GET(Constants.CURRENT_USER_ENDPOINT)
    suspend fun getCurrentUser(): AuthResponseDto

    @FormUrlEncoded
    @POST(Constants.FORGOT_PASSWORD_ENDPOINT)
    suspend fun forgotPassword(
        @Field("email") email: String
    ): ForgotPasswordResponseDto

    @POST(Constants.FORGOT_PASSWORD_ENDPOINT)
    suspend fun forgotPasswordWithJson(
        @Body request: ForgotPasswordRequestDto
    ): ForgotPasswordResponseDto

    @FormUrlEncoded
    @POST(Constants.RESET_PASSWORD_ENDPOINT)
    suspend fun resetPassword(
        @Field("token") token: String,
        @Field("email") email: String,
        @Field("password") pass: String,
        @Field("password_confirmation") passConfirmation: String
    ): GenericResponseDto

    @POST(Constants.RESET_PASSWORD_ENDPOINT)
    suspend fun resetPasswordWithJson(
        @Body request: ResetPasswordRequestDto
    ): GenericResponseDto

    @POST(Constants.LOGOUT_ENDPOINT)
    suspend fun logout(): GenericResponseDto

    // ── Module 2: Profile & Account Settings ─────────────────────────────────
    @GET(Constants.PROFILE_ENDPOINT)
    suspend fun getProfile(): ProfileResponseDto

    @GET(Constants.PROFILE_ENDPOINT)
    suspend fun getProfileById(@Query("userId") userId: String): ProfileResponseDto

    @FormUrlEncoded
    @POST(Constants.EDIT_PROFILE_ENDPOINT)
    suspend fun updateProfile(
        @FieldMap fields: Map<String, String>
    ): ProfileResponseDto

    @Multipart
    @POST(Constants.EDIT_PROFILE_ENDPOINT)
    suspend fun updateProfileMultipart(
        @PartMap fields: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part profilePicture: MultipartBody.Part? = null
    ): ProfileResponseDto

    @FormUrlEncoded
    @POST(Constants.EDIT_PROFILE_ENDPOINT)
    suspend fun updateProfileIndustries(
        @Field("industry_ids[]") industryIds: List<Int>
    ): ProfileResponseDto

    @Multipart
    @POST("profile/photos")
    suspend fun updateProfilePhotos(
        @Part photos: List<MultipartBody.Part>
    ): GenericResponseDto

    @FormUrlEncoded
    @POST(Constants.CHANGE_PASSWORD_ENDPOINT)
    suspend fun changePassword(
        @Field("current_password") currentPass: String,
        @Field("password") pass: String,
        @Field("password_confirmation") passConfirmation: String
    ): GenericResponseDto

    @POST(Constants.CHANGE_PASSWORD_ENDPOINT)
    suspend fun changePasswordWithJson(
        @Body request: ChangePasswordRequestDto
    ): GenericResponseDto

    @Multipart
    @POST(Constants.FEEDBACK_ENDPOINT)
    suspend fun submitFeedback(
        @Part("feedback") feedback: RequestBody
    ): GenericResponseDto

    @FormUrlEncoded
    @POST(Constants.FEEDBACK_ENDPOINT)
    suspend fun submitFeedbackForm(
        @Field("feedback") feedback: String
    ): GenericResponseDto

    @DELETE(Constants.DELETE_ACCOUNT_ENDPOINT)
    suspend fun deleteAccount(): GenericResponseDto

    // ── Module 3: Home & Mobile Feed ─────────────────────────────────────────
    @GET(Constants.HOME_ENDPOINT)
    suspend fun getHomeData(
        @Query("recommended_limit") recommendedLimit: Int? = 5,
        @Query("academy_limit") academyLimit: Int? = 6,
        @Query("brands_limit") brandsLimit: Int? = 6
    ): HomeResponseDto

    @GET(Constants.RECOMMENDED_CAMPAIGNS_ENDPOINT)
    suspend fun getRecommendedCampaigns(
        @Query("limit") limit: Int? = 10
    ): CampaignDiscoveryListResponseDto

    // ── Module 4: Mobile Unified Inbox ───────────────────────────────────────
    @GET(Constants.INBOX_ENDPOINT)
    suspend fun getUnifiedInbox(
        @Query("tab") tab: String? = "chat", // "chat" or "notifications"
        @Query("unread_only") unreadOnly: Int? = 0,
        @Query("per_page") perPage: Int? = 15,
        @Query("page") page: Int? = 1
    ): UnifiedInboxResponseDto

    @GET(Constants.INBOX_SUMMARY_ENDPOINT)
    suspend fun getInboxSummary(): InboxSummaryResponseDto

    // ── Module 5: Community & Campaign Content Feed ──────────────────────────
    @GET(Constants.CONTENT_FEED_ENDPOINT)
    suspend fun getContentFeed(
        @Query("tab") tab: String? = "all", // "all" or "collections"
        @Query("platform") platform: String? = null,
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("per_page") perPage: Int? = 10,
        @Query("page") page: Int? = 1
    ): ContentFeedListResponseDto

    @GET(Constants.CONTENT_FEED_CATEGORIES_ENDPOINT)
    suspend fun getContentFeedCategories(): ContentFeedCategoriesResponseDto

    @GET(Constants.CONTENT_FEED_REPORT_REASONS_ENDPOINT)
    suspend fun getContentReportReasons(): ContentReportReasonsResponseDto

    @GET("${Constants.CONTENT_FEED_ENDPOINT}/{id}")
    suspend fun getContentFeedDetail(
        @Path("id") contentId: Any
    ): ContentFeedDetailResponseDto

    @POST("${Constants.CONTENT_FEED_ENDPOINT}/{id}/bookmark")
    suspend fun toggleBookmarkContent(
        @Path("id") contentId: Any
    ): BookmarkResponseDto

    @POST("${Constants.CONTENT_FEED_ENDPOINT}/{id}/report")
    suspend fun reportContent(
        @Path("id") contentId: Any,
        @Body request: ContentReportRequestDto
    ): GenericResponseDto

    // ── Module 6: Influencer Showcase & Portfolio ────────────────────────────
    @GET("${Constants.INFLUENCERS_ENDPOINT}/{creator_id}")
    suspend fun getInfluencerProfile(
        @Path("creator_id") creatorId: Any
    ): InfluencerProfileResponseDto

    @GET("${Constants.INFLUENCERS_ENDPOINT}/{creator_id}/social-feed")
    suspend fun getInfluencerSocialFeed(
        @Path("creator_id") creatorId: Any,
        @Query("platform") platform: String? = "instagram",
        @Query("per_page") perPage: Int? = 18,
        @Query("page") page: Int? = 1
    ): InfluencerSocialFeedResponseDto

    @GET("${Constants.INFLUENCERS_ENDPOINT}/{creator_id}/content")
    suspend fun getInfluencerCampaignContent(
        @Path("creator_id") creatorId: Any,
        @Query("brand_id") brandId: Any? = null,
        @Query("per_page") perPage: Int? = 15,
        @Query("page") page: Int? = 1
    ): InfluencerContentResponseDto

    // ── Module 7: Refluenced Academy ─────────────────────────────────────────
    @GET(Constants.ACADEMY_CATEGORIES_ENDPOINT)
    suspend fun getAcademyCategories(): AcademyCategoriesResponseDto

    @GET(Constants.ACADEMY_VIDEOS_ENDPOINT)
    suspend fun getAcademyVideos(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("per_page") perPage: Int? = 15,
        @Query("page") page: Int? = 1
    ): AcademyVideosResponseDto

    @GET("${Constants.ACADEMY_VIDEOS_ENDPOINT}/{video_id}")
    suspend fun getAcademyVideoDetail(
        @Path("video_id") videoId: Any
    ): AcademyVideoDetailResponseDto

    // ── Module 8: Brand Following & My Brands ────────────────────────────────
    @GET(Constants.MY_BRANDS_ENDPOINT)
    suspend fun getMyFollowedBrands(
        @Query("search") search: String? = null,
        @Query("industry_id") industryId: Any? = null,
        @Query("per_page") perPage: Int? = 15,
        @Query("page") page: Int? = 1
    ): BrandListResponseDto

    @POST("${Constants.BRANDS_ENDPOINT}/{brand_id}/follow")
    suspend fun followBrand(
        @Path("brand_id") brandId: Any
    ): GenericResponseDto

    @POST("${Constants.BRANDS_ENDPOINT}/{brand_id}/unfollow")
    suspend fun unfollowBrand(
        @Path("brand_id") brandId: Any
    ): GenericResponseDto

    @POST("${Constants.BRANDS_ENDPOINT}/{brand_id}/toggle-follow")
    suspend fun toggleFollowBrand(
        @Path("brand_id") brandId: Any
    ): FollowToggleResponseDto

    // ── Module 9: Referrals & Invites ────────────────────────────────────────
    @GET(Constants.REFERRAL_MY_CODE_ENDPOINT)
    suspend fun getMyReferralCode(): ReferralStatsDto

    @POST(Constants.REFERRAL_CONFIRM_ENDPOINT)
    suspend fun confirmReferral(
        @Body request: ReferralConfirmRequestDto
    ): GenericResponseDto

    @FormUrlEncoded
    @POST(Constants.REFERRAL_CONFIRM_ENDPOINT)
    suspend fun confirmReferralForm(
        @Field("code") code: String
    ): GenericResponseDto

    @POST(Constants.REFERRAL_SKIP_ENDPOINT)
    suspend fun skipReferral(): GenericResponseDto

    @GET(Constants.REFERRALS_ENDPOINT)
    suspend fun getReferrals(): ReferralResponseDto

    @GET(Constants.EARNINGS_ENDPOINT)
    suspend fun getEarnings(): EarningsResponseDto

    @POST(Constants.PAYOUT_WITHDRAW_ENDPOINT)
    suspend fun withdrawPayout(
        @Body request: WithdrawRequestDto
    ): GenericResponseDto

    // ── Module 10: Geo & Master Data ─────────────────────────────────────────
    @GET(Constants.COUNTRIES_ENDPOINT)
    suspend fun getCountries(
        @Query("search") search: String? = null
    ): CountriesResponseDto

    @GET("${Constants.COUNTRIES_ENDPOINT}/{id}")
    suspend fun getCountryById(
        @Path("id") countryId: Int
    ): CountryDto

    @GET(Constants.INDUSTRIES_ENDPOINT)
    suspend fun getIndustries(): IndustriesResponseDto

    @GET(Constants.TERMS_ENDPOINT)
    suspend fun getTerms(): TermsResponseDto

    // ── Module 11: Creator Onboarding Steps ──────────────────────────────────
    @GET(Constants.ONBOARDING_STATUS_ENDPOINT)
    suspend fun getOnboardingStatus(): OnboardingStatusResponseDto

    @FormUrlEncoded
    @POST(Constants.ONBOARDING_ENDPOINT)
    suspend fun submitOnboardingStep1(
        @Field("step") step: Int = 1,
        @Field("first_name") firstName: String
    ): OnboardingResponseDto

    @FormUrlEncoded
    @POST(Constants.ONBOARDING_ENDPOINT)
    suspend fun submitOnboardingStep2(
        @Field("step") step: Int = 2,
        @Field("last_name") lastName: String
    ): OnboardingResponseDto

    @FormUrlEncoded
    @POST(Constants.ONBOARDING_ENDPOINT)
    suspend fun submitOnboardingStep3(
        @Field("step") step: Int = 3,
        @Field("gender") gender: String? = null,
        @Field("skip") skip: Int? = null
    ): OnboardingResponseDto

    @FormUrlEncoded
    @POST(Constants.ONBOARDING_ENDPOINT)
    suspend fun submitOnboardingStep4(
        @Field("step") step: Int = 4,
        @Field("country_id") countryId: Int
    ): OnboardingResponseDto

    @FormUrlEncoded
    @POST(Constants.ONBOARDING_ENDPOINT)
    suspend fun submitOnboardingStep5(
        @Field("step") step: Int = 5,
        @Field("industry_ids[]") industryIds: List<Int>
    ): OnboardingResponseDto

    @Multipart
    @POST(Constants.ONBOARDING_ENDPOINT)
    suspend fun submitOnboardingStep6(
        @Part("step") step: RequestBody,
        @Part photos: List<MultipartBody.Part>
    ): OnboardingResponseDto

    @FormUrlEncoded
    @POST(Constants.ONBOARDING_ENDPOINT)
    suspend fun submitOnboardingStep7(
        @Field("step") step: Int = 7,
        @Field("agreed") agreed: Int = 1
    ): OnboardingResponseDto

    // ── Module 12: Brand Management ──────────────────────────────────────────
    @GET(Constants.BRANDS_ME_ENDPOINT)
    suspend fun getMyBrand(): BrandDetailResponseDto

    @Multipart
    @POST(Constants.BRANDS_ENDPOINT)
    suspend fun createBrandProfile(
        @PartMap fields: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part logo: MultipartBody.Part? = null,
        @Part coverImage: MultipartBody.Part? = null
    ): BrandDetailResponseDto

    @Multipart
    @POST(Constants.BRANDS_UPDATE_ENDPOINT)
    suspend fun updateBrandProfile(
        @PartMap fields: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part logo: MultipartBody.Part? = null,
        @Part coverImage: MultipartBody.Part? = null
    ): BrandDetailResponseDto

    @DELETE(Constants.BRANDS_ENDPOINT)
    suspend fun deleteBrand(): GenericResponseDto

    @GET(Constants.BRANDS_ENDPOINT)
    suspend fun getActiveBrands(
        @Query("search") search: String? = null,
        @Query("country_id") countryId: Any? = null,
        @Query("industry_id") industryId: Any? = null,
        @Query("per_page") perPage: Int? = 15,
        @Query("page") page: Int? = 1
    ): BrandListResponseDto

    @GET("${Constants.BRANDS_ENDPOINT}/{brand_id}")
    suspend fun getBrandByIdOrSlug(
        @Path("brand_id") brandId: Any
    ): BrandDetailResponseDto

    // ── Module 13: Campaign Management (Brands) ──────────────────────────────
    @GET(Constants.BRAND_CAMPAIGNS_ENDPOINT)
    suspend fun getMyBrandCampaigns(
        @Query("status") status: String? = "active",
        @Query("per_page") perPage: Int? = 15,
        @Query("page") page: Int? = 1
    ): CampaignDiscoveryListResponseDto

    @POST(Constants.BRAND_CAMPAIGNS_ENDPOINT)
    suspend fun createBrandCampaign(
        @Body request: CreateCampaignRequestDto
    ): CampaignDetailResponseDto

    @POST("${Constants.BRAND_CAMPAIGNS_ENDPOINT}/{campaign_id}/update")
    suspend fun updateBrandCampaign(
        @Path("campaign_id") campaignId: Any,
        @Body request: UpdateCampaignRequestDto
    ): CampaignDetailResponseDto

    @DELETE("${Constants.BRAND_CAMPAIGNS_ENDPOINT}/{campaign_id}")
    suspend fun deleteBrandCampaign(
        @Path("campaign_id") campaignId: Any
    ): GenericResponseDto

    // ── Module 14: Campaign Discovery (Public & Creators) ────────────────────
    @GET(Constants.CAMPAIGNS_ENDPOINT)
    suspend fun getCampaigns(
        @Query("view_mode") viewMode: String? = null, // "recommended", "all", etc.
        @Query("filter_preset") filterPreset: String? = null,
        @Query("platform") platform: String? = null,
        @Query("stories_only") storiesOnly: Int? = null,
        @Query("search") search: String? = null,
        @Query("campaign_type") campaignType: String? = null,
        @Query("budget_type") budgetType: String? = null,
        @Query("industry_id") industryId: Any? = null,
        @Query("country_id") countryId: Any? = null,
        @Query("is_remote") isRemote: Int? = null,
        @Query("min_budget") minBudget: Double? = null,
        @Query("max_budget") maxBudget: Double? = null,
        @Query("is_featured") isFeatured: Int? = null,
        @Query("sort_by") sortBy: String? = "created_at",
        @Query("sort_order") sortOrder: String? = "desc",
        @Query("per_page") perPage: Int? = 15,
        @Query("page") page: Int? = 1
    ): CampaignDiscoveryListResponseDto

    @GET(Constants.CAMPAIGNS_FILTERS_ENDPOINT)
    suspend fun getCampaignFilterPresets(): CampaignFilterPresetsResponseDto

    @GET(Constants.CAMPAIGNS_REPORT_REASONS_ENDPOINT)
    suspend fun getCampaignReportReasons(): CampaignReportReasonsResponseDto

    @GET(Constants.CAMPAIGN_DETAIL_ENDPOINT)
    suspend fun getCampaignDetail(
        @Path("id") campaignId: Any
    ): CampaignDetailResponseDto

    @GET("${Constants.CAMPAIGNS_ENDPOINT}/{id}/share")
    suspend fun getCampaignShareLink(
        @Path("id") campaignId: Any
    ): CampaignShareLinkResponseDto

    @POST(Constants.FAVORITE_CAMPAIGN_ENDPOINT)
    suspend fun toggleFavoriteCampaign(
        @Path("id") campaignId: Any
    ): CampaignFavoriteResponseDto

    @GET(Constants.MY_FAVORITE_CAMPAIGNS_ENDPOINT)
    suspend fun getMyFavoriteCampaigns(
        @Query("per_page") perPage: Int? = 15,
        @Query("page") page: Int? = 1
    ): CampaignDiscoveryListResponseDto

    @POST("${Constants.CAMPAIGNS_ENDPOINT}/{id}/hide")
    suspend fun hideCampaign(
        @Path("id") campaignId: Any
    ): GenericResponseDto

    @POST("${Constants.CAMPAIGNS_ENDPOINT}/{id}/unhide")
    suspend fun unhideCampaign(
        @Path("id") campaignId: Any
    ): GenericResponseDto

    @POST(Constants.REPORT_CAMPAIGN_ENDPOINT)
    suspend fun reportCampaign(
        @Path("id") campaignId: Any,
        @Body request: ContentReportRequestDto
    ): GenericResponseDto

    // ── Module 15: Campaign Applications ─────────────────────────────────────
    @POST(Constants.APPLY_CAMPAIGN_ENDPOINT)
    suspend fun applyToCampaign(
        @Path("id") campaignId: Any,
        @Body request: ApplyCampaignRequestDto
    ): ApplicationDetailResponseDto

    @GET(Constants.MY_APPLICATIONS_ENDPOINT)
    suspend fun getMyApplications(
        @Query("status") status: String? = null,
        @Query("per_page") perPage: Int? = 15,
        @Query("page") page: Int? = 1
    ): ApplicationListResponseDto

    @GET("${Constants.MY_APPLICATIONS_ENDPOINT}/{id}")
    suspend fun getMyApplicationDetail(
        @Path("id") applicationId: Any
    ): ApplicationDetailResponseDto

    @POST("${Constants.MY_APPLICATIONS_ENDPOINT}/{id}/withdraw")
    suspend fun withdrawApplication(
        @Path("id") applicationId: Any
    ): GenericResponseDto

    @GET("${Constants.BRAND_CAMPAIGNS_ENDPOINT}/{campaign_id}/applications")
    suspend fun getBrandCampaignApplications(
        @Path("campaign_id") campaignId: Any,
        @Query("status") status: String? = null,
        @Query("per_page") perPage: Int? = 15,
        @Query("page") page: Int? = 1
    ): ApplicationListResponseDto

    @GET("${Constants.BRAND_APPLICATIONS_ENDPOINT}/{application_id}")
    suspend fun getBrandApplicationDetail(
        @Path("application_id") applicationId: Any
    ): ApplicationDetailResponseDto

    @POST("${Constants.BRAND_APPLICATIONS_ENDPOINT}/{application_id}/accept")
    suspend fun acceptBrandApplication(
        @Path("application_id") applicationId: Any
    ): GenericResponseDto

    @POST("${Constants.BRAND_APPLICATIONS_ENDPOINT}/{application_id}/reject")
    suspend fun rejectBrandApplication(
        @Path("application_id") applicationId: Any,
        @Body request: RejectApplicationRequestDto
    ): GenericResponseDto

    // ── Module 16: Campaign Invitations ──────────────────────────────────────
    @POST("${Constants.BRAND_CAMPAIGNS_ENDPOINT}/{campaign_id}/invite")
    suspend fun inviteCreatorToCampaign(
        @Path("campaign_id") campaignId: Any,
        @Body request: InviteCreatorRequestDto
    ): InvitationDetailResponseDto

    @GET("${Constants.BRAND_CAMPAIGNS_ENDPOINT}/{campaign_id}/invitations")
    suspend fun getBrandCampaignInvitations(
        @Path("campaign_id") campaignId: Any,
        @Query("status") status: String? = null,
        @Query("per_page") perPage: Int? = 15
    ): InvitationListResponseDto

    @POST("${Constants.BRAND_INVITATIONS_ENDPOINT}/{invitation_id}/cancel")
    suspend fun cancelBrandInvitation(
        @Path("invitation_id") invitationId: Any
    ): GenericResponseDto

    @GET(Constants.MY_INVITATIONS_ENDPOINT)
    suspend fun getMyInvitations(
        @Query("status") status: String? = null,
        @Query("per_page") perPage: Int? = 15
    ): InvitationListResponseDto

    @GET("${Constants.MY_INVITATIONS_ENDPOINT}/{invitation_id}")
    suspend fun getMyInvitationDetail(
        @Path("invitation_id") invitationId: Any
    ): InvitationDetailResponseDto

    @POST("${Constants.MY_INVITATIONS_ENDPOINT}/{invitation_id}/accept")
    suspend fun acceptMyInvitation(
        @Path("invitation_id") invitationId: Any
    ): GenericResponseDto

    @POST("${Constants.MY_INVITATIONS_ENDPOINT}/{invitation_id}/decline")
    suspend fun declineMyInvitation(
        @Path("invitation_id") invitationId: Any
    ): GenericResponseDto

    // ── Module 17: Creator Discovery (For Brands) ────────────────────────────
    @GET(Constants.CREATORS_ENDPOINT)
    suspend fun searchCreators(
        @Query("search") search: String? = null,
        @Query("country_id") countryId: Any? = null,
        @Query("industry_id") industryId: Any? = null,
        @Query("gender") gender: String? = null,
        @Query("city") city: String? = null,
        @Query("min_completed_steps") minCompletedSteps: Int? = null,
        @Query("per_page") perPage: Int? = 15,
        @Query("page") page: Int? = 1
    ): CreatorListResponseDto

    @GET("${Constants.CREATORS_ENDPOINT}/{creator_id}")
    suspend fun getCreatorPublicProfile(
        @Path("creator_id") creatorId: Any
    ): CreatorPublicProfileResponseDto

    // ── Module 18: Ratings & Reviews ─────────────────────────────────────────
    @POST(Constants.RATINGS_ENDPOINT)
    suspend fun submitRating(
        @Body request: SubmitRatingRequestDto
    ): RatingDetailResponseDto

    @GET(Constants.MY_RATINGS_ENDPOINT)
    suspend fun getMyRatings(
        @Query("type") type: String? = "all",
        @Query("per_page") perPage: Int? = 15
    ): RatingListResponseDto

    @GET("${Constants.CREATORS_ENDPOINT}/{creator_id}/ratings")
    suspend fun getCreatorPublicRatings(
        @Path("creator_id") creatorId: Any,
        @Query("per_page") perPage: Int? = 15
    ): RatingListResponseDto

    @GET("${Constants.BRANDS_ENDPOINT}/{brand_id}/ratings")
    suspend fun getBrandPublicRatings(
        @Path("brand_id") brandId: Any,
        @Query("per_page") perPage: Int? = 15
    ): RatingListResponseDto

    // ── Module 19: Notifications Center ──────────────────────────────────────
    @GET(Constants.NOTIFICATIONS_ENDPOINT)
    suspend fun getNotifications(
        @Query("unread_only") unreadOnly: Boolean? = null,
        @Query("per_page") perPage: Int? = 15,
        @Query("page") page: Int? = 1
    ): NotificationListResponseDto

    @GET(Constants.USER_NOTIFICATIONS_ENDPOINT)
    suspend fun getUserNotifications(
        @Query("userId") userId: String
    ): NotificationListResponseDto

    @POST("${Constants.NOTIFICATIONS_ENDPOINT}/{notification_id}/read")
    suspend fun markNotificationRead(
        @Path("notification_id") notificationId: Any
    ): GenericResponseDto

    @POST(Constants.NOTIFICATIONS_READ_ALL_ENDPOINT)
    suspend fun markAllNotificationsRead(): GenericResponseDto

    @DELETE("${Constants.NOTIFICATIONS_ENDPOINT}/{notification_id}")
    suspend fun deleteNotification(
        @Path("notification_id") notificationId: Any
    ): GenericResponseDto

    // ── Module 20: Chat & Messaging ──────────────────────────────────────────
    @GET(Constants.CONVERSATIONS_ENDPOINT)
    suspend fun getConversations(
        @Query("per_page") perPage: Int? = 15,
        @Query("page") page: Int? = 1
    ): ConversationListResponseDto

    @POST(Constants.CONVERSATIONS_ENDPOINT)
    suspend fun startConversation(
        @Body request: StartConversationRequestDto
    ): ConversationDetailResponseDto

    @GET("${Constants.CONVERSATIONS_ENDPOINT}/{conversation_id}")
    suspend fun getConversationDetail(
        @Path("conversation_id") conversationId: Any
    ): ConversationDetailResponseDto

    @GET("${Constants.CONVERSATIONS_ENDPOINT}/{conversation_id}/messages")
    suspend fun getConversationMessages(
        @Path("conversation_id") conversationId: Any,
        @Query("per_page") perPage: Int? = 25,
        @Query("page") page: Int? = 1
    ): MessageListResponseDto

    @Multipart
    @POST("${Constants.CONVERSATIONS_ENDPOINT}/{conversation_id}/messages")
    suspend fun sendMessage(
        @Path("conversation_id") conversationId: Any,
        @Part("message") message: RequestBody,
        @Part("message_type") messageType: RequestBody? = null,
        @Part attachment: MultipartBody.Part? = null
    ): MessageDetailResponseDto

    @POST("${Constants.CONVERSATIONS_ENDPOINT}/{conversation_id}/read")
    suspend fun markConversationRead(
        @Path("conversation_id") conversationId: Any
    ): GenericResponseDto

    // ── Module 21: Badges & Achievements ─────────────────────────────────────
    @GET(Constants.BADGES_ENDPOINT)
    suspend fun getPlatformBadges(): BadgeListResponseDto

    @GET(Constants.MY_BADGES_ENDPOINT)
    suspend fun getMyEarnedBadges(): BadgeListResponseDto

    @GET("${Constants.CREATORS_ENDPOINT}/{creator_id}/badges")
    suspend fun getCreatorEarnedBadges(
        @Path("creator_id") creatorId: Any
    ): BadgeListResponseDto

    // Legacy Support
    @GET(Constants.PRODUCTS_ENDPOINT)
    suspend fun getProducts(): ProductResponse
}
