package com.example.refluenceds.data.remote.dto

import com.example.refluenceds.domain.model.ReferralInfo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReferralResponseDto(
    @Json(name = "referralCode") val referralCode: String,
    @Json(name = "totalReferrals") val totalReferrals: Int,
    @Json(name = "totalEarned") val totalEarned: Double,
    @Json(name = "rewardPerReferral") val rewardPerReferral: String
) {
    fun toDomain(): ReferralInfo {
        return ReferralInfo(
            referralCode = referralCode,
            totalReferrals = totalReferrals,
            totalEarned = totalEarned,
            rewardPerReferral = rewardPerReferral
        )
    }
}
