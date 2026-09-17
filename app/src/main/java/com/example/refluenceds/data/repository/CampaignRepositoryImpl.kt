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
        return flowOf(emptyList())
    }

    override suspend fun refreshCampaigns() {
        // No-op
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
        return flowOf(emptyList())
    }

    override fun getSocialPosts(): Flow<List<Post>> {
        return flowOf(emptyList())
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
