package com.rstech.inventory.domain.usecase

import com.rstech.inventory.domain.model.Product
import com.rstech.inventory.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        return repository.getProductsStream()
    }
}