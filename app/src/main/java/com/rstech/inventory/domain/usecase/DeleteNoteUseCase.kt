package com.rstech.inventory.domain.usecase

import com.rstech.inventory.domain.repository.NoteRepository

class DeleteNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(noteId: String): Result<Unit> {
        if (noteId.isBlank()) {
            return Result.failure(IllegalArgumentException("El ID de la nota no es válido"))
        }
        return repository.deleteNote(noteId)
    }
}