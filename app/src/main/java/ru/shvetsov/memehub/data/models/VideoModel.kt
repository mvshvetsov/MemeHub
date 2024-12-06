package ru.shvetsov.memehub.data.models


data class VideoModel(
    val id: Int,
    val userId: Int,
    val description: String,
    val tag: String,
    val videoUrl: String,
    val thumbnailUrl: String
)