package com.example.refluenceds.domain.usecase

import com.example.refluenceds.domain.model.ReferralInfo
import com.example.refluenceds.domain.repository.AppRepository
import javax.inject.Inject

class GetReferralsUseCase @Inject constructor(
    private val repository: AppRepository
) {
    suspend operator fun invoke(): Result<ReferralInfo> {
        return repository.getReferrals()
    }
}
