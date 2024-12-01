package ru.shvetsov.memehub.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.shvetsov.memehub.data.requests.UploadVideoRequest
import ru.shvetsov.memehub.domain.usecases.VideoUseCase
import ru.shvetsov.memehub.utils.constants.Constants.FAILED_TO_UPLOAD_VIDEO
import ru.shvetsov.memehub.utils.constants.Constants.SOMETHING_WENT_WRONG
import ru.shvetsov.memehub.utils.constants.Constants.VIDEO_UPLOAD_SUCCESSFULLY
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val videoUseCase: VideoUseCase
): ViewModel() {

    private val _uploadVideoResult = MutableLiveData<String>()
    val uploadVideoResult: LiveData<String> get() = _uploadVideoResult

    fun uploadVideo(uploadVideoRequest: UploadVideoRequest, videoFile: File) {
        viewModelScope.launch {
            try {
                val response = videoUseCase.uploadVideo(uploadVideoRequest, videoFile)
                if (response.isSuccessful) {
                    _uploadVideoResult.postValue(VIDEO_UPLOAD_SUCCESSFULLY)
                } else {
                    _uploadVideoResult.postValue(FAILED_TO_UPLOAD_VIDEO + response.errorBody()?.string())
                }
            } catch (e: Exception) {
                _uploadVideoResult.postValue(SOMETHING_WENT_WRONG)
            }
        }
    }
}