package com.rstech.inventory.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.rstech.inventory.domain.model.Product

data class ProductDto(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("nombre") @set:PropertyName("nombre") var nombre: String = "",
    @get:PropertyName("sku") @set:PropertyName("sku") var sku: String = "",
    @get:PropertyName("categoria") @set:PropertyName("categoria") var categoria: String = "",
    @get:PropertyName("costo") @set:PropertyName("costo") var costo: Double = 0.0,
    @get:PropertyName("precio") @set:PropertyName("precio") var precio: Double = 0.0,
    @get:PropertyName("cantidad") @set:PropertyName("cantidad") var cantidad: Int = 0,
    @get:PropertyName("stock_minimo") @set:PropertyName("stock_minimo") var stockMinimo: Int = 0,
    @get:PropertyName("unidad") @set:PropertyName("unidad") var unidad: String = "unidad",
    @get:PropertyName("descripcion") @set:PropertyName("descripcion") var descripcion: String = "",
    @get:PropertyName("activo") @set:PropertyName("activo") var activo: Boolean = true,
    @get:PropertyName("fecha_creacion") @set:PropertyName("fecha_creacion") var fechaCreacion: Timestamp? = null,
    @get:PropertyName("fecha_actualizacion") @set:PropertyName("fecha_actualizacion") var fechaActualizacion: Timestamp? = null
)

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        name = nombre,
        sku = sku,
        category = categoria,
        cost = costo,
        price = precio,
        stock = cantidad,
        minStock = stockMinimo,
        unit = unidad.ifBlank { "unidad" },
        description = descripcion,
        isActive = activo,
        createdAt = fechaCreacion?.toDate(),
        updatedAt = fechaActualizacion?.toDate()
    )
}

fun Product.toDto(): ProductDto {
    return ProductDto(
        id = id,
        nombre = name,
        sku = sku,
        categoria = category,
        costo = cost,
        precio = price,
        cantidad = stock,
        stockMinimo = minStock,
        unidad = unit.ifBlank { "unidad" },
        descripcion = description,
        activo = isActive,
        fechaCreacion = createdAt?.let { Timestamp(it) },
        fechaActualizacion = updatedAt?.let { Timestamp(it) }
    )
}