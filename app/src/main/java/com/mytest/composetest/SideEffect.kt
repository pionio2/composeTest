package com.mytest.composetest

import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.*

@Composable
fun SideEffectScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    mainViewModel: MainViewModel = viewModel()
) {
    Log.e("composeTest", "[${Thread.currentThread().name}] MyScreen called")

    val isError by mainViewModel.error.observeAsState()

    if (isError == true) {
        // `LaunchedEffect` will cancel and re-launch if
        // `scaffoldState.snackbarHostState` changes

        LaunchedEffect(isError) {
            // Show snackbar using a coroutine, when the coroutine is cancelled the
            // snackbar will automatically dismiss. This coroutine will cancel whenever
            // `state.hasError` is false, and only start when `state.hasError` is true
            // (due to the above if-check), or if `scaffoldState.snackbarHostState` changes.
            try {
                Log.e("composeTest", "[${Thread.currentThread().name}] show snackbar1")
                withContext(Dispatchers.IO) {
                    Log.e("composeTest", "[${Thread.currentThread().name}] show snackbar2")
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = "Error message",
                        actionLabel = "Retry message"
                    )
                }
            } catch (ce: CancellationException) {
                Log.e("composeTest", "canceled!!")
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            val onTimeout by mainViewModel.timeoutText.collectAsState()

            HelloScreen()
            Box {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ShowSnackBarButton(scaffoldState)
                    StartTimeoutButton()
                    ShowLazySnackBar(scaffoldState, onTimeout)
                }
            }
        }
    }
}

@Composable
fun SideEffectTest(timeoutText: String) {

    SideEffect {
    }
    Text(timeoutText)
}

@Composable
fun BackHandler(backDispatcher: OnBackPressedDispatcher,
                isEnable: Boolean = false,
                onBack: () -> Unit) {

    // Safely update the current `onBack` lambda when a new one is provided
    val currentOnBack by rememberUpdatedState(onBack)

    // Remember in Composition a back callback that calls the `onBack` lambda
    val backCallback = remember {
        // Always intercept back events. See the SideEffect for
        // a more complete version
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBack()
            }
        }
    }

    SideEffect {
        backCallback.isEnabled = isEnable
    }

    // If `backDispatcher` changes, dispose and reset the effect
    DisposableEffect(backDispatcher) {
        // Add callback to the backDispatcher
        backDispatcher.addCallback(backCallback)

        // When the effect leaves the Composition, remove the callback
        onDispose {
            backCallback.remove()
        }
    }
}

@Composable
fun StartTimeoutButton(mainViewModel: MainViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    Button(onClick = {
        scope.launch {
            mainViewModel.changeTimeoutText("Timeout changed by button")
        }
    }) {
        Text("Change Timeout Text!!")
    }
}

@Composable
fun ShowSnackBarButton(scaffoldState: ScaffoldState = rememberScaffoldState()) {
    // Creates a CoroutineScope bound to the ShowSnackBarButton's lifecycle
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch {
                scaffoldState.snackbarHostState
                    .showSnackbar("Something happened!")
            }
        }) {
        Text("Show SnackBar")
    }
}

@Composable
fun ShowLazySnackBar(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    timeoutText: String
) {
    Log.d("composeTest", "ShowLazySnackBarButton()")
    // onTimeout은 LaunchedEffect내부에서 접근하나, 재시작 trigger 요건이 아님.
    val currentTimeoutText by rememberUpdatedState(timeoutText)

    // ShowLazySnackBarButton와 lifecycle이 동일
    // key가 true 이므로 recompose시에 재시작되지 않음. onTimeout이 바뀌어도 LaunchedEffect는 재시작 되지 않음.
    LaunchedEffect(true) {
        Log.i("composeTest", "ShowLazySnackBarButton() - timeout started!")
        try {
            delay(3000L)
            scaffoldState.snackbarHostState.showSnackbar(currentTimeoutText)
        } catch (ce: CancellationException) {
            Log.d("composeTest", "ShowLazySnackBarButton() - canceled!")
        }
    }
}

@Composable
fun HelloScreen(mainViewModel: MainViewModel = viewModel()) {
    // by default, viewModel() follows the Lifecycle as the Activity or Fragment
    // that calls HelloScreen(). This lifecycle can be modified by callers of HelloScreen.

    // name is the current value of [helloViewModel.name]
    // with an initial value of ""
    val name: String by mainViewModel.name.observeAsState("")
    HelloContent(name = name, onNameChange = { mainViewModel.onNameChange(it) })
}

@Composable
fun HelloContent(name: String, onNameChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Hello, $name",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.h5
        )
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") }
        )
    }
}