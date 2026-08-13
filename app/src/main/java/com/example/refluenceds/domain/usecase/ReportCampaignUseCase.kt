package com.example.refluenceds.domain.usecase

import com.example.refluenceds.domain.repository.AppRepository
import javax.inject.Inject

class ReportCampaignUseCase @Inject constructor(
    private val repository: AppRepository
) {
    suspend operator fun invoke(campaignId: String, reasons: List<String>, comment: String? = null): Result<String> {
        return repository.reportCampaign(campaignId, reasons, comment)
    }
}
