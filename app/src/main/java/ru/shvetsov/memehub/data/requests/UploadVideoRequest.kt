package ru.shvetsov.memehub.data.requests

data class UploadVideoRequest(
    val userId: Int,
    val description: String,
    val tag: String
)