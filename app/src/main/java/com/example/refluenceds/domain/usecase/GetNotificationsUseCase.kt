package com.example.refluenceds.domain.usecase

import com.example.refluenceds.domain.model.AppNotification
import com.example.refluenceds.domain.repository.AppRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: AppRepository
) {
    suspend operator fun invoke(userId: String = "user_1"): Result<List<AppNotification>> {
        return repository.getNotifications(userId)
    }
}
