package com.example.refluenceds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String,
    val creatorName: String,
    val creatorAvatar: String,
    val contentUrl: String,
    val caption: String,
    val likes: Int,
    val timeAgo: String
)
