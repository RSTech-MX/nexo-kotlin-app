package com.rstech.inventory.domain.usecase

import com.rstech.inventory.domain.model.Product
import com.rstech.inventory.domain.repository.ProductRepository
import javax.inject.Inject

class AddProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product): Result<Unit> {
        // Aquí puedes agregar reglas de negocio opcionales si lo deseas:
        if (product.price < product.cost) {
            return Result.failure(IllegalArgumentException("El precio de venta no puede ser menor al costo"))
        }
        return repository.addProduct(product)
    }
}