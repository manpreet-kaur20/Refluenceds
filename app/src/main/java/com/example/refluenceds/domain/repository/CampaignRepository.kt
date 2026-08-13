package com.example.refluenceds.domain.repository

import com.example.refluenceds.domain.model.Campaign
import com.example.refluenceds.domain.model.Submission
import com.example.refluenceds.domain.model.Tutorial
import com.example.refluenceds.domain.model.Post
import com.example.refluenceds.domain.model.Earnings
import kotlinx.coroutines.flow.Flow

interface CampaignRepository {
    fun getCampaigns(): Flow<List<Campaign>>
    suspend fun refreshCampaigns()
    fun getSubmissions(): Flow<List<Submission>>
    suspend fun submitContent(campaignId: String, contentUrl: String)
    fun getTutorials(): Flow<List<Tutorial>>
    fun getSocialPosts(): Flow<List<Post>>
    fun getEarnings(): Flow<Earnings>
}
