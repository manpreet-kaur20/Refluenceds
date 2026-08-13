package com.example.refluenceds.data.remote.dto

import com.example.refluenceds.domain.model.Campaign
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CampaignDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "brandName") val brandName: String,
    @Json(name = "description") val description: String? = "",
    @Json(name = "reward") val reward: String? = "",
    @Json(name = "imageUrl") val imageUrl: String,
    @Json(name = "status") val status: String? = "Active",
    @Json(name = "deadline") val deadline: Long? = 0L,
    @Json(name = "category") val category: String
) {
    fun toDomain(): Campaign {
        return Campaign(
            id = id,
            title = title,
            brandName = brandName,
            description = description ?: "",
            reward = reward ?: "",
            imageUrl = imageUrl,
            status = status ?: "Active",
            deadline = deadline ?: 0L,
            category = category
        )
    }
}
