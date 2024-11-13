package ru.shvetsov.memehub.presentation.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.shvetsov.memehub.data.network.token.TokenStorage
import ru.shvetsov.memehub.data.requests.LoginRequest
import ru.shvetsov.memehub.data.requests.RegisterRequest
import ru.shvetsov.memehub.data.response.BaseResponse
import ru.shvetsov.memehub.data.response.UserResponse
import ru.shvetsov.memehub.domain.usecases.UserUseCase
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val tokenStorage: TokenStorage,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _registrationResult = MutableLiveData<String>()
    val registrationResult: LiveData<String> get() = _registrationResult

    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> get() = _loginResult

    private val _logoutEvent = MutableLiveData<Boolean>()
    val logoutEvent: LiveData<Boolean> get() = _logoutEvent

    private val _userProfile = MutableLiveData<UserResponse>()
    val userProfile: LiveData<UserResponse> get() = _userProfile

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userUseCase.registerUser(registerRequest)
                if (response.isSuccessful) {
                    _registrationResult.postValue(response.body()?.message)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                    _registrationResult.postValue(errorResponse.message)
                }
            } catch (e: Exception) {
                _registrationResult.postValue("Something went wrong")
            }
        }
    }

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userUseCase.loginUser(loginRequest)
                if (response.isSuccessful) {
                    tokenStorage.saveToken(response.body()?.token.toString())
                    tokenStorage.saveUserId(response.body()?.id!!.toInt())
                    _loginResult.postValue("Success")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                    _loginResult.postValue(errorResponse.message)
                }
            } catch (e: Exception) {
                _loginResult.postValue("Something went wrong ${e.message}")
            }
        }
    }

    fun getUserProfile(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val userProfile = userUseCase.getUserProfile(userId)
            _userProfile.postValue(userProfile.body())
        }
    }

    fun uploadProfileImage(imageFile: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userUseCase.uploadProfileImage(imageFile)
                if (response.isSuccessful && response.body() != null) {
                    getUserProfile(tokenStorage.getUserId())
                } else {
                    _loginResult.postValue("Failed to upload profile image")
                }
            } catch (e: Exception) {
                _loginResult.postValue("Failed to upload profile image")
            }
        }
    }



    fun logout() {
        sharedPreferences.edit().clear().apply()
        _logoutEvent.value = true
    }
}