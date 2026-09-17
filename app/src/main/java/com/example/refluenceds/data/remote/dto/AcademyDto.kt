package com.example.refluenceds.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AcademyCategoryDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "count") val count: Int? = 0,
    @Json(name = "videos_count") val videosCount: Int? = 0
)

@JsonClass(generateAdapter = true)
data class AcademyCategoriesResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<AcademyCategoryDto>? = null
)

@JsonClass(generateAdapter = true)
data class AcademyVideoDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "video_url") val videoUrl: String? = null,
    @Json(name = "thumbnail_url") val thumbnailUrl: String? = null,
    @Json(name = "duration") val duration: String? = null,
    @Json(name = "duration_seconds") val durationSeconds: Int? = 0,
    @Json(name = "category") val category: String? = null,
    @Json(name = "order") val order: Int? = 0,
    @Json(name = "views_count") val viewsCount: Int? = 0,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class AcademyVideosDataDto(
    @Json(name = "items") val items: List<AcademyVideoDto>? = null,
    @Json(name = "videos") val videos: List<AcademyVideoDto>? = null,
    @Json(name = "data") val data: List<AcademyVideoDto>? = null,
    @Json(name = "pagination") val pagination: PaginationDto? = null
)

@JsonClass(generateAdapter = true)
data class AcademyVideosResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: AcademyVideosDataDto? = null,
    @Json(name = "items") val items: List<AcademyVideoDto>? = null,
    @Json(name = "videos") val videos: List<AcademyVideoDto>? = null,
    @Json(name = "current_page") val currentPage: Int? = 1,
    @Json(name = "per_page") val perPage: Int? = 15,
    @Json(name = "total") val total: Int? = 0
) {
    val effectiveItems: List<AcademyVideoDto>
        get() = data?.items
            ?: data?.videos
            ?: data?.data
            ?: items
            ?: videos
            ?: emptyList()
}

@JsonClass(generateAdapter = true)
data class AcademyVideoDetailResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: AcademyVideoDto? = null
)
