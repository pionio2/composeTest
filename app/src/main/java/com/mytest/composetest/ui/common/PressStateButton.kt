package com.mytest.composetest.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mytest.composetest.util.LogError

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PressStateButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: PressStateButtonColors = ButtonDefaults.pressStateButtonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    val backgroundColor by if (isPressed) colors.pressedBackgroundColor(enabled = enabled) else colors.backgroundColor(enabled = enabled)
    val contentColor by if (isPressed) colors.pressedContentColor(enabled = enabled) else colors.contentColor(enabled = enabled)

    val interactionState by interactionSource.interactions.collectAsState(initial = null)
    LaunchedEffect(key1 = interactionState) {
        snapshotFlow { interactionState }
            .collect {
                LogError("doohyun") { "doohyun state change $it" }

            }
    }

    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        rememberRipple()
        Surface(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            color = backgroundColor,
            contentColor = contentColor.copy(alpha = 1f),
            border = border,
            elevation = elevation?.elevation(enabled, interactionSource)?.value ?: 0.dp,
            interactionSource = interactionSource
        ) {
            CompositionLocalProvider(LocalContentAlpha provides contentColor.alpha) {
                ProvideTextStyle(
                    value = MaterialTheme.typography.button
                ) {
                    Row(
                        Modifier
                            .defaultMinSize(
                                minWidth = ButtonDefaults.MinWidth,
                                minHeight = ButtonDefaults.MinHeight
                            )
                            .padding(contentPadding),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        content = content
                    )
                }
            }
        }
    }
}

@Stable
interface PressStateButtonColors : ButtonColors {
    @Composable
    fun pressedBackgroundColor(enabled: Boolean): State<Color>

    @Composable
    fun pressedContentColor(enabled: Boolean): State<Color>
}

@Composable
fun ButtonDefaults.pressStateButtonColors(
    backgroundColor: Color = MaterialTheme.colors.primary,
    contentColor: Color = contentColorFor(backgroundColor),
    disabledBackgroundColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
        .compositeOver(MaterialTheme.colors.surface),
    disabledContentColor: Color = MaterialTheme.colors.onSurface
        .copy(alpha = ContentAlpha.disabled),
    backgroundPressColor: Color = backgroundColor,
    contentPressColor: Color = contentColor

): PressStateButtonColors = DefaultPressStateButtonColors(
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    disabledBackgroundColor = disabledBackgroundColor,
    disabledContentColor = disabledContentColor,
    backgroundPressColor = backgroundPressColor,
    contentPressColor = contentPressColor
)

@Immutable
private class DefaultPressStateButtonColors(
    private val backgroundColor: Color,
    private val contentColor: Color,
    private val disabledBackgroundColor: Color,
    private val disabledContentColor: Color,
    private val backgroundPressColor: Color,
    private val contentPressColor: Color
) : PressStateButtonColors {
    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) backgroundColor else disabledBackgroundColor)
    }

    @Composable
    override fun contentColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) contentColor else disabledContentColor)
    }

    @Composable
    override fun pressedBackgroundColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) backgroundPressColor else disabledBackgroundColor)
    }

    @Composable
    override fun pressedContentColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) contentPressColor else disabledContentColor)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DefaultPressStateButtonColors

        if (backgroundColor != other.backgroundColor) return false
        if (contentColor != other.contentColor) return false
        if (disabledBackgroundColor != other.disabledBackgroundColor) return false
        if (disabledContentColor != other.disabledContentColor) return false
        if (backgroundPressColor != other.backgroundPressColor) return false
        if (contentPressColor != other.contentPressColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = backgroundColor.hashCode()
        result = 31 * result + contentColor.hashCode()
        result = 31 * result + disabledBackgroundColor.hashCode()
        result = 31 * result + disabledContentColor.hashCode()
        result = 31 * result + backgroundPressColor.hashCode()
        result = 31 * result + contentPressColor.hashCode()
        return result
    }
}