package com.example.refluenceds.domain.usecase

import com.example.refluenceds.domain.model.EarningsSummary
import com.example.refluenceds.domain.repository.AppRepository
import javax.inject.Inject

class GetEarningsUseCase @Inject constructor(
    private val repository: AppRepository
) {
    suspend operator fun invoke(): Result<EarningsSummary> {
        return repository.getEarnings()
    }
}
