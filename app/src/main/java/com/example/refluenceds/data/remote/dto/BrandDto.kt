package com.example.refluenceds.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BrandItemDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "company_name") val companyName: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "logo") val logo: String? = null,
    @Json(name = "logo_url") val logoUrl: String? = null,
    @Json(name = "cover_image") val coverImage: String? = null,
    @Json(name = "cover_image_url") val coverImageUrl: String? = null,
    @Json(name = "website") val website: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "phone_code") val phoneCode: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "postal_code") val postalCode: String? = null,
    @Json(name = "country_id") val countryId: Any? = null,
    @Json(name = "country") val country: Any? = null,
    @Json(name = "industry_id") val industryId: Any? = null,
    @Json(name = "industry") val industry: Any? = null,
    @Json(name = "industry_name") val industryName: String? = null,
    @Json(name = "followers_count") val followersCount: Int? = 0,
    @Json(name = "active_campaigns_count") val activeCampaignsCount: Int? = 0,
    @Json(name = "is_following") val isFollowing: Boolean? = false,
    @Json(name = "is_verified") val isVerified: Boolean? = false,
    @Json(name = "rating") val rating: Double? = null,
    @Json(name = "ratings_count") val ratingsCount: Int? = 0,
    @Json(name = "created_at") val createdAt: String? = null
) {
    val displayName: String get() = companyName ?: name ?: ""
    val effectiveLogo: String? get() = logoUrl ?: logo
    val effectiveCover: String? get() = coverImageUrl ?: coverImage
    val effectiveIndustryName: String get() {
        if (industry is String) return industry
        if (industry is Map<*, *>) {
            return (industry["name"] as? String) ?: (industry["title"] as? String) ?: ""
        }
        return industryName ?: ""
    }
}

@JsonClass(generateAdapter = true)
data class BrandListResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<BrandItemDto>? = null,
    @Json(name = "current_page") val currentPage: Int? = 1,
    @Json(name = "per_page") val perPage: Int? = 15,
    @Json(name = "total") val total: Int? = 0
)

@JsonClass(generateAdapter = true)
data class BrandDetailResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: BrandItemDto? = null
)

@JsonClass(generateAdapter = true)
data class FollowToggleResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "is_following") val isFollowing: Boolean? = null,
    @Json(name = "followers_count") val followersCount: Int? = null
)
