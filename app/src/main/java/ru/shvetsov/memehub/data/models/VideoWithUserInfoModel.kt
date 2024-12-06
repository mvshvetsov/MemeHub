package ru.shvetsov.memehub.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoWithUserInfoModel(
    val description: String,
    val tag: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val username: String,
    val profilePicture: String
): Parcelable
