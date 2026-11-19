package com.rstech.inventory.domain.usecase

import com.rstech.inventory.domain.repository.ProductRepository
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productId: String): Result<Unit> {
        if (productId.isBlank()) {
            return Result.failure(IllegalArgumentException("El ID del producto no es válido"))
        }
        return repository.deleteProduct(productId)
    }
}