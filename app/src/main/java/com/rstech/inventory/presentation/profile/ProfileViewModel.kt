package com.rstech.inventory.presentation.profile

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

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val data: Map<String, Any>) : ProfileState()
    data class Error(val message: String) : ProfileState()
    object Unauthenticated : ProfileState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                _profileState.value = ProfileState.Unauthenticated
                return@launch
            }

            try {
                _profileState.value = ProfileState.Loading
                val document = firestore.collection("usuarios").document(user.uid).get().await()

                if (document.exists() && document.data != null) {
                    _profileState.value = ProfileState.Success(document.data!!)
                } else {
                    _profileState.value = ProfileState.Error("No se encontraron datos del usuario")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }
}