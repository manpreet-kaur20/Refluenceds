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
        return flowOf(
            listOf(
                Campaign("1", "Your Schogetten Moment", "Schogetten", "Your Schogetten Moment campaign", "$500", getResUri(com.example.refluenceds.R.drawable.schogetten_campaign), "Active", System.currentTimeMillis() + 86400000, "Fashion"),
                Campaign("2", "NEW COLLECTION // FW26", "Metalli", "New FW26 collection campaign", "$300", getResUri(com.example.refluenceds.R.drawable.boots_campaign), "Active", System.currentTimeMillis() + 172800000, "Fashion"),
                Campaign("3", "Modepark Röther Super Sale", "Modepark Röther", "Super sale campaign", "$200", getResUri(com.example.refluenceds.R.drawable.modepark_campaign), "Active", System.currentTimeMillis() + 259200000, "Fashion"),
                Campaign("4", "NEW COLLECTION // FW26", "Metalli", "New FW26 collection campaign", "$1000", getResUri(com.example.refluenceds.R.drawable.boots_campaign), "Active", System.currentTimeMillis() + 345600000, "Fashion"),
                Campaign("5", "French Coast 🌊", "Matcha", "French coast aesthetic campaign", "$1200", getResUri(com.example.refluenceds.R.drawable.french_coast_campaign), "Active", System.currentTimeMillis() + 432000000, "Lifestyle"),
                Campaign("6", "The Bangkok Edit", "Matcha", "Bangkok edit campaign", "$1000", getResUri(com.example.refluenceds.R.drawable.french_coast_campaign), "Active", System.currentTimeMillis() + 345600000, "Lifestyle"),
                Campaign("7", "s.Oliver x Heidi Klum", "s.Oliver", "s.Oliver x Heidi Klum campaign", "$800", getResUri(com.example.refluenceds.R.drawable.camp_david_campaign), "Active", System.currentTimeMillis() + 500000000, "Fashion"),
                Campaign("8", "New Autumn/Winter Collection by LES VISIONNAIRES", "LES VISIONNAIRES", "Autumn Winter collection", "$950", getResUri(com.example.refluenceds.R.drawable.les_visionnaires_campaign), "Active", System.currentTimeMillis() + 600000000, "Fashion"),
                Campaign("9", "Desert Escapes 🌴 Your Summer Lash Ritual", "NAVAH", "Summer lash ritual campaign", "$450", getResUri(com.example.refluenceds.R.drawable.summer_lash_campaign), "Active", System.currentTimeMillis() + 700000000, "Beauty")
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
                Tutorial("1", "Content Synchronisation", "Learn how to sync your content across platforms.", getResUri(com.example.refluenceds.R.drawable.academy_content_sync), "Onboarding", "01:00"),
                Tutorial("2", "Account Setup", "Step by step guide to set up your profile.", getResUri(com.example.refluenceds.R.drawable.academy_account_setup), "Onboarding", "00:47"),
                Tutorial("3", "How do you get paid?", "Everything you need to know about payouts.", getResUri(com.example.refluenceds.R.drawable.academy_how_get_paid), "Onboarding", "01:20"),
                Tutorial("4", "How to Apply for Campaigns", "A quick guide on applying for brand deals.", getResUri(com.example.refluenceds.R.drawable.academy_apply_campaigns), "Onboarding", "00:55"),

                // Basics Category
                Tutorial("5", "Lighting", "Mastering lighting for high quality videos.", getResUri(com.example.refluenceds.R.drawable.academy_lighting), "Basics", "00:17"),
                Tutorial("6", "Voice, Audio, Body Language", "How to speak clearly and present confidently.", getResUri(com.example.refluenceds.R.drawable.academy_voice_audio), "Basics", "00:35"),
                Tutorial("7", "Camera Angles & Framing", "Framing tips for mobile video creation.", getResUri(com.example.refluenceds.R.drawable.fashion_woman), "Basics", "00:42"),
                Tutorial("8", "Hook & Engagement", "Captivating viewers in the first 3 seconds.", getResUri(com.example.refluenceds.R.drawable.fashion_model), "Basics", "00:50"),

                // Most popular Category
                Tutorial("9", "UGC Shot-List", "Essential shots for UGC product campaigns.", getResUri(com.example.refluenceds.R.drawable.academy_ugc_shotlist), "Most popular", "00:28")
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
