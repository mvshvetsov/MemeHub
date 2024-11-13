package ru.shvetsov.memehub.data.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import ru.shvetsov.memehub.data.requests.LoginRequest
import ru.shvetsov.memehub.data.requests.RegisterRequest
import ru.shvetsov.memehub.data.response.BaseResponse
import ru.shvetsov.memehub.data.response.LoginResponse
import ru.shvetsov.memehub.data.response.UserResponse

interface UserApi {
    @POST("/register")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): Response<BaseResponse?>

    @POST("/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse?>

    @GET("/user/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String,
        @Query("id") userId: Int
    ): Response<UserResponse>

    @Multipart
    @POST("/user/profile-picture")
    suspend fun uploadProfileImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<UserResponse>
}