package com.example.refluenceds.presentation.state

import com.example.refluenceds.domain.model.Product

sealed class ProductUiState {

    object Loading : ProductUiState()

    data class Success(
        val products: List<Product>
    ) : ProductUiState()

    data class Error(
        val message: String
    ) : ProductUiState()

    object Empty : ProductUiState()
}
