package com.rstech.inventory.domain.usecase

import com.rstech.inventory.domain.model.Purchase
import com.rstech.inventory.domain.repository.PurchaseRepository
import kotlinx.coroutines.flow.Flow

class GetPurchasesUseCase(
    private val repository: PurchaseRepository
) {
    operator fun invoke(): Flow<List<Purchase>> {
        return repository.getPurchasesStream()
    }
}