package com.example.refluenceds.domain.model

data class EarningsSummary(
    val totalEarnings: Double,
    val pendingEarnings: Double,
    val availablePayout: Double,
    val currency: String
)
