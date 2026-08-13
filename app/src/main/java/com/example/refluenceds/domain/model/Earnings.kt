package com.example.refluenceds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Earnings(
    val totalEarnings: Double,
    val pendingPayouts: Double,
    val lastPayoutDate: Long,
    val earningsHistory: List<EarningItem>
)

@Serializable
data class EarningItem(
    val id: String,
    val amount: Double,
    val campaignId: String,
    val campaignTitle: String,
    val date: Long,
    val status: EarningStatus
)

enum class EarningStatus {
    PENDING, PAID
}
