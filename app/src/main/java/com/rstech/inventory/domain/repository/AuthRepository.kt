package com.rstech.inventory.domain.repository

import com.rstech.inventory.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    // Flujo en tiempo real del estado de autenticación (Equivalente al authStateChanges Stream)
    val authStateStream: Flow<Boolean>

    // Obtener el ID del usuario actualmente autenticado
    fun getCurrentUserId(): String?

    // Iniciar sesión con correo y contraseña
    suspend fun login(email: String, password: String): Result<Unit>

    // Registrar nuevo usuario
    suspend fun registrarUsuario(
        nombre: String,
        nombreUsuario: String,
        edad: Int,
        correo: String,
        password: String
    ): Result<Unit>

    // Cerrar sesión activa
    suspend fun logout(): Result<Unit>

    suspend fun updateEmail(newEmail: String, currentPassword: String): Result<Unit>
}