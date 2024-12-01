package ru.shvetsov.memehub.data.response

data class UserResponse(
    val login: String,
    val password: String,
    val username: String,
    val profilePicture: String
)