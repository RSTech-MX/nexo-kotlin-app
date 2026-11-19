package com.rstech.inventory.domain.repository

import com.rstech.inventory.domain.model.Purchase
import kotlinx.coroutines.flow.Flow

interface PurchaseRepository {

    // Registrar una compra en la subcolección del usuario
    suspend fun addPurchase(purchase: Purchase): Result<Unit>

    // Obtener flujo en tiempo real de todas las compras ordenadas por fecha
    fun getPurchasesStream(): Flow<List<Purchase>>

    // Obtener lista completa de compras (útil para generación de reportes)
    suspend fun getAllPurchases(): Result<List<Purchase>>
}