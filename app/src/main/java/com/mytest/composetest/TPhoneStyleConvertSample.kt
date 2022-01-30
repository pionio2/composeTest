package com.mytest.composetest

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.format.TextStyle

//<!-- 00 res / btn / btn_ghost_24 (IMPL_NOTE: 실제 zeplin 상의 padding 과 일부 차이가 있지만 좌/우 상/하 여백을 그냥 동일하게 함)-->
//<style name="ResBtnBtnGhost24">
//<item name="android:background">@drawable/btn_ghost_24_bg_selector</item>
//<item name="android:textColor">@color/text_02_color_non_pressed_selector</item>
//<item name="android:paddingStart">16dp</item>
//<item name="android:paddingEnd">16dp</item>
//<item name="android:paddingTop">5dp</item>
//<item name="android:paddingBottom">5dp</item>
//<item name="android:textSize">12sp</item>
//<item name="android:textStyle">bold</item>
//<item name="android:minHeight">24dp</item>
//<item name="android:lines">1</item>
//<item name="android:singleLine">true</item>
//<item name="android:gravity">center_vertical</item>
//</style>

val btnGhost24TextStyle = androidx.compose.ui.text.TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center
)

val line05 = Color(0xffe6e6e6)
val text02 = Color(0xff4d4d4d)
val text06 = Color(0xffcccccc)

val press01 = Color(0xffd6d6d6)
val bg01 = Color(0xffffffff)

@Composable
fun BtnGhost24Text(text: String, enabled: Boolean = true, modifier: Modifier = Modifier, onClicked: () -> Unit = {}) {
    val interactionSource = remember { MutableInteractionSource() }
    val clickable = modifier.clickable(interactionSource = interactionSource, indication = LocalIndication.current) { onClicked() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Surface(
        shape = RoundedCornerShape(13.5.dp),
        contentColor = if (isPressed) Color.Red else Color.White,
        border = BorderStroke(1.dp, line05),
        modifier = Modifier.clip(RoundedCornerShape(13.5.dp))
    ) {
        Text(
            text,
            color = if (enabled) text02 else text06,
            style = btnGhost24TextStyle,
            modifier = Modifier.then(clickable).padding(vertical = 5.dp, horizontal = 16.dp),
            maxLines = 1
        )
    }
}

@Composable
fun BtnGhost24Text2(text: String, enabled: Boolean = true, modifier: Modifier = Modifier, onClicked: () -> Unit = {}) {
    val interactionSource = remember { MutableInteractionSource() }
    val clickable = modifier.clickable(interactionSource = interactionSource, indication = LocalIndication.current) { onClicked() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Text(
        text,
        color = if (enabled) text02 else text06,
        style = btnGhost24TextStyle,
        modifier = Modifier
            .border(BorderStroke(1.dp, line05), RoundedCornerShape(13.5.dp))
            .clip(RoundedCornerShape(13.5.dp))
            .then(clickable)
            .background(if (isPressed) Color.Red else bg01)
            .padding(vertical = 5.dp, horizontal = 16.dp)
        ,
        maxLines = 1
    )
}

@Preview
@Composable
fun Preview() {
    Column {
        BtnGhost24Text("Test wow!!") {}
        Spacer(modifier = Modifier.padding(5.dp))
        BtnGhost24Text("Test wow!!", false) {}
        Spacer(modifier = Modifier.padding(5.dp))
        BtnGhost24Text2("Test wow!!")
    }
}


@Composable
fun TextDecorationLineThroughSample() {
    Text(
        text = "Demo Text",
        textDecoration = TextDecoration.LineThrough
    )
}


@Composable
fun TextDecorationUnderlineSample() {
    Text(
        text = "Demo Text",
        textDecoration = TextDecoration.Underline
    )
}


@Composable
fun TextDecorationCombinedSample() {
    Text(
        text = "Demo Text",
        textDecoration = TextDecoration.Underline + TextDecoration.LineThrough
    )
}