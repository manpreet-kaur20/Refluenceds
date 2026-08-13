package com.example.refluenceds.domain.repository

import com.example.refluenceds.domain.model.AppNotification
import com.example.refluenceds.domain.model.EarningsSummary
import com.example.refluenceds.domain.model.HomeData
import com.example.refluenceds.domain.model.Product
import com.example.refluenceds.domain.model.ReferralInfo
import com.example.refluenceds.domain.model.UserProfile

interface AppRepository {

    suspend fun login(email: String, pass: String): Result<String>

    suspend fun signup(email: String, pass: String): Result<String>

    suspend fun forgotPassword(email: String): Result<String>

    suspend fun verifyEmail(email: String, code: String): Result<String>

    suspend fun verifySocial(platform: String, username: String): Result<String>

    suspend fun submitOnboarding(firstName: String, lastName: String, gender: String, country: String, interests: List<String>): Result<String>

    suspend fun logout(): Result<String>

    suspend fun deleteAccount(): Result<String>

    suspend fun getProducts(): Result<List<Product>>

    suspend fun getHomeData(): Result<HomeData>

    suspend fun getProfile(userId: String): Result<UserProfile>

    suspend fun editProfile(profile: UserProfile): Result<UserProfile>

    suspend fun reportCampaign(campaignId: String, reasons: List<String>, comment: String?): Result<String>

    suspend fun toggleFavorite(campaignId: String, isFav: Boolean): Result<String>

    suspend fun followBrand(brandId: String, follow: Boolean): Result<String>

    suspend fun getReferrals(): Result<ReferralInfo>

    suspend fun getEarnings(): Result<EarningsSummary>

    suspend fun withdrawPayout(amount: Double, iban: String): Result<String>

    suspend fun getNotifications(userId: String): Result<List<AppNotification>>
}
