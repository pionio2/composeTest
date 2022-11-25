package com.mytest.composetest.friend.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.math.floor

const val INVALID_VALUE = -1

sealed interface IndexLabel
data class TextLabel(val label: String) : IndexLabel
data class ImageLabel(val drawableResId: Int) : IndexLabel
data class IconLabel(val imageVector: ImageVector) : IndexLabel

object IndexedScroll {
    enum class ScrollIndexType(val labelList: List<IndexLabel>) {
        KOREAN_ENGLISH(
            listOf(
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
                TextLabel("A"),
                TextLabel("F"),
                TextLabel("K"),
                TextLabel("P"),
                TextLabel("U"),
                TextLabel("Z"),
                TextLabel("#")
            )
        ),
        ENGLISH_KOREAN(
            listOf(
                TextLabel("A"),
                TextLabel("B"),
                TextLabel("C"),
                TextLabel("D"),
                TextLabel("E"),
                TextLabel("F"),
                TextLabel("G"),
                TextLabel("H"),
                TextLabel("I"),
                TextLabel("K"),
                TextLabel("L"),
                TextLabel("M"),
                TextLabel("O"),
                TextLabel("R"),
                TextLabel("S"),
                TextLabel("T"),
                TextLabel("U"),
                TextLabel("Z"),
                TextLabel("ㄱ"),
                TextLabel("ㅂ"),
                TextLabel("ㅇ"),
                TextLabel("ㅊ"),
                TextLabel("#")
            )
        ),
        FAVORITE(listOf(IconLabel(Icons.Filled.Star))),
        SEARCH(listOf(IconLabel(Icons.Filled.Search))),
        ADOT(listOf(IconLabel(Icons.Filled.School)))
    }

    fun getIndexLabel(indexTypes: List<ScrollIndexType>): List<IndexLabel> {
        val labelList = mutableListOf<IndexLabel>()
        indexTypes.forEach {
            labelList.addAll(it.labelList)
        }
        return labelList
    }
}

/**
 * 화면 오른쪽에 스크롤바를 표시하고, 선택시 중앙에 선택된 문자를 보여준다.
 * @param scrollState 스크롤 사용/미사용시 자동 visibility 제어를 위해 넘겨 받는다. null입력시 항상 보여진다.
 * @param onHovered: 손으로 dragging 할때 해당 글자의 list index를 담아 호출된다.
 */
@Composable
fun IndexedScroll(
    modifier: Modifier = Modifier,
    labelList: List<IndexLabel>,
    isScrollState: Boolean = false,
    scrollbarWidth: Dp = 20.dp,
    scrollbarBgColor: Color = Black40,
    onHovered: (Int) -> Unit
) {
    // 중앙에 보여질 Character or Image 값
    var centerBox by remember { mutableStateOf<IndexLabel?>(null) }

    // 스크롤에 따른 index bar visibility 처리
    var visibility by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = visibility,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 스크롤바 표시
            IndexedScrollBar(
                modifier = Modifier.align(Alignment.TopEnd),
                scrollbarWidth = scrollbarWidth,
                bgColor = scrollbarBgColor,
                labelList = labelList
            ) { index, isDragging ->
                if (isDragging) {
                    centerBox = labelList[index]
                    onHovered(index)
                } else {
                    centerBox = null
                }
            }

            // 스크롤시 현재 선택된 Character 가운데 표시
            centerBox?.let { indexLabel ->
                CenterIconBox(modifier = Modifier.align(Alignment.Center)) {
                    when (indexLabel) {
                        is TextLabel -> Text(text = indexLabel.label, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 25.sp)
                        is ImageLabel -> Icon(
                            modifier = Modifier
                                .size(30.dp, 30.dp)
                                .align(Alignment.Center),
                            painter = painterResource(id = indexLabel.drawableResId),
                            contentDescription = ""
                        )
                        is IconLabel -> Icon(
                            modifier = Modifier
                                .size(30.dp, 30.dp)
                                .align(Alignment.Center),
                            imageVector = indexLabel.imageVector,
                            contentDescription = ""
                        )
                    }
                }
            }
        }
    }

    // Visibility 처리, indexbar를 스크롤중이거나, 리스트를 스크롤중에는 표시한다.
    // 아무런 action이 없는 경우 2초 이후 사라진다.
    // scrollState를 넣지 않은 경우 무조건 true로 처리하여 보여준다.
    LaunchedEffect(centerBox, isScrollState) {
        if (centerBox != null || isScrollState) {
            visibility = true
        } else {
            delay(2000)
            visibility = false
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
                        is ImageLabel -> ImageLabelBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeightDp),
                            drawableResId = indexLabel.drawableResId,
                            myIndex = index,
                            currentDraggedItemIndex = draggedItemIndex
                        )
                        is IconLabel -> ImageLabelBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeightDp),
                            imageVector = indexLabel.imageVector,
                            myIndex = index,
                            currentDraggedItemIndex = draggedItemIndex
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
    val distanceOfSelectedIndex by getAlphaAndFontWeightByDistance(myIndex, currentDraggedItemIndex)

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

// 스크롤에서 사용하는 이미지 표기
@Composable
fun ImageLabelBox(
    modifier: Modifier = Modifier,
    drawableResId: Int = INVALID_VALUE,
    imageVector: ImageVector? = null,
    myIndex: Int = INVALID_VALUE,
    currentDraggedItemIndex: Int = INVALID_VALUE
) {
    if (drawableResId == INVALID_VALUE && imageVector == null) {
        return
    }

    val distanceOfSelectedIndex by getAlphaAndFontWeightByDistance(myIndex, currentDraggedItemIndex)

    if (drawableResId != INVALID_VALUE) {
        Icon(
            modifier = modifier
                .alpha(distanceOfSelectedIndex.first),
            painter = painterResource(id = drawableResId),
            contentDescription = ""
        )
    } else if (imageVector != null) {
        Icon(
            modifier = modifier
                .alpha(distanceOfSelectedIndex.first),
            imageVector = imageVector,
            contentDescription = ""
        )
    }
}

// 현재 내 위치(index)와 dragging되고 있는 item index간에 차이에 따른 alpha값과 weight를 반환한다.
@Composable
fun getAlphaAndFontWeightByDistance(myIndex: Int, currentDraggedItemIndex: Int): State<Pair<Float, FontWeight>> {
    return produceState(initialValue = Pair(1f, FontWeight.Normal), myIndex, currentDraggedItemIndex) {
        value = if (currentDraggedItemIndex != INVALID_VALUE) {
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

// 가운데 표시되는 Character 표시
@Composable
fun CenterIconBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.size(50.dp),
        color = Black40,
        shape = CircleShape
    ) {
        Box(modifier = modifier.padding(3.dp), contentAlignment = Alignment.Center) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Column {
        IndexedScroll(
            labelList = IndexedScroll.getIndexLabel(
                listOf(
                    IndexedScroll.ScrollIndexType.SEARCH,
                    IndexedScroll.ScrollIndexType.FAVORITE,
                    IndexedScroll.ScrollIndexType.KOREAN_ENGLISH
                )
            ),
        ) {

        }
        Icon(imageVector = Icons.Filled.Search, contentDescription = "")
    }
}