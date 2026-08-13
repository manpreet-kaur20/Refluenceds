package com.example.refluenceds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Creator(
    val id: String,
    val name: String,
    val bio: String,
    val profileImageUrl: String,
    val followersCount: Int,
    val engagementRate: Float
)
