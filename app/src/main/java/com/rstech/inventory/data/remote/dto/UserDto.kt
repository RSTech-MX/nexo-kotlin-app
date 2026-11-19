package com.rstech.inventory.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.rstech.inventory.domain.model.User
import com.rstech.inventory.domain.model.UserProfile

data class UserDto(
    @get:PropertyName("uid") @set:PropertyName("uid") var uid: String = "",
    @get:PropertyName("nombre") @set:PropertyName("nombre") var nombre: String = "",
    @get:PropertyName("nombre_usuario") @set:PropertyName("nombre_usuario") var nombreUsuario: String = "",
    @get:PropertyName("edad") @set:PropertyName("edad") var edad: Int = 0,
    @get:PropertyName("correo") @set:PropertyName("correo") var correo: String = "",
    @get:PropertyName("rol") @set:PropertyName("rol") var rol: String = "user",
    @get:PropertyName("fecha_creacion") @set:PropertyName("fecha_creacion") var fechaCreacion: Timestamp? = null,
    @get:PropertyName("fecha_actualizacion") @set:PropertyName("fecha_actualizacion") var fechaActualizacion: Timestamp? = null
)

// Mappers para convertir DTO -> Domain Models
fun UserDto.toDomainUser(): User {
    return User(
        uid = uid,
        name = nombre,
        username = nombreUsuario,
        age = edad,
        email = correo,
        role = rol,
        createdAt = fechaCreacion?.toDate(),
        updatedAt = fechaActualizacion?.toDate()
    )
}

fun UserDto.toDomainUserProfile(): UserProfile {
    return UserProfile(
        uid = uid,
        name = nombre,
        role = rol
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        uid = uid,
        nombre = name,
        nombreUsuario = username,
        edad = age,
        correo = email,
        rol = role
    )
}