package com.example.refluenceds.data.remote.dto

import com.example.refluenceds.domain.model.Campaign
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CampaignDeliverableDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "platform") val platform: String? = null, // instagram, tiktok, youtube
    @Json(name = "deliverable_type") val deliverableType: String? = null, // reel, story, post, video
    @Json(name = "quantity") val quantity: Int? = 1,
    @Json(name = "description") val description: String? = null,
    @Json(name = "order") val order: Int? = 0
)

@JsonClass(generateAdapter = true)
data class CampaignDetailDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "brand_id") val brandId: Any? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "short_description") val shortDescription: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "campaign_type") val campaignType: String? = null,
    @Json(name = "campaign_type_label") val campaignTypeLabel: String? = null,
    @Json(name = "industry_id") val industryId: Any? = null,
    @Json(name = "industry") val industry: Any? = null,
    @Json(name = "country_id") val countryId: Any? = null,
    @Json(name = "country") val country: Any? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "is_remote") val isRemote: Any? = null,
    @Json(name = "budget_type") val budgetType: String? = null,
    @Json(name = "minimum_budget") val minimumBudget: Any? = null,
    @Json(name = "maximum_budget") val maximumBudget: Any? = null,
    @Json(name = "budget") val budget: Any? = null,
    @Json(name = "compensation_details") val compensationDetails: String? = null,
    @Json(name = "currency") val currency: String? = "USD",
    @Json(name = "product_name") val productName: String? = null,
    @Json(name = "product_value") val productValue: Any? = null,
    @Json(name = "product_image") val productImage: String? = null,
    @Json(name = "application_deadline") val applicationDeadline: String? = null,
    @Json(name = "campaign_start_date") val campaignStartDate: String? = null,
    @Json(name = "campaign_end_date") val campaignEndDate: String? = null,
    @Json(name = "start_date") val startDate: String? = null,
    @Json(name = "end_date") val endDate: String? = null,
    @Json(name = "minimum_followers") val minimumFollowers: Any? = null,
    @Json(name = "status") val status: String? = "active",
    @Json(name = "is_featured") val isFeatured: Any? = null,
    @Json(name = "is_ongoing") val isOngoing: Any? = null,
    @Json(name = "ongoing_badge") val ongoingBadge: String? = null,
    @Json(name = "is_favorited") val isFavorited: Any? = null,
    @Json(name = "is_brand_followed") val isBrandFollowed: Any? = null,
    @Json(name = "is_hidden") val isHidden: Any? = null,
    @Json(name = "brand") val brand: BrandItemDto? = null,
    @Json(name = "brand_name") val brandName: String? = null,
    @Json(name = "brand_logo") val brandLogo: String? = null,
    @Json(name = "cover_image") val coverImage: String? = null,
    @Json(name = "cover_image_url") val coverImageUrl: String? = null,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "images") val images: Any? = null,
    @Json(name = "gallery") val gallery: Any? = null,
    @Json(name = "requirements") val requirements: List<Any>? = null,
    @Json(name = "deliverables") val deliverables: List<CampaignDeliverableDto>? = null,
    @Json(name = "applications_count") val applicationsCount: Any? = null,
    @Json(name = "applicants_count") val applicantsCount: Any? = null,
    @Json(name = "applicants_badge") val applicantsBadge: String? = null,
    @Json(name = "deliverables_summary") val deliverablesSummary: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
) {
    val effectiveBrandName: String get() = (brand?.companyName ?: brand?.name ?: brandName ?: "").ifBlank { "Brand" }
    val effectiveBrandLogo: String? get() = (brand?.effectiveLogo ?: brand?.logo ?: brandLogo).sanitizeMediaUrl()
    val effectiveImageUrl: String get() {
        val direct = (coverImageUrl ?: coverImage ?: imageUrl ?: productImage)?.takeIf { it.isNotBlank() }
        if (!direct.isNullOrBlank()) return direct.sanitizeMediaUrl()
        if (images is List<*>) {
            val first = images.firstOrNull()?.toString()?.takeIf { it.isNotBlank() }
            if (!first.isNullOrBlank()) return first.sanitizeMediaUrl()
        }
        if (gallery is List<*>) {
            val first = gallery.firstOrNull()?.toString()?.takeIf { it.isNotBlank() }
            if (!first.isNullOrBlank()) return first.sanitizeMediaUrl()
        }
        return ""
    }
    val effectiveStartDate: String get() = (campaignStartDate ?: startDate ?: "").substringBefore("T")
    val effectiveEndDate: String get() = (campaignEndDate ?: endDate ?: "").substringBefore("T")
    val effectiveDeadline: String get() = (applicationDeadline ?: "").substringBefore("T")
    val effectiveApplicationsCount: Int get() = (applicationsCount as? Number)?.toInt()
        ?: applicationsCount?.toString()?.toIntOrNull()
        ?: (applicantsCount as? Number)?.toInt()
        ?: applicantsCount?.toString()?.toIntOrNull()
        ?: 0
    val effectiveRequirements: List<String> get() {
        return requirements?.mapNotNull { item ->
            when (item) {
                is String -> item
                is Map<*, *> -> (item["requirement"] as? String)
                    ?: (item["description"] as? String)
                    ?: (item["title"] as? String)
                    ?: (item["text"] as? String)
                    ?: (item["name"] as? String)
                else -> item.toString()
            }
        } ?: emptyList()
    }
    val effectiveGalleryImages: List<String> get() {
        val list = mutableListOf<String>()
        val imgList = when (gallery) {
            is List<*> -> gallery
            is String -> listOf(gallery)
            else -> null
        } ?: when (images) {
            is List<*> -> images
            is String -> listOf(images)
            else -> null
        }
        imgList?.forEach { item ->
            val str = item?.toString()?.takeIf { it.isNotBlank() }
            if (str != null) list.add(str.sanitizeMediaUrl())
        }
        return list
    }
    val effectiveCompensation: String get() {
        if (!compensationDetails.isNullOrBlank()) return compensationDetails
        val bStr = budget?.toString()
        if (!bStr.isNullOrBlank()) return bStr
        val minB = (minimumBudget as? Number)?.toDouble() ?: minimumBudget?.toString()?.toDoubleOrNull()
        val maxB = (maximumBudget as? Number)?.toDouble() ?: maximumBudget?.toString()?.toDoubleOrNull()
        if (minB != null && maxB != null) {
            val curr = currency ?: "$"
            return "$curr${minB.toInt()} - $curr${maxB.toInt()}"
        }
        if (minB != null) return "${currency ?: "$"}${minB.toInt()}"
        val pv = productValue?.toString()
        if (!pv.isNullOrBlank()) return "${currency ?: "EUR"} $pv"
        return "Reward provided"
    }
    val effectiveIndustryName: String get() {
        if (industry is String) return industry
        if (industry is Map<*, *>) {
            return (industry["name"] as? String) ?: (industry["title"] as? String) ?: ""
        }
        return ""
    }

    val effectivePlatform: String get() {
        val dPlat = deliverables?.firstOrNull()?.platform?.lowercase()
        if (!dPlat.isNullOrBlank()) return dPlat
        val cType = campaignType?.lowercase() ?: ""
        if (cType.contains("tiktok")) return "tiktok"
        if (cType.contains("instagram")) return "instagram"
        return if (title?.contains("tiktok", ignoreCase = true) == true) "tiktok" else "instagram"
    }

    val effectiveReelCount: Int get() {
        val fromDeliv = deliverables?.filter { 
            it.deliverableType?.contains("reel", ignoreCase = true) == true || 
            it.deliverableType?.contains("video", ignoreCase = true) == true ||
            it.platform?.contains("tiktok", ignoreCase = true) == true
        }?.sumOf { it.quantity ?: 1 }
        return if (fromDeliv != null && fromDeliv > 0) fromDeliv else 1
    }

    val effectivePhotoCount: Int get() {
        val fromDeliv = deliverables?.filter { 
            it.deliverableType?.contains("photo", ignoreCase = true) == true || 
            it.deliverableType?.contains("post", ignoreCase = true) == true ||
            it.deliverableType?.contains("story", ignoreCase = true) == true
        }?.sumOf { it.quantity ?: 1 }
        return fromDeliv ?: 0
    }

    fun toDomain(): Campaign {
        return Campaign(
            id = id?.toString() ?: "",
            title = title ?: "",
            brandName = effectiveBrandName,
            description = description ?: shortDescription ?: "",
            reward = effectiveCompensation,
            imageUrl = effectiveImageUrl,
            status = status ?: "Active",
            deadline = 0L,
            category = effectiveIndustryName.ifBlank { "Fashion" },
            brandLogo = effectiveBrandLogo,
            applicantsCount = effectiveApplicationsCount,
            applicantsBadge = applicantsBadge,
            deliverablesSummary = deliverablesSummary,
            platform = effectivePlatform,
            reelCount = effectiveReelCount,
            photoCount = effectivePhotoCount
        )
    }
}

@JsonClass(generateAdapter = true)
data class CampaignDiscoveryDataDto(
    @Json(name = "items") val items: List<CampaignDetailDto>? = null,
    @Json(name = "campaigns") val campaigns: List<CampaignDetailDto>? = null,
    @Json(name = "data") val data: List<CampaignDetailDto>? = null,
    @Json(name = "pagination") val pagination: PaginationDto? = null
)

@JsonClass(generateAdapter = true)
data class CampaignDiscoveryListResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: CampaignDiscoveryDataDto? = null,
    @Json(name = "items") val items: List<CampaignDetailDto>? = null,
    @Json(name = "campaigns") val campaigns: List<CampaignDetailDto>? = null,
    @Json(name = "current_page") val currentPage: Int? = 1,
    @Json(name = "per_page") val perPage: Int? = 15,
    @Json(name = "total") val total: Int? = 0
) {
    val effectiveItems: List<CampaignDetailDto>
        get() = data?.items
            ?: data?.campaigns
            ?: data?.data
            ?: items
            ?: campaigns
            ?: emptyList()
}

@JsonClass(generateAdapter = true)
data class CampaignDetailResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: CampaignDetailDto? = null
)

@JsonClass(generateAdapter = true)
data class CampaignFilterPresetItemDto(
    @Json(name = "key") val key: String? = null,
    @Json(name = "label") val label: String? = null,
    @Json(name = "icon") val icon: String? = null,
    @Json(name = "params") val params: Map<String, Any>? = null
)

@JsonClass(generateAdapter = true)
data class CampaignFilterPresetsResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<CampaignFilterPresetItemDto>? = null
)

@JsonClass(generateAdapter = true)
data class CampaignReportReasonsResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<ReportReasonItemDto>? = null
)

@JsonClass(generateAdapter = true)
data class CampaignShareDataDto(
    @Json(name = "share_url") val shareUrl: String? = null,
    @Json(name = "share_text") val shareText: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "preview_image") val previewImage: String? = null
)

@JsonClass(generateAdapter = true)
data class CampaignShareLinkResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: CampaignShareDataDto? = null
)

@JsonClass(generateAdapter = true)
data class CampaignFavoriteResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "is_favorited") val isFavorited: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class CreateCampaignRequestDto(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "campaign_type") val campaignType: String = "paid", // paid, product
    @Json(name = "budget_type") val budgetType: String = "fixed", // fixed, negotiable
    @Json(name = "minimum_budget") val minimumBudget: Double? = null,
    @Json(name = "maximum_budget") val maximumBudget: Double? = null,
    @Json(name = "compensation_details") val compensationDetails: String? = null,
    @Json(name = "is_remote") val isRemote: Boolean = true,
    @Json(name = "industry_id") val industryId: Int? = null,
    @Json(name = "country_id") val countryId: Int? = null,
    @Json(name = "start_date") val startDate: String? = null,
    @Json(name = "end_date") val endDate: String? = null,
    @Json(name = "application_deadline") val applicationDeadline: String? = null,
    @Json(name = "status") val status: String = "active",
    @Json(name = "is_featured") val isFeatured: Boolean = false,
    @Json(name = "requirements") val requirements: List<String>? = null,
    @Json(name = "deliverables") val deliverables: List<CampaignDeliverableDto>? = null
)

@JsonClass(generateAdapter = true)
data class UpdateCampaignRequestDto(
    @Json(name = "title") val title: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "maximum_budget") val maximumBudget: Double? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "application_deadline") val applicationDeadline: String? = null
)
