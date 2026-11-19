package com.rstech.inventory.domain.usecase

import com.rstech.inventory.domain.model.Note
import com.rstech.inventory.domain.repository.NoteRepository

class SaveNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(
        id: String = "",
        title: String,
        content: String,
        color: Long
    ): Result<Unit> {
        val trimmedTitle = title.trim()
        val trimmedContent = content.trim()

        if (trimmedTitle.isBlank() && trimmedContent.isBlank()) {
            return Result.failure(IllegalArgumentException("La nota no puede estar completamente vacía"))
        }

        val note = Note(
            id = id,
            title = if (trimmedTitle.isBlank()) "Sin título" else trimmedTitle,
            content = trimmedContent,
            color = color
        )

        return if (id.isBlank()) {
            repository.addNote(note)
        } else {
            repository.updateNote(note)
        }
    }
}