package com.rstech.inventory.domain.model

import java.util.Date

data class User(
    val uid: String = "",
    val name: String,
    val username: String,
    val age: Int,
    val email: String,
    val role: String = "user",
    val createdAt: Date? = null,
    val updatedAt: Date? = null
) {
    val isMaster: Boolean
        get() = role == "master"

    val isAdmin: Boolean
        get() = role == "admin" || role == "master"
}