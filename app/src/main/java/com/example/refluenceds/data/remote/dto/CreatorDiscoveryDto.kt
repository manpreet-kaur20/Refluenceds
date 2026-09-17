package com.example.refluenceds.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatorSummaryDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "first_name") val firstName: String? = null,
    @Json(name = "last_name") val lastName: String? = null,
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "gender") val gender: String? = null,
    @Json(name = "avatar") val avatar: String? = null,
    @Json(name = "profile_picture") val profilePicture: String? = null,
    @Json(name = "bio") val bio: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "country_id") val countryId: Any? = null,
    @Json(name = "country") val country: CountryDto? = null,
    @Json(name = "industries") val industries: List<IndustryDto>? = null,
    @Json(name = "photos") val photos: List<PhotoDto>? = null,
    @Json(name = "instagram") val instagram: String? = null,
    @Json(name = "tiktok") val tiktok: String? = null,
    @Json(name = "rating") val rating: Double? = null,
    @Json(name = "ratings_count") val ratingsCount: Int? = 0,
    @Json(name = "badges") val badges: List<BadgeDto>? = null,
    @Json(name = "completed_campaigns_count") val completedCampaignsCount: Int? = 0
) {
    val displayName: String get() = fullName ?: listOfNotNull(firstName, lastName).joinToString(" ").ifEmpty { "Creator" }
    val effectiveAvatar: String? get() = avatar ?: profilePicture?.normalizeImageUrl() ?: photos?.firstOrNull()?.getEffectiveUrl()
}

@JsonClass(generateAdapter = true)
data class CreatorListResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<CreatorSummaryDto>? = null,
    @Json(name = "current_page") val currentPage: Int? = 1,
    @Json(name = "per_page") val perPage: Int? = 15,
    @Json(name = "total") val total: Int? = 0
)

@JsonClass(generateAdapter = true)
data class CreatorPublicProfileResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: CreatorSummaryDto? = null
)
