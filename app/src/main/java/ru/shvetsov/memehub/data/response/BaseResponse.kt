package ru.shvetsov.memehub.data.response

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse(
    val isSuccess: Boolean,
    val message: String
)
