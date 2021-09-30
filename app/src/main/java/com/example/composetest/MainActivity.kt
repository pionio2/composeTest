package com.example.composetest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.composetest.ui.theme.ComposeTestTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
//                    SideEffectScreen()
                    CustomColumnSample()
                }
            }
        }
    }
}

fun Modifier.firstBaselineToTop(firstBaselineToTop: Dp) = layout { measurable, constraints ->
    // Composable 측정
    val placeable = measurable.measure(constraints)

    // 측정값(pixel 3 기준 -  width:163px | height:59px

    // Check the composable has a first baseline
    check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
    val firstBaseline = placeable[FirstBaseline]

    // 측정값 - firstBaseline: 47px

    // Height of the composable with padding - first baseline
    val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
    val height = placeable.height + placeableY

    // 측정값 - firstBaselineToTop.roundToPx(): 88px, height:100

    Log.e(
        "composeTest", "pw:${placeable.width} | ph:${placeable.height} | firstBaseline:$firstBaseline |  firstBaselineToTop.roundToPx():${firstBaselineToTop.roundToPx()} | placeableY:$placeableY " +
                "| height:$height"
    )

    layout(placeable.width, height) {
        // Where the composable gets placed
        placeable.placeRelative(0, placeableY)
    }
}

@Composable
fun MyOwnColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map { measurable ->
            // Measure each child
            measurable.measure(constraints)
        }

        // Track the y co-ord we have placed children up to
        var yPosition = 0


        // Set the size of the layout as big as it can
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Place children in the parent layout
            placeables.forEach { placeable ->
                // Position item on the screen
                placeable.placeRelative(x = 0, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun LayoutModifierSample() {
    Column {
        Text("Hi there!", Modifier.firstBaselineToTop(32.dp))
        Text("Hi there!", Modifier.padding(top = 32.dp))
    }
}

@Composable
fun CustomColumnSample() {
    MyOwnColumn {
        Text("가나다")
        Text("라마바")
        Text("사아자")
        Text("차카타")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTestTheme {
        CustomColumnSample()
    }
}