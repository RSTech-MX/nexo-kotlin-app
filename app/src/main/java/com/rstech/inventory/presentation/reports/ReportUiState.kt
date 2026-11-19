package com.rstech.inventory.presentation.reports

import com.rstech.inventory.domain.model.Product

data class ReportUiState(
    val products: List<Product> = emptyList(),
    val totalInventoryValue: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)