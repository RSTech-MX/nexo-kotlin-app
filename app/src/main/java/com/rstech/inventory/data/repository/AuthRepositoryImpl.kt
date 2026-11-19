package com.rstech.inventory.data.repository

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rstech.inventory.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject // 1. Importar Inject

class AuthRepositoryImpl @Inject constructor( // 2. Agregar @Inject constructor
    private val auth: FirebaseAuth,           // 3. Remover "= FirebaseAuth.getInstance()"
    private val firestore: FirebaseFirestore  // 4. Remover "= FirebaseFirestore.getInstance()"
) : AuthRepository {

    override val authStateStream: Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val isUserLoggedIn = firebaseAuth.currentUser != null
            trySend(isUserLoggedIn)
        }

        auth.addAuthStateListener(listener)

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
    }

    override suspend fun registrarUsuario(
        nombre: String,
        nombreUsuario: String,
        edad: Int,
        correo: String,
        password: String
    ): Result<Unit> = runCatching {
        val authResult = auth.createUserWithEmailAndPassword(correo.trim(), password.trim()).await()
        val uid = authResult.user?.uid ?: throw IllegalStateException("No se obtuvo el UID del usuario")

        val datosUsuario = hashMapOf(
            "uid" to uid,
            "nombre" to nombre.trim(),
            "nombreUsuario" to nombreUsuario.trim(),
            "edad" to edad,
            "correo" to correo.trim()
        )

        firestore.collection("usuarios")
            .document(uid)
            .set(datosUsuario)
            .await()
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        auth.signOut()
    }

    override suspend fun updateEmail(newEmail: String, currentPassword: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("Usuario no autenticado")
        val currentEmail = user.email ?: throw IllegalStateException("El usuario no tiene un correo registrado")

        val credential = EmailAuthProvider.getCredential(currentEmail, currentPassword)
        user.reauthenticate(credential).await()

        user.updateEmail(newEmail.trim()).await()

        firestore.collection("usuarios")
            .document(user.uid)
            .update("correo", newEmail.trim())
            .await()
    }
}