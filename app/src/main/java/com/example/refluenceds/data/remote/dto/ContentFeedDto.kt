package com.example.refluenceds.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ContentFeedCreatorDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "first_name") val firstName: String? = null,
    @Json(name = "last_name") val lastName: String? = null,
    @Json(name = "profile_picture") val profilePicture: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "is_recently_active") val isRecentlyActive: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class ContentFeedBrandDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "company_name") val companyName: String? = null,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "logo") val logo: String? = null
)

@JsonClass(generateAdapter = true)
data class ContentFeedIndustryDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "name") val name: String? = null
)

@JsonClass(generateAdapter = true)
data class ContentFeedItemDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "caption") val caption: String? = null,
    @Json(name = "media_url") val mediaUrl: String? = null,
    @Json(name = "thumbnail_url") val thumbnailUrl: String? = null,
    @Json(name = "media_type") val mediaType: String? = "video", // video, image
    @Json(name = "platform") val platform: String? = "instagram", // instagram, tiktok, youtube
    @Json(name = "platform_post_url") val platformPostUrl: String? = null,
    @Json(name = "external_url") val externalUrl: String? = null,
    @Json(name = "views_count") val viewsCount: Int? = 0,
    @Json(name = "views_count_formatted") val viewsCountFormatted: String? = null,
    @Json(name = "likes_count") val likesCount: Int? = 0,
    @Json(name = "likes_count_formatted") val likesCountFormatted: String? = null,
    @Json(name = "comments_count") val commentsCount: Int? = 0,
    @Json(name = "comments_count_formatted") val commentsCountFormatted: String? = null,
    @Json(name = "shares_count") val sharesCount: Int? = 0,
    @Json(name = "is_bookmarked") val isBookmarked: Boolean? = false,
    @Json(name = "creator") val creator: ContentFeedCreatorDto? = null,
    @Json(name = "brand") val brand: ContentFeedBrandDto? = null,
    @Json(name = "campaign") val campaign: Any? = null,
    @Json(name = "industry") val industry: ContentFeedIndustryDto? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "published_at") val publishedAt: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,

    // Flat fallbacks
    @Json(name = "creator_name") val creatorNameFlat: String? = null,
    @Json(name = "creator_avatar") val creatorAvatarFlat: String? = null,
    @Json(name = "creator_handle") val creatorHandleFlat: String? = null,
    @Json(name = "brand_id") val brandIdFlat: Any? = null,
    @Json(name = "brand_name") val brandNameFlat: String? = null,
    @Json(name = "brand_logo") val brandLogoFlat: String? = null
) {
    val effectiveCreatorId: String
        get() = creator?.id?.toString() ?: ""

    val effectiveCreatorName: String
        get() = creator?.name?.takeIf { it.isNotBlank() }
            ?: listOfNotNull(creator?.firstName, creator?.lastName).joinToString(" ").takeIf { it.isNotBlank() }
            ?: creatorNameFlat
            ?: creatorHandleFlat
            ?: "Creator"

    val effectiveCreatorAvatar: String
        get() = creator?.profilePicture?.takeIf { it.isNotBlank() }
            ?: creatorAvatarFlat
            ?: ""

    val effectiveBrandName: String
        get() = brand?.companyName?.takeIf { it.isNotBlank() }
            ?: brandNameFlat
            ?: ""

    val effectiveBrandLogo: String
        get() = (brand?.logo?.takeIf { it.isNotBlank() }
            ?: brandLogoFlat
            ?: "").sanitizeMediaUrl()

    val effectiveBrandId: String
        get() = brand?.id?.toString()
            ?: brandIdFlat?.toString()
            ?: ""

    val effectiveThumbnailUrl: String
        get() = (thumbnailUrl?.takeIf { it.isNotBlank() }
            ?: mediaUrl?.takeIf { it.isNotBlank() }
            ?: "").sanitizeMediaUrl()

    val effectiveMediaUrl: String
        get() = (mediaUrl?.takeIf { it.isNotBlank() }
            ?: thumbnailUrl?.takeIf { it.isNotBlank() }
            ?: "").sanitizeMediaUrl()
}

@JsonClass(generateAdapter = true)
data class PaginationDto(
    @Json(name = "total") val total: Int? = 0,
    @Json(name = "per_page") val perPage: Int? = 10,
    @Json(name = "current_page") val currentPage: Int? = 1,
    @Json(name = "last_page") val lastPage: Int? = 1,
    @Json(name = "has_more") val hasMore: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class ContentFeedDataDto(
    @Json(name = "items") val items: List<ContentFeedItemDto>? = null,
    @Json(name = "pagination") val pagination: PaginationDto? = null
)

@JsonClass(generateAdapter = true)
data class ContentFeedListResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: ContentFeedDataDto? = null
) {
    val effectiveItems: List<ContentFeedItemDto>
        get() = data?.items ?: emptyList()
}

@JsonClass(generateAdapter = true)
data class ContentFeedDetailResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: ContentFeedItemDto? = null
)

@JsonClass(generateAdapter = true)
data class ContentFeedCategoryDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "icon") val icon: String? = null
)

@JsonClass(generateAdapter = true)
data class ContentFeedCategoriesResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<ContentFeedCategoryDto>? = null
)

@JsonClass(generateAdapter = true)
data class ReportReasonItemDto(
    @Json(name = "key") val key: String? = null,
    @Json(name = "reason") val reason: String? = null,
    @Json(name = "label") val label: String? = null,
    @Json(name = "description") val description: String? = null
)

@JsonClass(generateAdapter = true)
data class ContentReportReasonsResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<ReportReasonItemDto>? = null
)

@JsonClass(generateAdapter = true)
data class ContentReportRequestDto(
    @Json(name = "reason") val reason: String,
    @Json(name = "description") val description: String? = null
)

@JsonClass(generateAdapter = true)
data class BookmarkResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "is_bookmarked") val isBookmarked: Boolean? = null
)
