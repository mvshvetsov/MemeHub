package ru.shvetsov.memehub.domain.usecases

import retrofit2.Response
import ru.shvetsov.memehub.data.requests.UploadVideoRequest
import ru.shvetsov.memehub.data.response.BaseResponse
import ru.shvetsov.memehub.domain.repositories.VideoRepository
import java.io.File

class VideoUseCase(
    private val videoRepository: VideoRepository
) {

    suspend fun uploadVideo(uploadVideoRequest: UploadVideoRequest, videoFile: File): Response<BaseResponse> {
        return videoRepository.uploadVideo(uploadVideoRequest, videoFile)
    }
}