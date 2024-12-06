package ru.shvetsov.memehub.domain.usecases

import retrofit2.Response
import ru.shvetsov.memehub.data.models.VideoWithUserInfoModel
import ru.shvetsov.memehub.data.requests.LoginRequest
import ru.shvetsov.memehub.data.requests.RegisterRequest
import ru.shvetsov.memehub.data.requests.UpdateRequest
import ru.shvetsov.memehub.data.response.BaseResponse
import ru.shvetsov.memehub.data.response.LoginResponse
import ru.shvetsov.memehub.data.response.UserResponse
import ru.shvetsov.memehub.domain.repositories.UserRepository
import java.io.File

class UserUseCase(
    private val userRepository: UserRepository
) {

    suspend fun registerUser(registerRequest: RegisterRequest): Response<BaseResponse?> {
        return userRepository.registerUser(registerRequest)
    }

    suspend fun loginUser(loginRequest: LoginRequest): Response<LoginResponse?> {
        return userRepository.loginUser(loginRequest)
    }

    suspend fun getUserProfile(userId: Int): Response<UserResponse> {
        return userRepository.getUserProfile(userId)
    }

    suspend fun uploadProfileImage(imageFile: File): Response<UserResponse> {
        return userRepository.uploadProfileImage(imageFile)
    }

    suspend fun updateUserProfile(userId: Int, updateRequest: UpdateRequest): Response<BaseResponse> {
        return userRepository.updateUserProfile(userId, updateRequest)
    }

    suspend fun getVideosByUserId(userId: Int): Response<List<VideoWithUserInfoModel>> {
        return userRepository.getVideosByUserId(userId)
    }

    suspend fun getVideos(): Response<List<VideoWithUserInfoModel>> {
        return userRepository.getVideos()
    }
}