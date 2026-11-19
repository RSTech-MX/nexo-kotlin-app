package com.rstech.inventory.domain.usecase

import com.rstech.inventory.domain.repository.UserRepository

class UpdateUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        uid: String,
        name: String,
        username: String,
        age: Int
    ): Result<Unit> {
        val trimmedName = name.trim()
        val trimmedUsername = username.trim()

        if (trimmedName.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre no puede estar vacío"))
        }
        if (trimmedUsername.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre de usuario no puede estar vacío"))
        }
        if (age <= 0) {
            return Result.failure(IllegalArgumentException("Ingresa una edad válida"))
        }

        return userRepository.updateUserProfile(
            uid = uid,
            name = trimmedName,
            username = trimmedUsername,
            age = age
        )
    }
}