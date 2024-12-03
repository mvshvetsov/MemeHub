package ru.shvetsov.memehub.domain.repositories

import retrofit2.Response
import ru.shvetsov.memehub.data.models.VideoModel
import ru.shvetsov.memehub.data.requests.LoginRequest
import ru.shvetsov.memehub.data.requests.RegisterRequest
import ru.shvetsov.memehub.data.requests.UpdateRequest
import ru.shvetsov.memehub.data.response.BaseResponse
import ru.shvetsov.memehub.data.response.LoginResponse
import ru.shvetsov.memehub.data.response.UserResponse
import java.io.File

interface UserRepository {

    suspend fun registerUser(registerRequest: RegisterRequest): Response<BaseResponse?>

    suspend fun loginUser(loginRequest: LoginRequest): Response<LoginResponse?>

    suspend fun getUserProfile(userId: Int): Response<UserResponse>

    suspend fun uploadProfileImage(imageFile: File): Response<UserResponse>

    suspend fun updateUserProfile(userId: Int, updateRequest: UpdateRequest): Response<BaseResponse>

    suspend fun getVideosByUserId(userId: Int): Response<List<VideoModel>>
}