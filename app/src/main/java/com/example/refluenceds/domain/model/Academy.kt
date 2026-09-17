package com.example.refluenceds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Tutorial(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val category: String,
    val duration: String,
    val videoUrl: String = ""
)
