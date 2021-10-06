package com.example.composetest

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun MakeButton() {
    Button(onClick = {}) {
        Row {
            Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "")
            Text(
                "BUTTON", modifier = Modifier
                    .padding(start = 4.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun TestScreenContents(texts: List<String> = listOf("Welcome", "compose world")) {
    val count = remember { mutableStateOf(0) }

    Column {
        texts.forEach {
            Greeting(it)
            Divider(color = Color.DarkGray)
        }
        CounterButton1(count.value) { newCnt -> count.value = newCnt }
    }
}

@Composable
fun CounterButton1(count: Int, updateCount: (Int) -> Unit) {
    Button(
        onClick = { updateCount(count + 1) },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (count > 5) Color.Green else Color.White
        )
    ) {
        Text("Clicked Count#1: $count")
    }
}