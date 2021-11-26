package com.example.composetest

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.Throws

@HiltViewModel
class TestViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val TAG = "TestViewModel"
    }

    interface NetworkResult {
        fun success(result: Int)
        fun fail()
    }

    private suspend fun requestNetworkData(resultCallback: NetworkResult) {
        repeat(5) {
            delay(500)
            Log.d(TAG,"requestNetworkData() - ${Thread.currentThread().name}")
            resultCallback.success(it)
        }
    }

    private fun releaseNetwork() {
        Log.i(TAG, "releaseNetwork() - Network released")
    }

    @ExperimentalCoroutinesApi
    fun getNetworkResultFlow(): Flow<String> = callbackFlow {
        val callbackImpl = object : NetworkResult {
            override fun success(result: Int) {
                Log.d(TAG, "Network request success - $result")
                trySend("SUCCESS")
            }

            override fun fail() {
                Log.e(TAG, "Network request failed")
                trySend("ERROR")
                // 실패시 channel을 닫는다.
                close()
            }
        }

        requestNetworkData(callbackImpl)

        // coroutine scope이 cancel 또는 close될때 호출된다.
        awaitClose { releaseNetwork() }
    }
}