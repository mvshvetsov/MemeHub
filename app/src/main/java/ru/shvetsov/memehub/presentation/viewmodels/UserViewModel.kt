package ru.shvetsov.memehub.presentation.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.shvetsov.memehub.data.models.VideoModel
import ru.shvetsov.memehub.data.network.token.TokenStorage
import ru.shvetsov.memehub.data.requests.LoginRequest
import ru.shvetsov.memehub.data.requests.RegisterRequest
import ru.shvetsov.memehub.data.requests.UpdateRequest
import ru.shvetsov.memehub.data.response.BaseResponse
import ru.shvetsov.memehub.data.response.UserResponse
import ru.shvetsov.memehub.domain.usecases.UserUseCase
import ru.shvetsov.memehub.utils.constants.Constants.FAILED_TO_UPLOAD_PROFILE_IMAGE
import ru.shvetsov.memehub.utils.constants.Constants.SOMETHING_WENT_WRONG
import ru.shvetsov.memehub.utils.constants.Constants.SUCCESS
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
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

    private val _updateProfileResult = MutableLiveData<String>()
    val updateProfileResult: LiveData<String> get() = _updateProfileResult

    private val _userVideos = MutableLiveData<List<VideoModel>>()
    val userVideos: LiveData<List<VideoModel>> get() = _userVideos

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
                _registrationResult.postValue(SOMETHING_WENT_WRONG)
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
                    _loginResult.postValue(SUCCESS)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                    _loginResult.postValue(errorResponse.message)
                }
            } catch (e: Exception) {
                _loginResult.postValue(SOMETHING_WENT_WRONG + e.message)
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
                    _loginResult.postValue(FAILED_TO_UPLOAD_PROFILE_IMAGE)
                }
            } catch (e: Exception) {
                _loginResult.postValue(FAILED_TO_UPLOAD_PROFILE_IMAGE)
            }
        }
    }

    fun updateUserProfile(userId: Int, updateRequest: UpdateRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userUseCase.updateUserProfile(userId, updateRequest)
                if (response.isSuccessful) {
                    _updateProfileResult.postValue(response.body()?.message)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                    _updateProfileResult.postValue(errorResponse.message)
                }
            } catch (e: Exception) {
                _updateProfileResult.postValue(SOMETHING_WENT_WRONG)
            }
        }
    }

    fun getVideosByUserId(userId: Int) {
        viewModelScope.launch {
            try {
                val response = userUseCase.getVideosByUserId(userId)
                if (response.isSuccessful) {
                    _userVideos.postValue(response.body())
                } else {
                    _userVideos.postValue(emptyList())
                }
            } catch (e: Exception) {
                _userVideos.postValue(emptyList())
            }
        }
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
        _logoutEvent.value = true
    }
}