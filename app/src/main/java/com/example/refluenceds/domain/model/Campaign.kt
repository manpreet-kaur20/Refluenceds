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
    val category: String
)
