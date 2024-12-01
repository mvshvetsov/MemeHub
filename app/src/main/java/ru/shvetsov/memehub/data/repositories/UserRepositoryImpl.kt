package ru.shvetsov.memehub.data.repositories
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import ru.shvetsov.memehub.data.network.RetrofitInstance.Companion.apiService
import ru.shvetsov.memehub.data.network.token.TokenStorage
import ru.shvetsov.memehub.data.requests.LoginRequest
import ru.shvetsov.memehub.data.requests.RegisterRequest
import ru.shvetsov.memehub.data.requests.UpdateRequest
import ru.shvetsov.memehub.data.response.BaseResponse
import ru.shvetsov.memehub.data.response.LoginResponse
import ru.shvetsov.memehub.data.response.UserResponse
import ru.shvetsov.memehub.domain.repositories.UserRepository
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val tokenStorage: TokenStorage
) : UserRepository {

    override suspend fun registerUser(registerRequest: RegisterRequest): Response<BaseResponse?> {
        return try {
            val response = apiService.registerUser(registerRequest)
            response
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun loginUser(loginRequest: LoginRequest): Response<LoginResponse?> {
        val response = apiService.loginUser(loginRequest)
        return response
    }

    override suspend fun getUserProfile(userId: Int): Response<UserResponse> {
        val token = tokenStorage.getToken()
        val response = apiService.getUserProfile("Bearer $token", userId)
        return response
    }

    override suspend fun uploadProfileImage(imageFile: File): Response<UserResponse> {
        val token = tokenStorage.getToken()
        val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        return apiService.uploadProfileImage("Bearer $token", body)
    }

    override suspend fun updateUserProfile(userId: Int, updateRequest: UpdateRequest): Response<BaseResponse> {
        val token = tokenStorage.getToken()
        val response = apiService.updateUserProfile("Bearer $token", userId, updateRequest)
        return response
    }
}