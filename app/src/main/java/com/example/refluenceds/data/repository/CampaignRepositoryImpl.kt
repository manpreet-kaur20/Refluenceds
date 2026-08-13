package com.example.refluenceds.data.repository

import com.example.refluenceds.data.local.CampaignDao
import com.example.refluenceds.data.local.entity.toDomain
import com.example.refluenceds.data.local.entity.toEntity
import com.example.refluenceds.data.remote.RefluencedsApi
import com.example.refluenceds.domain.model.Campaign
import com.example.refluenceds.domain.model.Submission
import com.example.refluenceds.domain.model.SubmissionStatus
import com.example.refluenceds.domain.model.Tutorial
import com.example.refluenceds.domain.model.Post
import com.example.refluenceds.domain.model.Earnings
import com.example.refluenceds.domain.model.EarningItem
import com.example.refluenceds.domain.model.EarningStatus
import com.example.refluenceds.domain.repository.CampaignRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CampaignRepositoryImpl @Inject constructor(
    private val api: RefluencedsApi,
    private val dao: CampaignDao
) : CampaignRepository {

    private val _mockSubmissions = MutableStateFlow<List<Submission>>(emptyList())

    private fun getResUri(resId: Int): String = "android.resource://com.example.refluenceds/$resId"

    override fun getCampaigns(): Flow<List<Campaign>> {
        // For now, return a flow of mock data since we don't have a real API
        return flowOf(
            listOf(
                Campaign("1", "SOCCX MAKING", "SOCCX", "Fashion campaign for London", "$500", getResUri(com.example.refluenceds.R.drawable.fashion_shirt), "Active", System.currentTimeMillis() + 86400000, "Fashion"),
                Campaign("2", "Coral Magic Wash - No time? No problem.", "Coral", "Coral Magic Wash promotion", "$300", getResUri(com.example.refluenceds.R.drawable.coral_wash), "Active", System.currentTimeMillis() + 172800000, "Lifestyle"),
                Campaign("3", "PHANTOM PARFUM SEPTEMBER", "Phantom", "New fragrance campaign", "$200", getResUri(com.example.refluenceds.R.drawable.phantom_parfum), "Active", System.currentTimeMillis() + 259200000, "Lifestyle"),
                Campaign("4", "Seidenfelt Bundles UGC Campaign", "Seidenfelt", "UGC campaign for Seidenfelt bags", "$1000", getResUri(com.example.refluenceds.R.drawable.seidenfelt_ugc), "Active", System.currentTimeMillis() + 345600000, "Fashion"),
                Campaign("5", "Seidenfelt Bundles", "Seidenfelt", "Seidenfelt collection promo", "$1200", getResUri(com.example.refluenceds.R.drawable.seidenfelt_ugc), "Active", System.currentTimeMillis() + 432000000, "Fashion"),
                Campaign("6", "FAME SEPTEMBER", "Fame", "Fame perfume campaign", "$1000", getResUri(com.example.refluenceds.R.drawable.fashion_model), "Active", System.currentTimeMillis() + 345600000, "Fashion"),
            )
        )
    }

    override suspend fun refreshCampaigns() {
        // No-op for mock
    }

    override fun getSubmissions(): Flow<List<Submission>> {
        return _mockSubmissions.asStateFlow()
    }

    override suspend fun submitContent(campaignId: String, contentUrl: String) {
        val newSubmission = Submission(
            id = System.currentTimeMillis().toString(),
            campaignId = campaignId,
            creatorId = "current_user",
            contentUrl = contentUrl,
            status = SubmissionStatus.PENDING,
            submittedAt = System.currentTimeMillis()
        )
        _mockSubmissions.value = _mockSubmissions.value + newSubmission
    }

    override fun getTutorials(): Flow<List<Tutorial>> {
        return flowOf(
            listOf(
                // Onboarding Category
                Tutorial("1", "Content Synchronisation", "Learn how to sync your content across platforms.", getResUri(com.example.refluenceds.R.drawable.fashion_woman), "Onboarding", "01:00"),
                Tutorial("2", "Account Setup", "Step by step guide to set up your profile.", getResUri(com.example.refluenceds.R.drawable.fashion_model), "Onboarding", "00:47"),
                Tutorial("3", "How do you get paid?", "Everything you need to know about payouts.", getResUri(com.example.refluenceds.R.drawable.woman_laptop_post), "Onboarding", "01:20"),
                Tutorial("4", "How to Apply for Campaigns", "A quick guide on applying for brand deals.", getResUri(com.example.refluenceds.R.drawable.seidenfelt_ugc), "Onboarding", "00:55"),

                // Basics Category
                Tutorial("5", "Lighting", "Mastering lighting for high quality videos.", getResUri(com.example.refluenceds.R.drawable.fashion_shirt), "Basics", "00:17"),
                Tutorial("6", "Voice, Audio, Body Language", "How to speak clearly and present confidently.", getResUri(com.example.refluenceds.R.drawable.phantom_parfum), "Basics", "00:35"),
                Tutorial("7", "Camera Angles & Framing", "Framing tips for mobile video creation.", getResUri(com.example.refluenceds.R.drawable.fashion_woman), "Basics", "00:42"),
                Tutorial("8", "Hook & Engagement", "Captivating viewers in the first 3 seconds.", getResUri(com.example.refluenceds.R.drawable.fashion_model), "Basics", "00:50"),

                // Most popular Category
                Tutorial("9", "UGC Shot-List", "Essential shots for UGC product campaigns.", getResUri(com.example.refluenceds.R.drawable.uniq_skincare_logo), "Most popular", "00:28")
            )
        )
    }

    override fun getSocialPosts(): Flow<List<Post>> {
        return flowOf(
            listOf(
                Post("1", "Sarah Bloom", getResUri(com.example.refluenceds.R.drawable.app_icon), getResUri(com.example.refluenceds.R.drawable.fashion_woman), "Just finished my first campaign with Luxe Brand! #influencer #lifestyle", 124, "2h ago"),
                Post("2", "Tech Tom", getResUri(com.example.refluenceds.R.drawable.app_icon), getResUri(com.example.refluenceds.R.drawable.woman_laptop_post), "New gadget review incoming. The quality is insane! #tech #gadgets", 89, "5h ago"),
                Post("3", "Fitness Freya", getResUri(com.example.refluenceds.R.drawable.app_icon), getResUri(com.example.refluenceds.R.drawable.fashion_model), "Morning routine for maximum energy. #fitness #health", 256, "1d ago"),
            )
        )
    }

    override fun getEarnings(): Flow<Earnings> {
        return flowOf(
            Earnings(
                totalEarnings = 5450.0,
                pendingPayouts = 1200.0,
                lastPayoutDate = System.currentTimeMillis() - 604800000,
                earningsHistory = listOf(
                    EarningItem("1", 500.0, "1", "Summer Breeze", System.currentTimeMillis() - 86400000, EarningStatus.PAID),
                    EarningItem("2", 300.0, "2", "Tech Review", System.currentTimeMillis() - 172800000, EarningStatus.PAID),
                    EarningItem("3", 200.0, "3", "Healthy Living", System.currentTimeMillis() - 259200000, EarningStatus.PENDING),
                )
            )
        )
    }
}
