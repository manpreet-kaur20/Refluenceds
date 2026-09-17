package com.example.refluenceds.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BadgeDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "icon_url") val iconUrl: String? = null,
    @Json(name = "icon") val icon: String? = null,
    @Json(name = "category") val category: String? = null,
    @Json(name = "level") val level: Int? = 1,
    @Json(name = "is_earned") val isEarned: Boolean? = false,
    @Json(name = "earned_at") val earnedAt: String? = null,
    @Json(name = "progress") val progress: Int? = 0,
    @Json(name = "max_progress") val maxProgress: Int? = 100
)

@JsonClass(generateAdapter = true)
data class BadgeListResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<BadgeDto>? = null,
    @Json(name = "total") val total: Int? = 0
)
