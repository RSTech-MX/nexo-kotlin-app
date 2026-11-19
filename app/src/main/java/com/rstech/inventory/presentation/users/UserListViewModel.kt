package com.rstech.inventory.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class UserItem(
    val id: String,
    val nombre: String,
    val nombreUsuario: String,
    val edad: String
)

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _isAdmin = MutableStateFlow<Boolean?>(null)
    val isAdmin: StateFlow<Boolean?> = _isAdmin.asStateFlow()

    private val _isMaster = MutableStateFlow(false)
    val isMaster: StateFlow<Boolean> = _isMaster.asStateFlow()

    private val _users = MutableStateFlow<List<UserItem>>(emptyList())
    val users: StateFlow<List<UserItem>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkUserPermissionsAndLoad()
    }

    private fun checkUserPermissionsAndLoad() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            try {
                // Validación de rol Admin y Master (ajusta el nombre de tu colección y campos si difieren)
                val userDoc = firestore.collection("usuarios").document(uid).get().await()
                val rol = userDoc.getString("rol") ?: ""

                val admin = rol == "admin" || rol == "master"
                val master = rol == "master"

                _isAdmin.value = admin
                _isMaster.value = master

                if (admin) {
                    loadUsers()
                }
            } catch (e: Exception) {
                _isAdmin.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadUsers() {
        firestore.collection("usuarios").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val list = snapshot.documents.map { doc ->
                    UserItem(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "Sin nombre",
                        nombreUsuario = doc.getString("nombre_usuario") ?: "",
                        edad = doc.get("edad")?.toString() ?: ""
                    )
                }
                _users.value = list
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("usuarios").document(userId).delete().await()
            } catch (e: Exception) {
                // Manejar error de eliminación si es necesario
            }
        }
    }
}