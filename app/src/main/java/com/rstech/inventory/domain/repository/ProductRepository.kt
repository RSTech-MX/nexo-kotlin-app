package com.rstech.inventory.domain.repository

import com.rstech.inventory.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    // Obtener flujo en tiempo real (Equivalente al Stream/snapshots() de Flutter)
    fun getProductsStream(): Flow<List<Product>>

    // Obtener lista de productos una sola vez
    suspend fun getProductsOnce(): Result<List<Product>>

    // Agregar un producto nuevo
    suspend fun addProduct(product: Product): Result<Unit>

    // Actualizar un producto existente
    suspend fun updateProduct(product: Product): Result<Unit>

    // Eliminar un producto por ID
    suspend fun deleteProduct(productId: String): Result<Unit>
}