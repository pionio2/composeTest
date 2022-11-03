package com.mytest.composetest.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimationMain() {
    Column(modifier = Modifier.fillMaxSize()) {
        var isVisible by remember {
            mutableStateOf(false)
        }
        var isRound by remember {
            mutableStateOf(false)
        }
        Button(onClick = {
            isVisible = !isVisible
            isRound = !isRound
        }) {
            Text(text = "Toggle")
        }





//        AnimationVisibilityTest(isVisible)
//        CircleToRectBySpring(isRound)
//        CircleToRectByTween(isRound)
//        TransitionAnimation(isRound)
//        InfiniteAnimation()
//        AnimationContentTest(isVisible)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimationContentTest(isVisible: Boolean) {
    AnimatedContent(
        targetState = isVisible,
        modifier = Modifier
            .fillMaxWidth(),
        content = { isVisible ->
            if (isVisible) {
                Box(modifier = Modifier.background(Color.Green))
            } else {
                Box(modifier = Modifier.background(Color.Red))
            }
        },
        transitionSpec = {
//                fadeIn() with fadeOut()
            slideInHorizontally(
                initialOffsetX = {
                    if (isVisible) it else -it
                }
            ) with slideOutHorizontally(
                targetOffsetX = {
                    if (isVisible) -it else it
                }
            )
        }

    )
}

@Composable
fun InfiniteAnimation() {
    val transition = rememberInfiniteTransition()
    val color by transition.animateColor(
        initialValue = Color.Red,
        targetValue = Color.Green,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(200.dp)
            .background(color)
    )
}

@Composable
fun TransitionAnimation(isRound: Boolean) {

    val transition = updateTransition(
        targetState = isRound,
        label = null
    )

    val borderRadius by transition.animateInt(
        transitionSpec = { tween(2000) },
        label = "borderRadius",
        targetValueByState = { isRound ->
            if (isRound) 100 else 0
        }
    )

    val color by transition.animateColor(
        transitionSpec = { tween(1000) },
        label = "color",
        targetValueByState = { isRound ->
            if (isRound) Color.Green else Color.Red
        }
    )

    Box(
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(borderRadius))
            .background(color)
    )
}

@Composable
fun CircleToRectByTween(isRound: Boolean) {
    val borderRadius by animateIntAsState(
        targetValue = if (isRound) 100 else 0,
        animationSpec = tween( //일반적인 처리
            durationMillis = 300,
            delayMillis = 500,
            easing = LinearEasing
        )
    )
    Box(
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(borderRadius))
            .background(Color.Red)

    )
}

@Composable
fun CircleToRectBySpring(isRound: Boolean) {
    val borderRadius by animateIntAsState(
        targetValue = if (isRound) 20 else 0,
        animationSpec = spring( //통통 튀는듯한 동작 - 근데 자꾸 죽네;;;
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    Box(
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(borderRadius))
            .background(Color.Red)

    )
}

@Composable
fun AnimationVisibilityTest(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally() + fadeIn(),
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.background(Color.Red))
    }
}