package com.example.composetest

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.composetest.ui.theme.ComposeTestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MyScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTestTheme {
        MyScreen()
    }
}

@Composable
fun MyScreen(scaffoldState: ScaffoldState = rememberScaffoldState(),
             mainViewModel: MainViewModel = viewModel()) {
    Log.e("composeTest", "MyScreen called")

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
                Log.e("composeTest", "show snackbar")
                scaffoldState.snackbarHostState.showSnackbar(
                    message = "Error message",
                    actionLabel = "Retry message"
                )
            } catch (ce: CancellationException) {
                Log.e("composeTest", "canceled!!")
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState) {
        HelloScreen()
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