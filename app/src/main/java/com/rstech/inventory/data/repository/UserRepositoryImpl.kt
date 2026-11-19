package com.rstech.inventory.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rstech.inventory.data.remote.dto.UserDto
import com.rstech.inventory.data.remote.dto.toDomainUser
import com.rstech.inventory.data.remote.dto.toDomainUserProfile
import com.rstech.inventory.domain.model.User
import com.rstech.inventory.domain.model.UserProfile
import com.rstech.inventory.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val usersRef get() = firestore.collection("usuarios")

    override suspend fun registerUser(user: User, password: String): Result<Unit> = runCatching {
        val authResult = auth.createUserWithEmailAndPassword(user.email.trim(), password.trim()).await()
        val uid = authResult.user?.uid ?: throw IllegalStateException("UID nulo al crear usuario")

        val userMap = hashMapOf(
            "uid" to uid,
            "nombre" to user.name,
            "nombre_usuario" to user.username,
            "edad" to user.age,
            "correo" to user.email.trim(),
            "rol" to "user",
            "fecha_creacion" to Timestamp.now()
        )

        usersRef.document(uid).set(userMap).await()
    }

    override suspend fun getUserRole(uid: String): Result<String> = runCatching {
        val snapshot = usersRef.document(uid).get().await()
        if (!snapshot.exists()) return@runCatching "user"
        snapshot.getString("rol") ?: "user"
    }

    override fun getUsersStreamByRole(currentUid: String): Flow<List<User>> = callbackFlow {
        val roleResult = getUserRole(currentUid).getOrDefault("user")

        if (roleResult == "admin" || roleResult == "master") {
            val listener = usersRef.orderBy("nombre", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val users = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(UserDto::class.java)?.toDomainUser()
                    } ?: emptyList()
                    trySend(users)
                }
            awaitClose { listener.remove() }
        } else {
            trySend(emptyList())
            close()
        }
    }

    override fun getUsersStream(): Flow<List<User>> = callbackFlow {
        val listener = usersRef.orderBy("nombre", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val users = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(UserDto::class.java)?.toDomainUser()
                } ?: emptyList()
                trySend(users)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateUser(user: User): Result<Unit> = runCatching {
        val updates = hashMapOf<String, Any>(
            "nombre" to user.name,
            "nombre_usuario" to user.username,
            "edad" to user.age,
            "fecha_actualizacion" to Timestamp.now()
        )
        usersRef.document(user.uid).update(updates).await()
    }

    override suspend fun deleteUser(uid: String): Result<Unit> = runCatching {
        usersRef.document(uid).delete().await()
    }

    override suspend fun getUserProfile(uid: String): Result<UserProfile> = runCatching {
        val snapshot = usersRef.document(uid).get().await()
        val dto = snapshot.toObject(UserDto::class.java) ?: throw NoSuchElementException("Perfil no encontrado")
        dto.toDomainUserProfile()
    }

    override suspend fun updateUserProfile(
        uid: String,
        name: String,
        username: String,
        age: Int
    ): Result<Unit> = runCatching {
        val updates = hashMapOf<String, Any>(
            "nombre" to name.trim(),
            "nombre_usuario" to username.trim(),
            "edad" to age
        )
        usersRef.document(uid).update(updates).await()
    }

    override suspend fun changeEmail(
        newEmail: String,
        currentPassword: String
    ): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("No hay sesión activa")
        val userEmail = user.email ?: throw IllegalStateException("Correo actual de usuario no disponible")

        // Re-autenticación obligatoria
        val credential = EmailAuthProvider.getCredential(userEmail, currentPassword)
        user.reauthenticate(credential).await()

        // Cambiar correo en Firebase Auth
        user.updateEmail(newEmail.trim()).await()

        // Actualizar documento en Firestore
        usersRef.document(user.uid).update("correo", newEmail.trim()).await()
    }
}