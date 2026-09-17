package com.example.refluenceds.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BrandRatingsBrandDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "company_name") val companyName: String? = null,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "logo_url") val logoUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class BrandRatingsSummaryDto(
    @Json(name = "average_rating") val averageRating: Double? = null,
    @Json(name = "total_ratings") val totalRatings: Int? = 0,
    @Json(name = "breakdown") val breakdown: Map<String, Int>? = null
)

@JsonClass(generateAdapter = true)
data class RatingReviewCreatorDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "avatar") val avatar: String? = null,
    @Json(name = "profile_picture") val profilePicture: String? = null
)

@JsonClass(generateAdapter = true)
data class RatingReviewCampaignDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "title") val title: String? = null
)

@JsonClass(generateAdapter = true)
data class RatingItemDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "campaign_id") val campaignId: Any? = null,
    @Json(name = "campaign_title") val campaignTitle: String? = null,
    @Json(name = "campaign") val campaign: RatingReviewCampaignDto? = null,
    @Json(name = "reviewer_id") val reviewerId: Any? = null,
    @Json(name = "reviewer_name") val reviewerName: String? = null,
    @Json(name = "reviewer_avatar") val reviewerAvatar: String? = null,
    @Json(name = "creator") val creator: RatingReviewCreatorDto? = null,
    @Json(name = "creator_id") val creatorId: Any? = null,
    @Json(name = "brand") val brand: BrandRatingsBrandDto? = null,
    @Json(name = "brand_id") val brandId: Any? = null,
    @Json(name = "brand_name") val brandName: String? = null,
    @Json(name = "brand_logo") val brandLogo: String? = null,
    @Json(name = "rating") val rating: Int = 5,
    @Json(name = "clear_specifications_rating") val clearSpecificationsRating: Int? = null,
    @Json(name = "specifications_rating") val specificationsRating: Int? = null,
    @Json(name = "likelihood_again_rating") val likelihoodAgainRating: Int? = null,
    @Json(name = "work_again_rating") val workAgainRating: Int? = null,
    @Json(name = "reliability_rating") val reliabilityRating: Int? = null,
    @Json(name = "brand_reliability_rating") val brandReliabilityRating: Int? = null,
    @Json(name = "review") val review: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
) {
    val effectiveReviewerName: String
        get() = reviewerName?.takeIf { it.isNotBlank() }
            ?: brandName?.takeIf { it.isNotBlank() }
            ?: brand?.companyName?.takeIf { it.isNotBlank() }
            ?: creator?.name?.takeIf { it.isNotBlank() }
            ?: "Reviewer"

    val effectiveReviewerAvatar: String?
        get() = reviewerAvatar?.takeIf { it.isNotBlank() }
            ?: brandLogo?.takeIf { it.isNotBlank() }
            ?: brand?.logoUrl?.takeIf { it.isNotBlank() }
            ?: creator?.avatar?.takeIf { it.isNotBlank() }
            ?: creator?.profilePicture

    val effectiveCampaignTitle: String
        get() = campaignTitle?.takeIf { it.isNotBlank() }
            ?: campaign?.title?.takeIf { it.isNotBlank() }
            ?: "Campaign"

    val effectiveClearSpecsRating: Int
        get() = clearSpecificationsRating ?: specificationsRating ?: rating

    val effectiveLikelihoodAgainRating: Int
        get() = likelihoodAgainRating ?: workAgainRating ?: rating

    val effectiveReliabilityRating: Int
        get() = reliabilityRating ?: brandReliabilityRating ?: rating
}

@JsonClass(generateAdapter = true)
data class BrandRatingsDataDto(
    @Json(name = "brand") val brand: BrandRatingsBrandDto? = null,
    @Json(name = "summary") val summary: BrandRatingsSummaryDto? = null,
    @Json(name = "items") val items: List<RatingItemDto>? = null,
    @Json(name = "ratings") val ratings: List<RatingItemDto>? = null,
    @Json(name = "pagination") val pagination: PaginationDto? = null
)

@JsonClass(generateAdapter = true)
data class RatingListResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: BrandRatingsDataDto? = null,
    @Json(name = "items") val items: List<RatingItemDto>? = null,
    @Json(name = "ratings") val ratings: List<RatingItemDto>? = null,
    @Json(name = "average_rating") val averageRating: Double? = null,
    @Json(name = "total_ratings") val totalRatings: Int? = 0,
    @Json(name = "current_page") val currentPage: Int? = 1,
    @Json(name = "per_page") val perPage: Int? = 15,
    @Json(name = "total") val total: Int? = 0
) {
    val effectiveItems: List<RatingItemDto>
        get() = data?.items ?: data?.ratings ?: items ?: ratings ?: emptyList()
}

@JsonClass(generateAdapter = true)
data class RatingDetailResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: RatingItemDto? = null
)

@JsonClass(generateAdapter = true)
data class SubmitRatingRequestDto(
    @Json(name = "campaign_id") val campaignId: Any,
    @Json(name = "rating") val rating: Int,
    @Json(name = "review") val review: String? = null,
    @Json(name = "creator_id") val creatorId: Any? = null,
    @Json(name = "brand_id") val brandId: Any? = null
)
