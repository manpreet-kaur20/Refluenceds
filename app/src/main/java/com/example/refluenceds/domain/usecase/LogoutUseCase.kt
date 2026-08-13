package com.example.refluenceds.domain.usecase

import com.example.refluenceds.domain.repository.AppRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AppRepository
) {
    suspend operator fun invoke(): Result<String> {
        return repository.logout()
    }
}
