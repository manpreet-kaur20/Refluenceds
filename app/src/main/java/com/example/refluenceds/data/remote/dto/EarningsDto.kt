package com.example.refluenceds.data.remote.dto

import com.example.refluenceds.domain.model.EarningsSummary
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EarningsResponseDto(
    @Json(name = "totalEarnings") val totalEarnings: Double,
    @Json(name = "pendingEarnings") val pendingEarnings: Double,
    @Json(name = "availablePayout") val availablePayout: Double,
    @Json(name = "currency") val currency: String
) {
    fun toDomain(): EarningsSummary {
        return EarningsSummary(
            totalEarnings = totalEarnings,
            pendingEarnings = pendingEarnings,
            availablePayout = availablePayout,
            currency = currency
        )
    }
}
