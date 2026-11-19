package com.rstech.inventory.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class ChangePasswordUiEvent {
    data class ShowMessage(val message: String) : ChangePasswordUiEvent()
    object PasswordUpdated : ChangePasswordUiEvent()
}

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    var passwordActual by mutableStateOf("")
    var passwordNueva by mutableStateOf("")

    var cargando by mutableStateOf(false)
        private set

    private val _uiEvent = MutableSharedFlow<ChangePasswordUiEvent>()
    val uiEvent: SharedFlow<ChangePasswordUiEvent> = _uiEvent.asSharedFlow()

    fun cambiarPassword() {
        viewModelScope.launch {
            if (passwordActual.isEmpty() || passwordNueva.length < 6) {
                _uiEvent.emit(ChangePasswordUiEvent.ShowMessage("Datos inválidos (la contraseña debe tener al menos 6 caracteres)"))
                return@launch
            }

            try {
                cargando = true
                val user = auth.currentUser
                if (user == null || user.email == null) {
                    _uiEvent.emit(ChangePasswordUiEvent.ShowMessage("No hay usuario autenticado"))
                    return@launch
                }

                // 1. Crear la credencial correctamente en Android (getCredential)
                val credential = EmailAuthProvider.getCredential(user.email!!, passwordActual)

                // 2. Reautenticar usando .reauthenticate(...)
                user.reauthenticate(credential).await()

                // 3. Actualizar contraseña
                user.updatePassword(passwordNueva).await()

                _uiEvent.emit(ChangePasswordUiEvent.ShowMessage("Contraseña actualizada"))
                _uiEvent.emit(ChangePasswordUiEvent.PasswordUpdated)
            } catch (e: FirebaseAuthException) {
                val msg = when (e.errorCode) {
                    "ERROR_WRONG_PASSWORD", "wrong-password" -> "Contraseña incorrecta"
                    "ERROR_WEAK_PASSWORD", "weak-password" -> "Contraseña muy débil"
                    "ERROR_REQUIRES_RECENT_LOGIN", "requires-recent-login" -> "Vuelve a iniciar sesión"
                    else -> "Error de autenticación: ${e.localizedMessage}"
                }
                _uiEvent.emit(ChangePasswordUiEvent.ShowMessage(msg))
            } catch (e: Exception) {
                _uiEvent.emit(ChangePasswordUiEvent.ShowMessage("Error: ${e.localizedMessage}"))
            } finally {
                cargando = false
            }
        }
    }
}