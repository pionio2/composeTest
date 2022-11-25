package com.mytest.composetest.performance

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mytest.composetest.ui.theme.ComposeTestTheme

// Swith와 Button을 갖는 화면 전체
@Composable
fun PerformanceTestScreen3(
    modifier: Modifier = Modifier,
    state: ScreenState = ScreenState(),
    onCheckChanged: (Boolean) -> Unit,
    onBtnClicked: () -> Unit
) {
    Column(modifier.fillMaxWidth().padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Switch(checked = state.isChecked, onCheckedChange = { onCheckChanged(it) })
        Spacer(modifier = Modifier.padding(10.dp))
        Counter(state.count, onClicked = onBtnClicked)
    }
}

// 버튼 + 카운트 텍스트
@Composable
fun Counter(count: Int, onClicked: () -> Unit) {
    Row {
        Button(onClick = onClicked) {
            Text("Increase Count")
        }
        Spacer(modifier = Modifier.padding(20.dp))
        Text(text = count.toString(), modifier = Modifier.align(Alignment.CenterVertically))
    }
}

data class ScreenState(val isChecked: Boolean = false, val count: Int = 0)

@Preview(showBackground = true)
@Composable
fun Performance3DefaultPreview() {
    ComposeTestTheme {
        PerformanceTestScreen3(onCheckChanged = {}) {}
    }
}