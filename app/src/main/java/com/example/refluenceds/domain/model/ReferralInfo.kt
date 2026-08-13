package com.example.refluenceds.domain.model

data class ReferralInfo(
    val referralCode: String,
    val totalReferrals: Int,
    val totalEarned: Double,
    val rewardPerReferral: String
)
