package ru.shvetsov.memehub.domain.repositories

import retrofit2.Response
import ru.shvetsov.memehub.data.requests.UploadVideoRequest
import ru.shvetsov.memehub.data.response.BaseResponse
import java.io.File

interface VideoRepository {

    suspend fun uploadVideo(uploadVideoRequest: UploadVideoRequest, videoFile: File): Response<BaseResponse>
}