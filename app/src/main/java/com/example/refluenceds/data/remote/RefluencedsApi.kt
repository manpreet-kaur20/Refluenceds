package com.example.refluenceds.data.remote

import com.example.refluenceds.domain.model.Campaign
import retrofit2.http.GET

interface RefluencedsApi {
    @GET("campaigns")
    suspend fun getCampaigns(): List<Campaign>

    companion object {
        const val BASE_URL = "https://api.refluenceds.com/" // Placeholder
    }
}
