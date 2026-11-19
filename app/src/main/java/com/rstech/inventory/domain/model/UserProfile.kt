package com.rstech.inventory.domain.model

data class UserProfile(
    val uid: String,
    val name: String,
    val role: String
) {
    val isMaster: Boolean
        get() = role == "master"

    val isAdmin: Boolean
        get() = role == "admin" || role == "master"

    // Primer nombre para el saludo
    val firstName: String
        get() {
            val parts = name.trim().split("\\s+".toRegex())
            if (parts.isEmpty() || parts.first().isEmpty()) return "Usuario"
            return parts.first()
        }

    // Iniciales para el avatar
    val initials: String
        get() {
            val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
            if (parts.isEmpty()) return "U"
            if (parts.size == 1) return parts.first().first().uppercase()
            return "${parts.first().first()}${parts.last().first()}".uppercase()
        }
}