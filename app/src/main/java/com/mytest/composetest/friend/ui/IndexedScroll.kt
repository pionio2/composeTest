package com.mytest.composetest.friend.ui

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mytest.composetest.ui.theme.Black40
import com.mytest.composetest.ui.theme.White40
import com.mytest.composetest.util.LogDebug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.lang.Math.abs
import kotlin.math.floor

const val INVALID_VALUE = -1
const val MIDDLE_DOT = "\u2022"

/**
 * 화면 오른쪽에 스크롤바를 표시하고, 선택시 중앙에 선택된 문자를 보여준다.
 */
@Composable
fun IndexedScroll(
    modifier: Modifier = Modifier,
    labelList: List<IndexLabel>,
    scrollbarWidth: Dp = 20.dp,
    bgColor: Color = Black40,
    onHovered: (Int) -> Unit
) {
    // 중앙에 보여질 Character 값
    var centerCharacter by remember { mutableStateOf("") }
    // 중앙에 보여질 Image 값
    var centerImage by remember { mutableStateOf(INVALID_VALUE) }

    Box(modifier = modifier.fillMaxSize()) {
        IndexedScrollBar(
            modifier = Modifier.align(Alignment.TopEnd),
            scrollbarWidth = scrollbarWidth,
            bgColor = bgColor,
            labelList = labelList
        ) { index, isDragging ->
            if (isDragging) {
                when (val indexLabel = labelList[index]) {
                    is TextLabel -> centerCharacter = indexLabel.label
                    is ImageLabel -> centerImage = indexLabel.drawableResId
                }
                onHovered(index)
            } else {
                centerCharacter = ""
                centerImage = INVALID_VALUE
            }
        }

        // 스크롤시 현재 선택된 Character 가운데 표시
        if (centerCharacter.isBlank().not()) {
            CenterCharacterBox(modifier = Modifier.align(Alignment.Center), centerCharacter)
        }

        if (centerImage != INVALID_VALUE) {
            Icon(
                modifier = Modifier
                    .size(30.dp, 30.dp)
                    .align(Alignment.Center),
                painter = painterResource(id = centerImage),
                contentDescription = ""
            )
        }
    }
}

/**
 * 스크롤 bar를 그린다.
 * @param onHovered: 손으로 dragging 할때 해당 글자의 list index를 담아 호출된다.
 */
@Composable
fun IndexedScrollBar(
    modifier: Modifier = Modifier,
    labelList: List<IndexLabel>,
    scrollbarWidth: Dp = 20.dp,
    bgColor: Color = Black40,
    onHovered: (Int, Boolean) -> Unit
) {
    if (labelList.isEmpty()) {
        return
    }
    BoxWithConstraints(modifier = modifier.fillMaxHeight()) {
        // 각 index 아이템이 가질수 있는 height 계산
        val itemHeightDp by remember(labelList) {
            derivedStateOf { maxHeight.div(labelList.size) }
        }

        // 각 index 아이템 개수에 따른 text size 계산
        val itemTextSizeDp by remember(scrollbarWidth) {
            derivedStateOf { scrollbarWidth.times(0.8f) }
        }

        // 현재 dragged 되는 y 값 (surface top을 0으로 기준하여 나오는 y 값)
        var draggedPositionY by remember { mutableStateOf(INVALID_VALUE.toFloat()) }
        // 현재 dragged 되는 item index값 (drag 아래 있는 index값)
        var draggedItemIndex by remember { mutableStateOf(INVALID_VALUE) }

        val currentOnHovered by rememberUpdatedState(onHovered)

        val density = LocalDensity.current

        Surface(
            modifier = Modifier
                .height(maxHeight)
                .width(scrollbarWidth)
                .pointerInput(Unit) {
                    // position.y는 이 surface의 top을 0으로 하여 + - 값으로 표현된다.
                    detectVerticalDragGestures(
                        onDragEnd = {
                            onHovered(INVALID_VALUE, false)
                            draggedPositionY = INVALID_VALUE.toFloat()
                            draggedItemIndex = INVALID_VALUE
                        },
                        onDragCancel = {
                            onHovered(INVALID_VALUE, false)
                            draggedPositionY = INVALID_VALUE.toFloat()
                            draggedItemIndex = INVALID_VALUE
                        }) { change, _ ->
                        draggedPositionY = change.position.y
                    }
                },
            color = bgColor, shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 2.dp, bottom = 2.dp)
                    .fillMaxSize()
            ) {
                labelList.forEachIndexed { index, indexLabel ->
                    when (indexLabel) {
                        is TextLabel -> TextLabelBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeightDp),
                            label = indexLabel.label,
                            myIndex = index,
                            currentDraggedItemIndex = draggedItemIndex,
                            itemTextSizeDp = itemTextSizeDp,
                        )
                        is ImageLabel -> Icon(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeightDp),
                            painter = painterResource(id = indexLabel.drawableResId),
                            contentDescription = ""
                        )
                    }
                }
            }
        }

        // dragging시에 y값을 받아서 몇번째 index가 dragging되고 있는지 계산한다.
        LaunchedEffect(itemHeightDp, itemTextSizeDp) {
            val itemHeightPx = density.run { itemHeightDp.toPx() }
            val maxHeightPx = density.run { maxHeight.toPx() }
            snapshotFlow { draggedPositionY }
                .filter { draggedPositionY > 0 && itemHeightPx > 0 && draggedPositionY <= maxHeightPx }
                .map { floor(draggedPositionY / itemHeightPx).toInt() }
                .flowOn(Dispatchers.Default)
                .distinctUntilChanged()
                .collect {
                    draggedItemIndex = it
                    currentOnHovered(it, true)
                }
        }
    }
}

// 스크롤에서 사용하는 글자 표기
@Composable
fun TextLabelBox(
    modifier: Modifier = Modifier,
    label: String,
    myIndex: Int = INVALID_VALUE,
    currentDraggedItemIndex: Int = INVALID_VALUE,
    itemTextSizeDp: Dp
) {
    val distanceOfSelectedIndex by remember(myIndex, currentDraggedItemIndex) {
        derivedStateOf {
            // 선택된 index와의 거리에 따라 alpah값과 font weight를 처리한다.
            if (currentDraggedItemIndex != INVALID_VALUE) {
                when (kotlin.math.abs(currentDraggedItemIndex - myIndex)) {
                    0 -> Pair(1f, FontWeight.Bold)
                    1 -> Pair(0.3f, FontWeight.Normal)
                    2 -> Pair(0.5f, FontWeight.Normal)
                    3 -> Pair(0.7f, FontWeight.Normal)
                    else -> Pair(1f, FontWeight.Normal)
                }
            } else {
                Pair(1f, FontWeight.Normal)
            }
        }
    }

    Box(modifier = modifier) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(distanceOfSelectedIndex.first),
            text = label,
            maxLines = 1,
            fontSize = LocalDensity.current.run { itemTextSizeDp.toSp() },
            textAlign = TextAlign.Center,
            fontWeight = distanceOfSelectedIndex.second
        )
    }
}

// 가운데 표시되는 Character 표시
@Composable
fun CenterCharacterBox(
    modifier: Modifier = Modifier,
    label: String
) {
    Surface(
        modifier = modifier.size(50.dp),
        color = Black40,
        shape = CircleShape
    ) {
        Box(modifier = modifier.padding(3.dp), contentAlignment = Alignment.Center) {
            Text(text = label, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 25.sp)
        }
    }
}

sealed interface IndexLabel
data class TextLabel(val label: String) : IndexLabel
data class ImageLabel(val drawableResId: Int) : IndexLabel


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Column {
        IndexedScrollBar(
            labelList = listOf(
                TextLabel("ㄱ"),
                TextLabel("ㄴ"),
                TextLabel("ㄷ"),
                TextLabel("ㄹ"),
                TextLabel("ㅁ"),
                TextLabel("ㅂ"),
                TextLabel("ㅅ"),
                TextLabel("ㅇ"),
                TextLabel("ㅈ"),
                TextLabel("ㅊ"),
                TextLabel("ㅋ"),
                TextLabel("ㅌ"),
                TextLabel("ㅍ"),
                TextLabel("ㅎ"),
                TextLabel("#"),
                TextLabel("ㄱ"),
                TextLabel("ㄴ"),
                TextLabel("ㄷ"),
                TextLabel("ㄹ"),
                TextLabel("ㅁ"),
                TextLabel("ㅂ"),
                TextLabel("ㅅ"),
                TextLabel("ㅇ"),
                TextLabel("ㅈ"),
                TextLabel("ㅊ"),
                TextLabel("ㅋ"),
                TextLabel("ㅌ"),
                TextLabel("ㅍ"),
                TextLabel("ㅎ"),
                TextLabel("*"),
            )
        ) { _, _ -> }
//        CenterCharacterBox(label = "ㄱ")
    }
}