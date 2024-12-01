package ru.shvetsov.memehub.data.response

data class LoginResponse(
    val isSuccess: Boolean,
    val id: Int,
    val token: String
)
