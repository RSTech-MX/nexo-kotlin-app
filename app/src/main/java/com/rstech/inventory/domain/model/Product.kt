package com.rstech.inventory.domain.model

import java.util.Date

data class Product(
    val id: String = "",
    val name: String,
    val cost: Double,
    val price: Double,
    val stock: Int,
    val sku: String = "",
    val category: String = "",
    val minStock: Int = 0,
    val unit: String = "unidad",
    val description: String = "",
    val isActive: Boolean = true,
    val createdAt: Date? = null,
    val updatedAt: Date? = null
)