package com.rstech.inventory.domain.model

import java.util.Date

data class Note(
    val id: String = "",
    val title: String,
    val content: String,
    val color: Long, // Representación numérica del color en formato ARGB/Color
    val createdAt: Date? = null,
    val updatedAt: Date? = null
)