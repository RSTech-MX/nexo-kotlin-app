package com.rstech.inventory.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rstech.inventory.data.remote.dto.NoteDto
import com.rstech.inventory.data.remote.dto.toDomain
import com.rstech.inventory.domain.model.Note
import com.rstech.inventory.domain.repository.NoteRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : NoteRepository {

    private val currentUid: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

    private val notesRef
        get() = firestore.collection("usuarios").document(currentUid).collection("notas")

    override fun getNotesStream(): Flow<List<Note>> = callbackFlow {
        val listener = notesRef
            .orderBy("titulo", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val notes = snapshot?.documents?.mapNotNull { doc ->
                    val dto = doc.toObject(NoteDto::class.java)
                    dto?.id = doc.id
                    dto?.toDomain()
                } ?: emptyList()

                trySend(notes)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun addNote(note: Note): Result<Unit> = runCatching {
        val noteData = hashMapOf(
            "titulo" to note.title,
            "contenido" to note.content,
            "color" to note.color,
            "fecha_creacion" to Timestamp.now()
        )
        notesRef.add(noteData).await()
    }

    override suspend fun updateNote(note: Note): Result<Unit> = runCatching {
        val updateData = hashMapOf<String, Any>(
            "titulo" to note.title,
            "contenido" to note.content,
            "color" to note.color,
            "fecha_actualizacion" to Timestamp.now()
        )
        notesRef.document(note.id).update(updateData).await()
    }

    override suspend fun deleteNote(id: String): Result<Unit> = runCatching {
        notesRef.document(id).delete().await()
    }
}