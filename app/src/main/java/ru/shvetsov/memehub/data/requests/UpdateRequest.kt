package ru.shvetsov.memehub.data.requests

data class UpdateRequest(
    val username: String? = null,
    val login: String? = null,
    val password: String? = null
)