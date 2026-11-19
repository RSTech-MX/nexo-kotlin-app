package com.rstech.inventory.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.rstech.inventory.domain.model.Sale
import com.rstech.inventory.domain.model.SaleItem
import java.util.Date

// DTO para el documento principal de la Venta
data class SaleDto(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("total") @set:PropertyName("total") var total: Double = 0.0,
    @get:PropertyName("metodo_pago") @set:PropertyName("metodo_pago") var metodoPago: String = "",
    @get:PropertyName("cantidad_productos") @set:PropertyName("cantidad_productos") var cantidadProductos: Int = 0,
    @get:PropertyName("fecha") @set:PropertyName("fecha") var fecha: Timestamp? = null
)

// DTO para la subcolección 'detalle'
data class SaleItemDto(
    @get:PropertyName("producto_id") @set:PropertyName("producto_id") var productoId: String = "",
    @get:PropertyName("nombre") @set:PropertyName("nombre") var nombre: String = "",
    @get:PropertyName("precio") @set:PropertyName("precio") var precio: Double = 0.0,
    @get:PropertyName("cantidad") @set:PropertyName("cantidad") var cantidad: Int = 0,
    @get:PropertyName("subtotal") @set:PropertyName("subtotal") var subtotal: Double = 0.0
)

// Mappers para convertir DTO -> Modelo de Dominio
fun SaleDto.toDomain(items: List<SaleItem> = emptyList()): Sale {
    return Sale(
        id = id,
        total = total,
        paymentMethod = metodoPago,
        totalItems = cantidadProductos,
        date = fecha?.toDate() ?: Date(),
        items = items
    )
}

fun SaleItemDto.toDomain(): SaleItem {
    return SaleItem(
        productId = productoId,
        name = nombre,
        price = precio,
        quantity = cantidad,
        subtotal = subtotal
    )
}