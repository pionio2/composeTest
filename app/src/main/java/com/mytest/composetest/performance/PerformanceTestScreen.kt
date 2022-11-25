package com.mytest.composetest.performance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mytest.composetest.ui.theme.ComposeTestTheme
import kotlinx.coroutines.delay

//DrawBehind 예제
@Composable
fun PerformanceTestScreen(modifier:Modifier = Modifier) {

    var bgColor1 by remember {
        mutableStateOf(Color.Black)
    }

    var bgColor2 by remember {
        mutableStateOf(Color.Red)
    }

    Box() {
        Column(modifier = modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(100.dp)
                    .drawBehind {
                        drawRect(bgColor1)
                    }
//                        .background(bgColor1)
                )
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(100.dp)
                    .drawBehind {
                        drawRect(bgColor2)
                    }
//                        .background(bgColor2)
                )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { bgColor1 = if (bgColor1 == Color.Black) Color.LightGray else Color.Black },
                    modifier = Modifier
                        .padding(10.dp)
                        .width(100.dp)
                ) {
                    Text("Box1 Color")
                }

                Button(
                    onClick = { bgColor2 = if (bgColor2 == Color.Red) Color.Blue else Color.Red },
                    modifier = Modifier
                        .padding(10.dp)
                        .width(100.dp)
                ) {
                    Text("Box2 Color")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTestTheme {
        PerformanceTestScreen()
    }
}
