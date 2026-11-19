package com.rstech.inventory.presentation.reports.sales

import java.util.Date

data class SaleDetailItem(
    val name: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val subtotal: Double = 0.0
)

data class SaleReportItem(
    val id: String = "",
    val date: Date = Date(),
    val paymentMethod: String = "",
    val total: Double = 0.0,
    val products: List<SaleDetailItem> = emptyList()
)