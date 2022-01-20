package com.example.composetest.restful

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.composetest.MainScreen
import com.example.composetest.TestViewModel
import com.example.composetest.ui.theme.ComposeTestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
class RestFulTestActivity : AppCompatActivity() {

    private val viewModel: RestFulTestViewModel by viewModels()

    @ExperimentalCoilApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestTheme {
                val data by viewModel.natureData.collectAsState()
                RestFulTestScreen(data)
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
fun RestFulTestScreen(data: Nature) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center).padding(20.dp)) {
            Image(
                painter = rememberImagePainter(data.imageUrl),
                contentDescription = "",
                modifier = Modifier.fillMaxWidth()
            )
            Divider()
            Text("Title: ${data.title}")
            Text("Location: ${data.location}")
        }
    }

}

@ExperimentalCoilApi
@InternalCoroutinesApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTestTheme {
        RestFulTestScreen(Nature("안드로이드", "인터넷 어딘가...","https://developer.android.com/images/brand/Android_Robot.png"))
    }
}