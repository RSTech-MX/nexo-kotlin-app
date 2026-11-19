package com.rstech.inventory.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class EditProfileUiEvent {
    data class ShowMessage(val message: String) : EditProfileUiEvent()
    object ProfileUpdated : EditProfileUiEvent()
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    var nombre by mutableStateOf("")
    var usuario by mutableStateOf("")
    var edad by mutableStateOf("")

    var cargando by mutableStateOf(true)
        private set

    var guardando by mutableStateOf(false)
        private set

    private val _uiEvent = MutableSharedFlow<EditProfileUiEvent>()
    val uiEvent: SharedFlow<EditProfileUiEvent> = _uiEvent.asSharedFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                cargando = false
                return@launch
            }

            try {
                cargando = true
                val doc = firestore.collection("usuarios").document(user.uid).get().await()
                if (doc.exists()) {
                    nombre = doc.getString("nombre") ?: ""
                    usuario = doc.getString("nombre_usuario") ?: ""
                    edad = doc.getLong("edad")?.toString() ?: doc.getString("edad") ?: ""
                }
            } catch (e: Exception) {
                _uiEvent.emit(EditProfileUiEvent.ShowMessage("Error al cargar datos: ${e.localizedMessage}"))
            } finally {
                cargando = false
            }
        }
    }

    fun guardarCambios() {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                _uiEvent.emit(EditProfileUiEvent.ShowMessage("No hay usuario autenticado"))
                return@launch
            }

            try {
                guardando = true
                val edadInt = edad.toIntOrNull() ?: 0

                val datosActualizados = mapOf(
                    "nombre" to nombre,
                    "nombre_usuario" to usuario,
                    "edad" to edadInt
                )

                firestore.collection("usuarios").document(user.uid).update(datosActualizados).await()

                _uiEvent.emit(EditProfileUiEvent.ProfileUpdated)
            } catch (e: Exception) {
                _uiEvent.emit(EditProfileUiEvent.ShowMessage("Error al actualizar: ${e.localizedMessage}"))
            } finally {
                guardando = false
            }
        }
    }
}