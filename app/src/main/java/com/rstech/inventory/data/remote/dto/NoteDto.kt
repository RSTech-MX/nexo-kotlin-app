package com.rstech.inventory.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.rstech.inventory.domain.model.Note

data class NoteDto(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("titulo") @set:PropertyName("titulo") var titulo: String = "",
    @get:PropertyName("contenido") @set:PropertyName("contenido") var contenido: String = "",
    @get:PropertyName("color") @set:PropertyName("color") var color: Long = 0L,
    @get:PropertyName("fecha_creacion") @set:PropertyName("fecha_creacion") var fechaCreacion: Timestamp? = null,
    @get:PropertyName("fecha_actualizacion") @set:PropertyName("fecha_actualizacion") var fechaActualizacion: Timestamp? = null
)

fun NoteDto.toDomain(): Note {
    return Note(
        id = id,
        title = titulo,
        content = contenido,
        color = color,
        createdAt = fechaCreacion?.toDate(),
        updatedAt = fechaActualizacion?.toDate()
    )
}