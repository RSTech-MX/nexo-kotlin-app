package com.rstech.inventory.domain.repository

import com.rstech.inventory.domain.model.User
import com.rstech.inventory.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    // --- MÉTODOS DE AUTENTICACIÓN Y GESTIÓN ---
    suspend fun registerUser(user: User, password: String): Result<Unit>

    suspend fun getUserRole(uid: String): Result<String>

    fun getUsersStreamByRole(currentUid: String): Flow<List<User>>

    fun getUsersStream(): Flow<List<User>>

    suspend fun updateUser(user: User): Result<Unit>

    suspend fun deleteUser(uid: String): Result<Unit>

    // --- MÉTODOS DE PERFIL (PerfilService.dart) ---
    suspend fun getUserProfile(uid: String): Result<UserProfile>

    suspend fun updateUserProfile(
        uid: String,
        name: String,
        username: String,
        age: Int
    ): Result<Unit>

    suspend fun changeEmail(
        newEmail: String,
        currentPassword: String
    ): Result<Unit>
}