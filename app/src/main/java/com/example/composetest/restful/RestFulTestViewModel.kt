package com.example.composetest.restful

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetest.MainScreen
import com.example.composetest.ui.theme.ComposeTestTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class RestFulTestViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val TAG = "RestFulTestViewModel"
    }

    private val _natureData = MutableStateFlow(Nature("안드로이드", "인터넷 어딘가...","https://developer.android.com/images/brand/Android_Robot.png"))
    val natureData:StateFlow<Nature> = _natureData
}
