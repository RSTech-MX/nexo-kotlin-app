package com.rstech.inventory.domain.model

data class SaleItem(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val subtotal: Double = price * quantity
)