package com.rstech.inventory.domain.usecase

import com.rstech.inventory.domain.model.Purchase
import com.rstech.inventory.domain.repository.PurchaseRepository
import javax.inject.Inject

class AddPurchaseUseCase @Inject constructor(
    private val repository: PurchaseRepository
) {
    suspend operator fun invoke(purchase: Purchase): Result<Unit> {
        if (purchase.descripcion.isBlank()) {
            return Result.failure(IllegalArgumentException("La descripción es obligatoria"))
        }

        if (purchase.costo <= 0) {
            return Result.failure(IllegalArgumentException("El costo de la compra debe ser mayor a 0"))
        }

        if (purchase.proveedor.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre del proveedor es obligatorio"))
        }

        return repository.addPurchase(purchase)
    }
}