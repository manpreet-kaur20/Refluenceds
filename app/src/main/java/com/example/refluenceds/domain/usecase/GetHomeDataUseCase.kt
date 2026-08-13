package com.example.refluenceds.domain.usecase

import com.example.refluenceds.domain.model.HomeData
import com.example.refluenceds.domain.repository.AppRepository
import javax.inject.Inject

class GetHomeDataUseCase @Inject constructor(
    private val repository: AppRepository
) {
    suspend operator fun invoke(): Result<HomeData> {
        return repository.getHomeData()
    }
}
