package com.example.refluenceds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String,
    val creatorId: String = "",
    val creatorName: String,
    val creatorAvatar: String,
    val contentUrl: String,
    val videoUrl: String = "",
    val caption: String,
    val likes: Int = 0,
    val timeAgo: String = "",
    val creatorHandle: String = "",
    val commentsCount: Int = 0,
    val viewsCount: Int = 0,
    val platform: String = "instagram",
    val brandId: String = "",
    val brandName: String = "",
    val brandLogo: String = "",
    val isBookmarked: Boolean = false,
    val mediaType: String = "video"
)

