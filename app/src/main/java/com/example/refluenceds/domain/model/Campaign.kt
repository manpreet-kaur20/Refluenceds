package com.example.refluenceds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Campaign(
    val id: String,
    val title: String,
    val brandName: String,
    val description: String,
    val reward: String,
    val imageUrl: String,
    val status: String,
    val deadline: Long,
    val category: String,
    val brandLogo: String? = null,
    val applicantsCount: Int = 0,
    val applicantsBadge: String? = null,
    val deliverablesSummary: String? = null,
    val platform: String? = null,
    val reelCount: Int = 0,
    val photoCount: Int = 0
)
