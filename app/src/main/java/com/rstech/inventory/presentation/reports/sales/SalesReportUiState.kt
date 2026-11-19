package com.rstech.inventory.presentation.reports.sales

import java.util.Date

data class SalesReportUiState(
    val filterType: SalesFilterType = SalesFilterType.DAY,
    val selectedDate: Date = Date(),
    val paymentMethod: String = "Todos",
    val sales: List<SaleReportItem> = emptyList(),
    val grandTotal: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)