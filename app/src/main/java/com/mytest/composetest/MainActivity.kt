package com.mytest.composetest

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Space

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mytest.composetest.billing.ui.BillingTestActivity
import com.mytest.composetest.calendar.EventInfo
import com.mytest.composetest.calendar.EventItem
import com.mytest.composetest.restful.RestFulTestActivity
import com.mytest.composetest.ui.common.PressStateButton
import com.mytest.composetest.ui.common.pressStateButtonColors

import com.mytest.composetest.ui.theme.ComposeTestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import java.util.*
import kotlin.coroutines.suspendCoroutine


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()
    private val testViewModel: TestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestTheme {
                MainScreen {
                    when (it) {
                        is KtorTest -> {
                            Intent(this, RestFulTestActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }.also {
                                startActivity(it)
                            }
                        }
                        is InappTest -> {
                            Intent(this, BillingTestActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }.also {
                                startActivity(it)
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed interface MainButton
object KtorTest : MainButton
object InappTest : MainButton

@Composable
fun MainScreen(buttonClick: (MainButton) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Button(
                onClick = { buttonClick(KtorTest) }
            ) {
                Text("Go RestFulApi Test!")
            }
            Spacer(modifier = Modifier.padding(2.dp))
            PressStateButton(colors = ButtonDefaults.pressStateButtonColors(backgroundPressColor = Color.Yellow,
            contentPressColor = Color.Black),
                onClick = { buttonClick(InappTest) }
            ) {
                Text("Go InApp Buy Test!")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTestTheme {
        MainScreen({})
    }
}