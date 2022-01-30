package com.mytest.composetest.restful

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.data.PictureRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestFulTestViewModel @Inject constructor(private val restFulRepository: RestFulTestRepository) : ViewModel() {
    companion object {
        private const val TAG = "RestFulTestViewModel"
    }

    private val _pictureData = MutableStateFlow(Picture("안드로이드", "인터넷 어딘가...", "https://developer.android.com/images/brand/Android_Robot.png"))
    val pictureData: StateFlow<Picture> = _pictureData

    fun requestPicture() {
        Log.e(TAG, "requestRestFul()")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = restFulRepository.getPictureByGet(0)
//                val response = restFulRepository.getPictureByPost(PictureRequest(1))
                Log.i(TAG, "requestRestFul() - success:$response")
                _pictureData.emit(response)
            } catch (th: Throwable) {
                Log.e(TAG, "Error:Code: ${restFulRepository.getErrorStatus(th)}")
            }
        }
    }
}
