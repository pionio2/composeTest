package com.example.composetest.ui.theme

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.atLeast

@Composable
fun ConstraintLayoutContent() {
    ConstraintLayout {
        // Creates references for the three composables
        // in the ConstraintLayout's body
        val (button1, button2, text) = createRefs()

        Button(
            onClick = { /* Do something */ },
            modifier = Modifier.constrainAs(button1) {
                top.linkTo(parent.top, margin = 16.dp)
            }
        ) {
            Text("Button 1")
        }

        Text("Text", Modifier.constrainAs(text) {
            top.linkTo(button1.bottom, margin = 16.dp)
            centerAround(button1.end)
        })

        val barrier = createEndBarrier(button1, text)
        Button(
            onClick = { /* Do something */ },
            modifier = Modifier.constrainAs(button2) {
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(barrier)
            }
        ) {
            Text("Button 2")
        }
    }
}

@Composable
fun LargeConstraintLayout1() {
    ConstraintLayout {
        val text = createRef()

        val guideline = createGuidelineFromStart(fraction = 0.5f)
        Text(
            "This is a very very very very very very very long text",
            Modifier.constrainAs(text) {
                linkTo(start = guideline, end = parent.end)
                width = Dimension.preferredWrapContent
            }
        )
    }
}

@Composable
fun LargeConstraintLayout() {
    ConstraintLayout {
        val (text0, text1, text2, text3, text4, text5, text6) = createRefs()

        val guideline = createGuidelineFromStart(fraction = 0.3f)
        val text = "This is a very very very very very very very very very very very long text"
//        val text = "text"

        Text(
            "value - $text",
            Modifier.constrainAs(text0) {
                linkTo(start = guideline, end = parent.end)
            }
        )

        Text(
            "preferredWrapContent - $text",
            Modifier.constrainAs(text1) {
                linkTo(start = guideline, end = parent.end)
                top.linkTo(text0.bottom, margin = 6.dp)
                width = Dimension.preferredWrapContent
            }
        )
        Text(
            "wrapContent - $text",
            Modifier.constrainAs(text2) {
                linkTo(start = guideline, end = parent.end)
                top.linkTo(text1.bottom, margin = 6.dp)
                width = Dimension.wrapContent
            }
        )

        Text(
            "fillToConstraints - $text",
            Modifier.constrainAs(text3) {
                linkTo(start = guideline, end = parent.end)
                top.linkTo(text2.bottom, margin = 6.dp)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            "preferredValue - $text",
            Modifier.constrainAs(text4) {
                linkTo(start = guideline, end = parent.end)
                top.linkTo(text3.bottom, margin = 6.dp)
                width = Dimension.preferredValue(100.dp)
            }
        )

        Text(
            "value - $text",
            Modifier.constrainAs(text5) {
                linkTo(start = guideline, end = parent.end)
                top.linkTo(text4.bottom, margin = 6.dp)
                width = Dimension.value(100.dp)
            }
        )

        Text(
            "preferredWrapContent.atLeast - $text",
            Modifier.constrainAs(text6) {
                linkTo(start = guideline, end = parent.end)
                top.linkTo(text5.bottom, margin = 6.dp)
                width = Dimension.preferredWrapContent.atLeast(50.dp)
            }
        )
    }
}

@Composable
fun DecoupledConstraintLayout() {
    BoxWithConstraints {
        val constraints = if (maxWidth < maxHeight) {
            decoupledConstraints(margin = 16.dp) // Portrait constraints
        } else {
            decoupledConstraints(margin = 32.dp) // Landscape constraints
        }

        ConstraintLayout(constraints) {
            Button(
                onClick = { /* Do something */ },
                modifier = Modifier.layoutId("button")
            ) {
                Text("Button")
            }

            Text("Text", Modifier.layoutId("text"))
        }
    }
}

private fun decoupledConstraints(margin: Dp): ConstraintSet {
    return ConstraintSet {
        val button = createRefFor("button")
        val text = createRefFor("text")

        constrain(button) {
            top.linkTo(parent.top, margin= margin)
        }
        constrain(text) {
            top.linkTo(button.bottom, margin)
        }
    }
}