package com.example.refluenceds.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refluenceds.domain.usecase.GetProductsUseCase
import com.example.refluenceds.presentation.state.ProductUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        getProducts()
    }

    fun getProducts() {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading

            getProductsUseCase()
                .onSuccess { products ->
                    if (products.isEmpty()) {
                        _uiState.value = ProductUiState.Empty
                    } else {
                        _uiState.value = ProductUiState.Success(products)
                    }
                }
                .onFailure { exception ->
                    _uiState.value = ProductUiState.Error(
                        exception.message ?: "Something went wrong"
                    )
                }
        }
    }
}
