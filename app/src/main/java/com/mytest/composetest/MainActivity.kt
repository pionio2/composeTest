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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.mytest.composetest.animation.AnimationMain
import com.mytest.composetest.billing.ui.BillingTestActivity
import com.mytest.composetest.calendar.EventInfo
import com.mytest.composetest.calendar.EventItem
import com.mytest.composetest.coroutinetest.CallbackFlowTest
import com.mytest.composetest.coroutinetest.FlowTestViewModel
import com.mytest.composetest.friend.ClickMovePage
import com.mytest.composetest.friend.FriendsListMainView
import com.mytest.composetest.friend.FriendsListViewModel
import com.mytest.composetest.performance.PerformanceActivity
import com.mytest.composetest.performance.PerformanceTestScreen
import com.mytest.composetest.restful.RestFulTestActivity
import com.mytest.composetest.sealed.Result1
import com.mytest.composetest.sealed.SealedClassTest
import com.mytest.composetest.ui.common.PressStateButton
import com.mytest.composetest.ui.common.pressStateButtonColors

import com.mytest.composetest.ui.theme.ComposeTestTheme
import com.mytest.composetest.util.LogError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import java.lang.NullPointerException
import java.util.*
import kotlin.coroutines.suspendCoroutine


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()
    private val testViewModel: TestViewModel by viewModels()
    private val flowTestViewModel: FlowTestViewModel by viewModels()
    private val callbackFlowTestViewModel: CallbackFlowTestViewModel by viewModels()
    private val friendViewModel: FriendsListViewModel by viewModels()

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestTheme {
                Scaffold(modifier = Modifier.semantics {
                    // uiAutomator 사용을 위한 추가.
                    testTagsAsResourceId = true
                }) { scaffoldPadding ->
//                    val friendPagingItems = friendViewModel.friendsListFlow.collectAsLazyPagingItems()
                    val friendItems by friendViewModel.friendsListFlow.collectAsState()
                    FriendsListMainView(Modifier.padding(scaffoldPadding), friendItems) {
                        when (it) {
                            is ClickMovePage -> {
                                //do something
                            }
                        }
                    }


//                PerformanceTestScreen()

//                    MainScreen(modifier = Modifier.padding(scaffoldPadding)) {
//                        when (it) {
//                            is KtorTest -> {
//                                Intent(this, RestFulTestActivity::class.java).apply {
//                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                }.also {
//                                    startActivity(it)
//                                }
//                            }
//                            is InappTest -> {
//                                Intent(this, BillingTestActivity::class.java).apply {
//                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                }.also {
//                                    startActivity(it)
//                                }
//                            }
//                            is Performance -> {
//                                Intent(this, PerformanceActivity::class.java).apply {
//                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                }.also {
//                                    startActivity(it)
//                                }
//                            }
//                        }
//                    }
//                AnimationMain()
                }
            }
        }
    }
}


sealed interface MainButton
object KtorTest : MainButton
object InappTest : MainButton
object Performance : MainButton

@Composable
fun MainScreen(modifier: Modifier = Modifier, buttonClick: (MainButton) -> Unit) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Button(
                onClick = { buttonClick(KtorTest) }
            ) {
                Text("Go RestFulApi Test!")
            }
            Spacer(modifier = Modifier.padding(2.dp))
            PressStateButton(colors = ButtonDefaults.pressStateButtonColors(
                backgroundPressColor = Color.Yellow,
                contentPressColor = Color.Black
            ),
                onClick = { buttonClick(InappTest) }
            ) {
                Text("Go InApp Buy Test!")
            }
            Spacer(modifier = Modifier.padding(2.dp))
            Button(
                onClick = { buttonClick(Performance) }
            ) {
                Text("Go Performance Test!")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTestTheme {
        MainScreen() {}
    }
}