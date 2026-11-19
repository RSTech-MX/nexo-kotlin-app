package com.rstech.inventory.domain.usecase

import com.rstech.inventory.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetAuthStateUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return authRepository.authStateStream
    }
}