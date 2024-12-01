package ru.shvetsov.memehub.data.requests

data class RegisterRequest(
    val login: String,
    val username: String,
    val password: String
)