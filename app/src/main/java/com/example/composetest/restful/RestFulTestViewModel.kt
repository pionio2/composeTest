package com.example.composetest.restful

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.features.*
import io.ktor.client.features.get
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

    private val _natureData = MutableStateFlow(Nature("안드로이드", "인터넷 어딘가...", "https://developer.android.com/images/brand/Android_Robot.png"))
    val natureData: StateFlow<Nature> = _natureData

    fun requestRestFul() {
        Log.e(TAG, "requestRestFul()")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = restFulRepository.getHttpClient().get<Nature>(RestFulTestRepository.BASE_URL + "/picture") {
                    //parameter("errorCode", 500)
                }
                Log.i(TAG, "requestRestFul() - success:$response")
                _natureData.emit(response)
            } catch (th: Throwable) {
                Log.e(TAG, "Error:Code: ${restFulRepository.getErrorStatus(th)}")
            }
        }
    }
}
