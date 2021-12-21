package com.example.composetest

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class TestViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val TAG = "TestViewModel"
    }

    private val _stateFlow = MutableStateFlow(99)
    val stateFlow = _stateFlow

    private val _sharedFlow = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val sharedFlow = _sharedFlow

    suspend fun startSendDataToSharedFlow() {
        repeat(10) {
            _sharedFlow.emit(it)
            delay(500)
        }
    }

    suspend fun startSendDataToStateFlow() {
        repeat(10) {
            _stateFlow.value = it
            delay(500)
        }
    }

    suspend fun repeatSameDataToEachFlow() {
        repeat(5) {
            Log.d(TAG, "sendData #$it")
            _sharedFlow.emit(100)
            _stateFlow.value = 100
            delay(500)
        }
    }

    private val _connectionFlow = flow {
        initHeavyLogic() //Expensive logic
        var i = 0
        while (true) {
            delay(500)
            emit(i++)
        }
    }
    val connectionFlow = _connectionFlow
    val connectionSharedFlow = _connectionFlow.buffer(0).shareIn(viewModelScope, SharingStarted.WhileSubscribed(0, 0), 0)

    private suspend fun initHeavyLogic() {
        delay(1000)
        Log.d(TAG, "initHeavyLogic()")
    }

    interface NetworkResult {
        fun success(resultCode: Int)
        fun fail(cause: Throwable)
    }

    private fun requestNetwork(resultCallback: NetworkResult) {
        viewModelScope.launch {
            delay(1000)
            resultCallback.success(200)
//            resultCallback.fail(Exception("Network access failed!!"))
        }
    }


    private fun releaseNetwork() {
        Log.i(TAG, "releaseNetwork() - Network released")
    }

    @ExperimentalCoroutinesApi
    suspend fun connectNetwork(): Int {
        Log.i(TAG, "connectNetwork() START!")

        val result = suspendCancellableCoroutine<Int> { continuation ->
            Log.i(TAG, "suspendCancellableCoroutine START!")
            val callbackImpl = object : NetworkResult {
                override fun success(resultCode: Int) {
                    Log.d(TAG, "Network request success - $resultCode")
                    continuation.resume(resultCode) {
                        Log.i(TAG, "resume with release request!")
                        releaseNetwork()
                    }
                }

                override fun fail(cause: Throwable) {
                    Log.e(TAG, "Network request failed!!")
                    continuation.resumeWithException(cause)
                }
            }

            // coroutine scope이 cancel 될때 호출된다.
            continuation.invokeOnCancellation {
                Log.i(TAG, "Release request!")
                releaseNetwork()
            }

            requestNetwork(callbackImpl)
            Log.i(TAG, "suspendCancellableCoroutine END!")
        }

        Log.i(TAG, "connectNetwork() END!")
        return result
    }
}