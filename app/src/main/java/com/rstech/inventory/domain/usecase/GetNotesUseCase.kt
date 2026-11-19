package com.rstech.inventory.domain.usecase

import com.rstech.inventory.domain.model.Note
import com.rstech.inventory.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getNotesStream()
    }
}