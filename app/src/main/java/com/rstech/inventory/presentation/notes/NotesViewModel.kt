package com.rstech.inventory.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rstech.inventory.domain.model.Note
import com.rstech.inventory.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteItem(
    val id: String,
    val titulo: String,
    val contenido: String,
    val color: Long
)

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _notas = MutableStateFlow<List<NoteItem>>(emptyList())
    val notas: StateFlow<List<NoteItem>> = _notas.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                noteRepository.getNotesStream()
                    .catch { e ->
                        e.printStackTrace()
                        _isLoading.value = false
                    }
                    .collect { list ->
                        _notas.value = list.map { note ->
                            NoteItem(
                                id = note.id,
                                titulo = note.title,
                                contenido = note.content,
                                color = note.color.toLong()
                            )
                        }
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                _isLoading.value = false
            }
        }
    }

    fun agregarNota(titulo: String, contenido: String, color: Long) {
        viewModelScope.launch {
            try {
                val newNote = Note(
                    id = "",
                    title = titulo,
                    content = contenido,
                    color = color // <--- Sin .toInt(), pasa directo como Long
                )
                noteRepository.addNote(newNote)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun actualizarNota(id: String, titulo: String, contenido: String, color: Long) {
        viewModelScope.launch {
            try {
                val updatedNote = Note(
                    id = id,
                    title = titulo,
                    content = contenido,
                    color = color // <--- Sin .toInt(), pasa directo como Long
                )
                noteRepository.updateNote(updatedNote)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun eliminarNota(id: String) {
        viewModelScope.launch {
            try {
                noteRepository.deleteNote(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}