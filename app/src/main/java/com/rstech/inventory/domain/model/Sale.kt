package com.rstech.inventory.domain.model

import java.util.Date

data class Sale(
    val id: String = "",
    val total: Double,
    val paymentMethod: String, // "Efectivo", "Tarjeta", etc.
    val totalItems: Int,
    val date: Date = Date(),
    val items: List<SaleItem> = emptyList()
)