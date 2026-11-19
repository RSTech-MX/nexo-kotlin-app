package com.rstech.inventory.domain.usecase

import com.rstech.inventory.domain.repository.UserRepository

class ChangeEmailUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        newEmail: String,
        currentPassword: String
    ): Result<Unit> {
        val trimmedEmail = newEmail.trim()

        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Ingresa un correo electrónico válido"))
        }

        if (currentPassword.isBlank()) {
            return Result.failure(IllegalArgumentException("Debes ingresar tu contraseña actual para confirmar el cambio"))
        }

        return userRepository.changeEmail(
            newEmail = trimmedEmail,
            currentPassword = currentPassword
        )
    }
}