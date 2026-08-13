package com.example.refluenceds.domain.usecase

import com.example.refluenceds.domain.repository.AppRepository
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val repository: AppRepository
) {
    suspend operator fun invoke(email: String, pass: String): Result<String> {
        return repository.signup(email, pass)
    }
}
