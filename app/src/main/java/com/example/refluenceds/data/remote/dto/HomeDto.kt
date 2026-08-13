package com.example.refluenceds.data.remote.dto

import com.example.refluenceds.domain.model.HomeData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeResponseDto(
    @Json(name = "categories") val categories: List<String>,
    @Json(name = "featuredCampaigns") val featuredCampaigns: List<CampaignDto>
) {
    fun toDomain(): HomeData {
        return HomeData(
            categories = categories,
            featuredCampaigns = featuredCampaigns.map { it.toDomain() }
        )
    }
}
