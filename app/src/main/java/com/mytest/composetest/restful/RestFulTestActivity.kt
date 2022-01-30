package com.mytest.composetest.restful

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.mytest.composetest.ui.theme.ComposeTestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
class RestFulTestActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "RestFulTestActivity"
    }

    private val viewModel: RestFulTestViewModel by viewModels()

    @ExperimentalCoilApi
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestTheme {
                val data by viewModel.pictureData.collectAsState()
                RestFulTestScreen(data)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
        viewModel.requestPicture()
    }
}

@ExperimentalCoilApi
@Composable
fun RestFulTestScreen(data: Picture) {
    Log.d("RestFulTestActivity", "RestFulTestScreen() - $data")
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center).padding(20.dp)) {
            Image(
                painter = rememberImagePainter(data.imageUrl),
                contentDescription = "",
                modifier = Modifier.size(300.dp).border(1.dp, Color.Gray).align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
            )
            Divider(Modifier.padding(10.dp))
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
        RestFulTestScreen(Picture("안드로이드", "인터넷 어딘가...","https://developer.android.com/images/brand/Android_Robot.png"))
    }
}