package com.example.refluenceds.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InfluencerSocialStatsDto(
    @Json(name = "followers") val followers: String? = null,
    @Json(name = "engagement_rate") val engagementRate: String? = null,
    @Json(name = "total_posts") val totalPosts: Int? = 0,
    @Json(name = "avg_likes") val avgLikes: String? = null,
    @Json(name = "avg_comments") val avgComments: String? = null
)

@JsonClass(generateAdapter = true)
data class InfluencerSocialProfileDto(
    @Json(name = "platform") val platform: String? = null,
    @Json(name = "handle") val handle: String? = null,
    @Json(name = "profile_url") val profileUrl: String? = null,
    @Json(name = "followers_count") val followersCount: Long? = 0,
    @Json(name = "followers_formatted") val followersFormatted: String? = null,
    @Json(name = "engagement_rate") val engagementRate: Double? = null,
    @Json(name = "engagement_rate_text") val engagementRateText: String? = null,
    @Json(name = "avg_views") val avgViews: Long? = 0,
    @Json(name = "avg_views_formatted") val avgViewsFormatted: String? = null,
    @Json(name = "is_verified") val isVerified: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class InfluencerReviewsSummaryDto(
    @Json(name = "average_rating") val averageRating: Double? = null,
    @Json(name = "ratings_count") val ratingsCount: Int? = 0,
    @Json(name = "formatted") val formatted: String? = null
)

fun String?.sanitizeMediaUrl(): String {
    if (this == null || this.isBlank()) return ""
    var url = this.trim()
    if (url.contains("/storage/http://")) {
        url = "http://" + url.substringAfter("/storage/http://")
    } else if (url.contains("/storage/https://")) {
        url = "https://" + url.substringAfter("/storage/https://")
    }
    return url
}

@JsonClass(generateAdapter = true)
data class InfluencerBrandChipDto(
    @Json(name = "brand_id") val brandId: Any? = null,
    @Json(name = "brand_name") val brandName: String? = null,
    @Json(name = "logo") val logo: String? = null,
    @Json(name = "pieces_count") val piecesCount: Int? = 0,
    @Json(name = "is_default") val isDefault: Boolean? = false
) {
    val effectiveLogo: String get() = logo.sanitizeMediaUrl()
}

@JsonClass(generateAdapter = true)
data class InfluencerCampaignContentWrapperDto(
    @Json(name = "total_campaigns_count") val totalCampaignsCount: Int? = 0,
    @Json(name = "total_pieces_count") val totalPiecesCount: Int? = 0,
    @Json(name = "brand_chips") val brandChips: List<InfluencerBrandChipDto>? = null,
    @Json(name = "items") val items: List<ContentFeedItemDto>? = null
)

@JsonClass(generateAdapter = true)
data class InfluencerSocialPostDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "platform") val platform: String? = "instagram",
    @Json(name = "media_type") val mediaType: String? = "video",
    @Json(name = "media_url") val mediaUrl: String? = null,
    @Json(name = "thumbnail_url") val thumbnailUrl: String? = null,
    @Json(name = "caption") val caption: String? = null,
    @Json(name = "likes") val likes: Int? = 0,
    @Json(name = "likes_count") val likesCount: Int? = 0,
    @Json(name = "comments") val comments: Int? = 0,
    @Json(name = "comments_count") val commentsCount: Int? = 0,
    @Json(name = "post_url") val postUrl: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
) {
    val effectiveLikes: Int get() = likes ?: likesCount ?: 0
    val effectiveComments: Int get() = comments ?: commentsCount ?: 0
    val effectiveMediaUrl: String get() = (thumbnailUrl ?: mediaUrl).sanitizeMediaUrl()
}

@JsonClass(generateAdapter = true)
data class InfluencerProfileDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "first_name") val firstName: String? = null,
    @Json(name = "last_name") val lastName: String? = null,
    @Json(name = "handle") val handle: String? = null,
    @Json(name = "avatar") val avatar: String? = null,
    @Json(name = "profile_picture") val profilePicture: String? = null,
    @Json(name = "cover_photo") val coverPhoto: String? = null,
    @Json(name = "bio") val bio: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "is_recently_active") val isRecentlyActive: Boolean? = true,
    @Json(name = "country") val country: CountryDto? = null,
    @Json(name = "industries") val industries: List<IndustryDto>? = null,
    @Json(name = "portfolio_photos") val portfolioPhotos: List<String>? = null,
    @Json(name = "social_profiles") val socialProfiles: List<InfluencerSocialProfileDto>? = null,
    @Json(name = "reviews_summary") val reviewsSummary: InfluencerReviewsSummaryDto? = null,
    @Json(name = "campaign_content") val campaignContent: InfluencerCampaignContentWrapperDto? = null,
    @Json(name = "instagram_stats") val instagramStats: InfluencerSocialStatsDto? = null,
    @Json(name = "tiktok_stats") val tiktokStats: InfluencerSocialStatsDto? = null,
    @Json(name = "badges") val badges: List<BadgeDto>? = null,
    @Json(name = "rating") val rating: Double? = null,
    @Json(name = "completed_campaigns_count") val completedCampaignsCount: Int? = 0
) {
    val effectiveCoverPhoto: String
        get() = (coverPhoto?.takeIf { it.isNotBlank() }
            ?: profilePicture?.takeIf { it.isNotBlank() }
            ?: avatar?.takeIf { it.isNotBlank() }
            ?: "").sanitizeMediaUrl()

    val effectiveProfilePicture: String
        get() = (profilePicture?.takeIf { it.isNotBlank() }
            ?: avatar?.takeIf { it.isNotBlank() }
            ?: coverPhoto?.takeIf { it.isNotBlank() }
            ?: "").sanitizeMediaUrl()

    val instagramProfile: InfluencerSocialProfileDto?
        get() = socialProfiles?.firstOrNull { it.platform?.equals("instagram", ignoreCase = true) == true }

    val tiktokProfile: InfluencerSocialProfileDto?
        get() = socialProfiles?.firstOrNull { it.platform?.equals("tiktok", ignoreCase = true) == true }
}

@JsonClass(generateAdapter = true)
data class InfluencerProfileResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: InfluencerProfileDto? = null
)

@JsonClass(generateAdapter = true)
data class InfluencerSocialFeedResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "platform") val platform: String? = null,
    @Json(name = "data") val data: List<InfluencerSocialPostDto>? = null,
    @Json(name = "current_page") val currentPage: Int? = 1,
    @Json(name = "per_page") val perPage: Int? = 18,
    @Json(name = "total") val total: Int? = 0
)

@JsonClass(generateAdapter = true)
data class InfluencerContentResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<ContentFeedItemDto>? = null,
    @Json(name = "current_page") val currentPage: Int? = 1,
    @Json(name = "per_page") val perPage: Int? = 15,
    @Json(name = "total") val total: Int? = 0
)
