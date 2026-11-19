package com.rstech.inventory.domain.usecase

import com.rstech.inventory.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Ingresa un correo electrónico válido"))
        }

        if (trimmedPassword.isBlank()) {
            return Result.failure(IllegalArgumentException("La contraseña no puede estar vacía"))
        }

        return authRepository.login(trimmedEmail, trimmedPassword)
    }
}