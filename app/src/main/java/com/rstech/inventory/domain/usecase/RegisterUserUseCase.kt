package com.rstech.inventory.domain.usecase

import com.rstech.inventory.domain.model.User
import com.rstech.inventory.domain.repository.UserRepository

class RegisterUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        name: String,
        username: String,
        age: Int,
        email: String,
        password: String
    ): Result<Unit> {
        // Reglas de negocio / Validaciones previas al registro
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre no puede estar vacío"))
        }
        if (email.isBlank() || !email.contains("@")) {
            return Result.failure(IllegalArgumentException("Ingresa un correo electrónico válido"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("La contraseña debe tener al menos 6 caracteres"))
        }

        val newUser = User(
            name = name,
            username = username,
            age = age,
            email = email
        )

        return userRepository.registerUser(newUser, password)
    }
}