package com.example.refluenceds.domain.usecase

import com.example.refluenceds.domain.repository.AppRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AppRepository
) {
    suspend operator fun invoke(email: String, pass: String): Result<String> {
        return repository.login(email, pass)
    }
}
