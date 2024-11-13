package ru.shvetsov.memehub.domain.repositories

import android.net.Uri
import retrofit2.Response
import ru.shvetsov.memehub.data.requests.LoginRequest
import ru.shvetsov.memehub.data.requests.RegisterRequest
import ru.shvetsov.memehub.data.response.BaseResponse
import ru.shvetsov.memehub.data.response.LoginResponse
import ru.shvetsov.memehub.data.response.UserResponse
import java.io.File

interface UserRepository {

    suspend fun registerUser(registerRequest: RegisterRequest): Response<BaseResponse?>

    suspend fun loginUser(loginRequest: LoginRequest): Response<LoginResponse?>

    suspend fun getUserProfile(userId: Int): Response<UserResponse>

    suspend fun uploadProfileImage(imageFile: File): Response<UserResponse>
}