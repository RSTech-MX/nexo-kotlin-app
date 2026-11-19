package com.rstech.inventory.domain.repository

import com.rstech.inventory.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    // Flujo en tiempo real de notas ordenadas por título
    fun getNotesStream(): Flow<List<Note>>

    // Agregar una nueva nota
    suspend fun addNote(note: Note): Result<Unit>

    // Actualizar una nota existente
    suspend fun updateNote(note: Note): Result<Unit>

    // Eliminar una nota por su ID
    suspend fun deleteNote(noteId: String): Result<Unit>
}