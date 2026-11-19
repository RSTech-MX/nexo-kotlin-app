package com.rstech.inventory.domain.usecase

import com.rstech.inventory.domain.model.UserProfile
import com.rstech.inventory.domain.repository.UserRepository

class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String): Result<UserProfile> {
        if (uid.isBlank()) {
            return Result.failure(IllegalArgumentException("El UID del usuario no puede estar vacío"))
        }
        return userRepository.getUserProfile(uid)
    }
}