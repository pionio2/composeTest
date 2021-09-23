package com.example.composetest

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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