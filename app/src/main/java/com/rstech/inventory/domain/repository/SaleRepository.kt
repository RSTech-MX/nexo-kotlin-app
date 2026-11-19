package com.rstech.inventory.domain.repository

import com.rstech.inventory.domain.model.Sale
import com.rstech.inventory.domain.model.SaleItem
import java.util.Date
import kotlinx.coroutines.flow.Flow

interface SaleRepository {

    // Registrar una venta actualizando stock en transacción
    suspend fun registerSale(
        items: List<SaleItem>,
        total: Double,
        paymentMethod: String
    ): Result<Unit>

    // Obtener flujo continuo de ventas por rango de fechas (Equivalente a tu Stream/ventasStream)
    fun getSalesStream(
        startDate: Date,
        endDate: Date,
        paymentMethod: String
    ): Flow<List<Sale>>

    // Obtener ventas completas con detalle para reportes/exportar
    suspend fun getSalesWithDetails(
        startDate: Date,
        endDate: Date,
        paymentMethod: String
    ): Result<List<Sale>>
}