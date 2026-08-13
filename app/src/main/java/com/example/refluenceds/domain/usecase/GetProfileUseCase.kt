package com.example.refluenceds.domain.usecase

import com.example.refluenceds.domain.model.UserProfile
import com.example.refluenceds.domain.repository.AppRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: AppRepository
) {
    suspend operator fun invoke(userId: String = "user_1"): Result<UserProfile> {
        return repository.getProfile(userId)
    }
}
