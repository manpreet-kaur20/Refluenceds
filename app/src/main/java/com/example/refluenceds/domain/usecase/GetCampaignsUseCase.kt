package com.example.refluenceds.domain.usecase

import com.example.refluenceds.domain.model.Campaign
import com.example.refluenceds.domain.repository.CampaignRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCampaignsUseCase @Inject constructor(
    private val repository: CampaignRepository
) {
    operator fun invoke(): Flow<List<Campaign>> {
        return repository.getCampaigns()
    }
}
