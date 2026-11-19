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

sealed class ChangeEmailUiEvent {
    data class ShowMessage(val message: String) : ChangeEmailUiEvent()
    object EmailUpdated : ChangeEmailUiEvent()
}

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    var nuevoCorreo by mutableStateOf("")
    var password by mutableStateOf("")

    var cargando by mutableStateOf(false)
        private set

    private val _uiEvent = MutableSharedFlow<ChangeEmailUiEvent>()
    val uiEvent: SharedFlow<ChangeEmailUiEvent> = _uiEvent.asSharedFlow()

    fun cambiarCorreo() {
        viewModelScope.launch {
            if (nuevoCorreo.isEmpty() || password.isEmpty()) {
                _uiEvent.emit(ChangeEmailUiEvent.ShowMessage("Por favor llena todos los campos"))
                return@launch
            }

            try {
                cargando = true
                val user = auth.currentUser
                if (user == null || user.email == null) {
                    _uiEvent.emit(ChangeEmailUiEvent.ShowMessage("No hay usuario autenticado"))
                    return@launch
                }

                // 1. Reautenticar al usuario por seguridad antes de cambiar el correo
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                user.reauthenticate(credential).await()

                // 2. Actualizar el correo en Firebase Auth
                user.verifyBeforeUpdateEmail(nuevoCorreo).await() // o usar updateEmail(nuevoCorreo) según preferencia

                _uiEvent.emit(ChangeEmailUiEvent.ShowMessage("Correo actualizado correctamente"))
                _uiEvent.emit(ChangeEmailUiEvent.EmailUpdated)
            } catch (e: FirebaseAuthException) {
                val msg = when (e.errorCode) {
                    "ERROR_WRONG_PASSWORD", "wrong-password" -> "Contraseña incorrecta"
                    "ERROR_INVALID_EMAIL", "invalid-email" -> "El formato del correo no es válido"
                    "ERROR_EMAIL_ALREADY_IN_USE", "email-already-in-use" -> "El correo ya está registrado por otra cuenta"
                    else -> "Error: ${e.localizedMessage}"
                }
                _uiEvent.emit(ChangeEmailUiEvent.ShowMessage(msg))
            } catch (e: Exception) {
                _uiEvent.emit(ChangeEmailUiEvent.ShowMessage("Error: ${e.localizedMessage}"))
            } finally {
                cargando = false
            }
        }
    }
}