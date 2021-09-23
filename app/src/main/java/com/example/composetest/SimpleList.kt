package com.example.composetest

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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SimpleList() {
    Box {
        val listSize = 100
        // 스크롤의 position의 상태를 저장.
        val scrollState = rememberLazyListState()

        Column(Modifier.fillMaxWidth()) {
            LazyColumn(
                state = scrollState,
                contentPadding = PaddingValues(bottom = 50.dp)
            ) {
                items(listSize) {
                    ImageListItem(it)
                }
            }
        }
        val showButton = remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex > 0
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomEnd)) {
            AnimatedVisibility(visible = showButton.value) {
                ScrollToTopButton(scrollState)
            }
        }
    }
}

@Composable
fun ScrollToTopButton(scrollState: LazyListState, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    Image(
        painter = painterResource(id = R.drawable.btn_top),
        contentDescription = "",
        modifier
            .padding(20.dp)
            .background(color = Color.Gray, shape = CircleShape)
            .clickable {
                scope.launch {  scrollState.scrollToItem(0) }
            }
    )
}


@ExperimentalCoilApi
@Composable
fun ImageListItem(index: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = rememberImagePainter(
                data = "https://developer.android.com/images/brand/Android_Robot.png"
            ),
            contentDescription = "",
            modifier = Modifier.size(50.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text("Item $index", style = MaterialTheme.typography.subtitle2)
    }
}