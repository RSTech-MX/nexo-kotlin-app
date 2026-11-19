package com.rstech.inventory.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rstech.inventory.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState(isLoading = true))
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        loadProductsReport()
    }

    private fun loadProductsReport() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            productRepository.getProductsStream()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar productos"
                    )
                }
                .collect { products ->
                    // Cálculo seguro usando las propiedades cost y stock de tu modelo Product
                    val totalValue = products.sumOf { product ->
                        (product.cost * product.stock).toDouble()
                    }
                    _uiState.value = _uiState.value.copy(
                        products = products,
                        totalInventoryValue = totalValue,
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }
}