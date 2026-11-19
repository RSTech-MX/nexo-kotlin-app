package com.rstech.inventory.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rstech.inventory.domain.model.User
import com.rstech.inventory.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RegisterUiState {
    object Idle : RegisterUiState
    object Loading : RegisterUiState
    object Success : RegisterUiState
    data class Error(val message: String) : RegisterUiState
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository // Inyectamos tu UserRepository correctamente
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun registrarUsuario(
        nombre: String,
        nombre_usuario: String,
        edadStr: String,
        correo: String,
        password: String
    ) {
        if (nombre.isBlank() || correo.isBlank() || password.length < 6) {
            _uiState.value = RegisterUiState.Error("Datos inválidos (Verifica los campos y que la contraseña tenga mín. 6 caracteres)")
            return
        }

        val edad = edadStr.toIntOrNull() ?: 0

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            // Creamos el objeto User del dominio que espera tu UserRepositoryImpl
            val user = User(
                name = nombre.trim(),
                username = nombre_usuario.trim(),
                age = edad,
                email = correo.trim(),
                role = "user"
            )

            userRepository.registerUser(user, password.trim())
                .onSuccess {
                    _uiState.value = RegisterUiState.Success
                }
                .onFailure { exception ->
                    val mensaje = when {
                        exception.message?.contains("email-already-in-use") == true -> "El correo ya está registrado"
                        exception.message?.contains("weak-password") == true -> "La contraseña es muy débil"
                        else -> exception.localizedMessage ?: "Error al registrar usuario"
                    }
                    _uiState.value = RegisterUiState.Error(mensaje)
                }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}