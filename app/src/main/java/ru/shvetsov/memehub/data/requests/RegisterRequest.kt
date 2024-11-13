package ru.shvetsov.memehub.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val login: String,
    val username: String,
    val password: String
)
