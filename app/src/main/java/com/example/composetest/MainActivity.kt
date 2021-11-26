package com.example.composetest

import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composetest.calendar.EventInfo
import com.example.composetest.calendar.EventItem

import com.example.composetest.ui.theme.ComposeTestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()
    private val testViewModel: TestViewModel by viewModels()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestTheme {
                Log.d("MainActivity", "Compose Start!")
                Text("Hello world")
            }
        }

        MainScope().launch(Dispatchers.IO) {
            testViewModel.getNetworkResultFlow().collect {
                Log.i(TAG, "Network result#1: $it - ${Thread.currentThread().name}")
            }
            Log.d(TAG,"Collect End #1 - ${Thread.currentThread().name}")
        }

//        MainScope().launch {
//            testViewModel.getNetworkResultFlow().collect {
//                Log.i(TAG, "Network result#2: $it")
//                Log.d(TAG,"Collect End #1")
//            }
//        }
    }
}

@InternalCoroutinesApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTestTheme {

    }
}