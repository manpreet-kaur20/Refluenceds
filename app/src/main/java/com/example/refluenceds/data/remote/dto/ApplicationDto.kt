package com.example.refluenceds.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApplicationItemDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "campaign_id") val campaignId: Any? = null,
    @Json(name = "campaign") val campaign: CampaignDetailDto? = null,
    @Json(name = "campaign_title") val campaignTitle: String? = null,
    @Json(name = "brand_id") val brandId: Any? = null,
    @Json(name = "brand_name") val brandName: String? = null,
    @Json(name = "brand_logo") val brandLogo: String? = null,
    @Json(name = "creator_id") val creatorId: Any? = null,
    @Json(name = "creator") val creator: UserDto? = null,
    @Json(name = "creator_name") val creatorName: String? = null,
    @Json(name = "creator_avatar") val creatorAvatar: String? = null,
    @Json(name = "pitch") val pitch: String? = null,
    @Json(name = "requested_compensation") val requestedCompensation: Double? = null,
    @Json(name = "deliverables_proposal") val deliverablesProposal: String? = null,
    @Json(name = "status") val status: String? = "pending", // pending, accepted, rejected, withdrawn
    @Json(name = "rejection_reason") val rejectionReason: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class ApplicationListResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<ApplicationItemDto>? = null,
    @Json(name = "current_page") val currentPage: Int? = 1,
    @Json(name = "per_page") val perPage: Int? = 15,
    @Json(name = "total") val total: Int? = 0
)

@JsonClass(generateAdapter = true)
data class ApplicationDetailResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: ApplicationItemDto? = null
)

@JsonClass(generateAdapter = true)
data class ApplyCampaignRequestDto(
    @Json(name = "pitch") val pitch: String? = null,
    @Json(name = "requested_compensation") val requestedCompensation: Double? = null,
    @Json(name = "deliverables_proposal") val deliverablesProposal: String? = null
)

@JsonClass(generateAdapter = true)
data class RejectApplicationRequestDto(
    @Json(name = "rejection_reason") val rejectionReason: String? = null
)
