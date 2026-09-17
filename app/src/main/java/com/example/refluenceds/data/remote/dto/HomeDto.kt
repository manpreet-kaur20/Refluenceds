package com.example.refluenceds.data.remote.dto

import com.example.refluenceds.domain.model.Campaign
import com.example.refluenceds.domain.model.HomeData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReferralBannerDto(
    @Json(name = "title") val title: String? = null,
    @Json(name = "subtitle") val subtitle: String? = null,
    @Json(name = "reward_amount") val rewardAmount: String? = null,
    @Json(name = "reward_value") val rewardValue: Any? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "referral_code") val referralCode: String? = null,
    @Json(name = "share_url") val shareUrl: String? = null,
    @Json(name = "button_text") val buttonText: String? = null,
    @Json(name = "total_invited") val totalInvited: Int? = 0,
    @Json(name = "banner_image") val bannerImage: String? = null
)

@JsonClass(generateAdapter = true)
data class HomeFeedDataDto(
    @Json(name = "banner") val banner: ReferralBannerDto? = null,
    @Json(name = "referral_banner") val referralBanner: ReferralBannerDto? = null,
    @Json(name = "recommended_campaigns") val recommendedCampaigns: List<CampaignDetailDto>? = null,
    @Json(name = "academy_preview") val academyPreview: List<AcademyVideoDto>? = null,
    @Json(name = "explore_brands") val exploreBrands: List<BrandItemDto>? = null,
    @Json(name = "categories") val categories: List<String>? = null,
    @Json(name = "featuredCampaigns") val featuredCampaigns: List<CampaignDto>? = null
) {
    val effectiveBanner: ReferralBannerDto? get() = banner ?: referralBanner
}

@JsonClass(generateAdapter = true)
data class HomeResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: HomeFeedDataDto? = null,
    @Json(name = "categories") val categories: List<String>? = null,
    @Json(name = "featuredCampaigns") val featuredCampaigns: List<CampaignDto>? = null
) {
    fun toDomain(): HomeData {
        val cats = data?.categories ?: categories ?: emptyList()
        val feats = (data?.featuredCampaigns ?: featuredCampaigns ?: emptyList()).map { it.toDomain() }
        val recCampaigns = data?.recommendedCampaigns?.map { it.toDomain() } ?: emptyList()

        return HomeData(
            categories = cats,
            featuredCampaigns = if (feats.isNotEmpty()) feats else recCampaigns
        )
    }
}
