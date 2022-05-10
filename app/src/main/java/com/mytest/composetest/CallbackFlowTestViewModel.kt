package com.mytest.composetest

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytest.composetest.coroutinetest.CallbackFlowTest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class CallbackFlowTestViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val TAG = "CallbackFlowTestViewModel"
    }

    fun startCallbackFlowTest() {
        viewModelScope.launch {
            CallbackFlowTest().test()
        }
    }

    fun startCallbackFlowTest3() {
        CallbackFlowTest().callbackFlowTest2()
    }

    fun startCallbackFlowTest2() = CallbackFlowTest().test2()

    fun startCallbackFlowTest4() {
        viewModelScope.launch {
            CallbackFlowTest().getCallbackFlowTest4()
        }
    }
}