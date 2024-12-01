package ru.shvetsov.memehub.data.repositories

import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import ru.shvetsov.memehub.data.network.RetrofitInstance.Companion.apiService
import ru.shvetsov.memehub.data.network.token.TokenStorage
import ru.shvetsov.memehub.data.requests.UploadVideoRequest
import ru.shvetsov.memehub.data.response.BaseResponse
import ru.shvetsov.memehub.domain.repositories.VideoRepository
import java.io.File
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val tokenStorage: TokenStorage
) : VideoRepository {
    override suspend fun uploadVideo(
        uploadVideoRequest: UploadVideoRequest,
        videoFile: File
    ): Response<BaseResponse> {
        val gson = Gson()
        val token = tokenStorage.getToken()
        val videoRequestJson = gson.toJson(uploadVideoRequest)
        val videoRequestBody = videoRequestJson.toRequestBody("application/json".toMediaTypeOrNull())
        val videoRequestPart = MultipartBody.Part.createFormData("videoRequest", null, videoRequestBody)
        val videoFileRequestBody = videoFile.asRequestBody("video/mp4".toMediaTypeOrNull())
        val videoPart = MultipartBody.Part.createFormData("video", videoFile.name, videoFileRequestBody)
        return apiService.uploadVideo("Bearer $token", videoRequestPart, videoPart)
    }
}