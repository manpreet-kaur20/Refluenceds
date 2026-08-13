package com.example.refluenceds.domain.usecase

import com.example.refluenceds.domain.model.Product
import com.example.refluenceds.domain.repository.AppRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: AppRepository
) {
    suspend operator fun invoke(): Result<List<Product>> {
        return repository.getProducts()
    }
}
