package com.example.refluenceds.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenericResponseDto(
    @Json(name = "success") val success: Boolean? = true,
    @Json(name = "status") val status: Any? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class ReportRequestDto(
    @Json(name = "campaignId") val campaignId: String,
    @Json(name = "reasons") val reasons: List<String>,
    @Json(name = "comment") val comment: String? = null
)

@JsonClass(generateAdapter = true)
data class FollowRequestDto(
    @Json(name = "brandId") val brandId: String,
    @Json(name = "follow") val follow: Boolean
)

@JsonClass(generateAdapter = true)
data class FavoriteRequestDto(
    @Json(name = "campaignId") val campaignId: String,
    @Json(name = "isFavorite") val isFavorite: Boolean
)

@JsonClass(generateAdapter = true)
data class WithdrawRequestDto(
    @Json(name = "amount") val amount: Double,
    @Json(name = "iban") val iban: String
)
