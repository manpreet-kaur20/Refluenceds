package com.example.refluenceds.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InvitationItemDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "campaign_id") val campaignId: Any? = null,
    @Json(name = "campaign") val campaign: CampaignDetailDto? = null,
    @Json(name = "brand_id") val brandId: Any? = null,
    @Json(name = "brand") val brand: BrandItemDto? = null,
    @Json(name = "creator_id") val creatorId: Any? = null,
    @Json(name = "creator") val creator: UserDto? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "status") val status: String? = "pending", // pending, accepted, declined, cancelled
    @Json(name = "expires_at") val expiresAt: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class InvitationListResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<InvitationItemDto>? = null,
    @Json(name = "current_page") val currentPage: Int? = 1,
    @Json(name = "per_page") val perPage: Int? = 15,
    @Json(name = "total") val total: Int? = 0
)

@JsonClass(generateAdapter = true)
data class InvitationDetailResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: InvitationItemDto? = null
)

@JsonClass(generateAdapter = true)
data class InviteCreatorRequestDto(
    @Json(name = "creator_id") val creatorId: Any? = null,
    @Json(name = "user_id") val userId: Any? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "expires_at") val expiresAt: String? = null
)
