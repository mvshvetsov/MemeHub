package ru.shvetsov.memehub.data.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val isSuccess: Boolean,
    val id: Int,
    val token: String
)
