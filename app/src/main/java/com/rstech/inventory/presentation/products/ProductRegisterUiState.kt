package com.rstech.inventory.presentation.products

data class ProductRegisterUiState(
    val name: String = "",
    val sku: String = "",
    val category: String = "",
    val cost: String = "",
    val price: String = "",
    val quantity: String = "",
    val minStock: String = "",
    val unit: String = "unidad",
    val description: String = "",
    val isSaving: Boolean = false
)

sealed interface ProductRegisterUiEvent {
    data class ShowMessage(val message: String) : ProductRegisterUiEvent
    data object ProductRegistered : ProductRegisterUiEvent
}