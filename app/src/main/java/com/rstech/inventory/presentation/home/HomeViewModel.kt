package com.rstech.inventory.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rstech.inventory.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class HomeUiState(
    val saludo: String = "Hola",
    val primerNombre: String = "Usuario",
    val iniciales: String = "U",
    val balanceTotal: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth // Inyectamos FirebaseAuth directamente mediante Hilt
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // Obtenemos el usuario activo mediante la instancia inyectada
                val currentUser = auth.currentUser
                var nombreCompleto = "Usuario"

                if (currentUser != null) {
                    val uid = currentUser.uid
                    val result = userRepository.getUserProfile(uid)
                    result.onSuccess { userProfile ->
                        nombreCompleto = userProfile.name.ifEmpty { "Usuario" }
                    }.onFailure {
                        nombreCompleto = "Usuario"
                    }
                }

                // Lógica de separación de nombre, iniciales y saludo
                val trimmedName = nombreCompleto.trim()
                val parts = trimmedName.split("\\s+".toRegex())

                val primerNombre = if (parts.isNotEmpty() && parts.first().isNotEmpty()) {
                    parts.first()
                } else {
                    "Usuario"
                }

                val iniciales = when {
                    parts.isEmpty() || parts.first().isEmpty() -> "U"
                    parts.size == 1 -> parts.first().take(1).uppercase()
                    else -> "${parts.first().take(1)}${parts.last().take(1)}".uppercase()
                }

                _uiState.value = _uiState.value.copy(
                    saludo = obtenerSaludoSegunHora(),
                    primerNombre = primerNombre,
                    iniciales = iniciales,
                    balanceTotal = 15420.50,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar perfil: ${e.message}"
                )
            }
        }
    }

    private fun obtenerSaludoSegunHora(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Buenos días"
            hour < 19 -> "Buenas tardes"
            else -> "Buenas noches"
        }
    }
}