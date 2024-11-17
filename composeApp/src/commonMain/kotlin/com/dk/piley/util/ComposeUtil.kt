package com.dk.piley.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.common.LocalDim
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.cancel
import piley.composeapp.generated.resources.ok

/**
 * Alert dialog helper to display and handle alert dialogs
 *
 * @param title dialog title
 * @param description dialog description
 * @param confirmText dialog confirm button text
 * @param dismissText dialog dismiss button text
 * @param onDismiss on dialog dismiss
 * @param onConfirm on click dialog confirm button
 */
@Composable
fun AlertDialogHelper(
    title: String,
    description: String,
    confirmText: String = stringResource(Res.string.ok),
    dismissText: String? = stringResource(Res.string.cancel),
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = description) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmText) } },
        dismissButton = {
            if (dismissText != null) {
                TextButton(onClick = onDismiss) { Text(dismissText) }
            }
        },
        onDismissRequest = onDismiss
    )
}

/**
 * Indefinite progress bar
 *
 * @param modifier generic modifier
 * @param visible whether the progress bar is visible
 */
@Composable
fun IndefiniteProgressBar(modifier: Modifier = Modifier, visible: Boolean = false) {
    AnimatedVisibility(visible) {
        LinearProgressIndicator(modifier = modifier.fillMaxWidth())
    }
}

enum class SlideDirection { UP, DOWN, LEFT, RIGHT }

/**
 * Initial slide in animation
 *
 * @param direction direction into which the slide in happens
 * @param pathLengthInDp slide in path length in [Dp] units
 * @param initialAlpha initial alpha value of animated content
 * @param initialTransitionStateValue initial animation transition state value
 * @param content content to perform the animation for
 */
@Composable
fun InitialSlideIn(
    modifier: Modifier = Modifier,
    direction: SlideDirection,
    pathLengthInDp: Int,
    initialAlpha: Float = 0f,
    initialTransitionStateValue: Boolean = false,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    // create horizontal or vertical slide transition depending on direction and path length
    val slideTransition = when (direction) {
        SlideDirection.UP -> slideInVertically { with(density) { pathLengthInDp.dp.roundToPx() } }
        SlideDirection.DOWN -> slideInVertically { with(density) { -pathLengthInDp.dp.roundToPx() } }
        SlideDirection.LEFT -> slideInHorizontally { with(density) { pathLengthInDp.dp.roundToPx() } }
        SlideDirection.RIGHT -> slideInHorizontally { with(density) { -pathLengthInDp.dp.roundToPx() } }
    }
    AnimatedVisibility(
        modifier = modifier,
        visibleState = remember {
            MutableTransitionState(initialTransitionStateValue).apply {
                targetState = true
            }
        },
        enter = slideTransition + fadeIn(initialAlpha = initialAlpha)
    ) {
        content()
    }
}

/**
 * Generate list of preview transition states
 *
 * @param T list of generic entities to get the preview transition states for
 * @param initial initial value to set for each transition state
 * @return a list of transition states of type [T] for with the specified initial value
 */
fun <T> List<T>.getPreviewTransitionStates(initial: Boolean = true) =
    List(this.size) { MutableTransitionState(initial) }

/**
 * Rounded outline modifier
 *
 */
fun Modifier.roundedOutline() = composed {
    clip(MaterialTheme.shapes.large)
        .border(
            border = BorderStroke(LocalDim.current.mini, MaterialTheme.colorScheme.outline),
            shape = MaterialTheme.shapes.large
        )
}

/**
 * Default padding modifier
 *
 */
@Composable
fun Modifier.defaultPadding() = this then padding(LocalDim.current.large)

/**
 * Big spacer with large dimen size
 *
 */
@Composable
fun BigSpacer() {
    Spacer(modifier = Modifier.size(LocalDim.current.large))
}

/**
 * Medium spacer with medium dimen size
 *
 */
@Composable
fun MediumSpacer() {
    Spacer(modifier = Modifier.size(LocalDim.current.medium))
}

/**
 * Tiny spacer with small dimen size
 *
 */
@Composable
fun TinySpacer() {
    Spacer(modifier = Modifier.size(LocalDim.current.small))
}


class BooleanProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(false, true)
}

@Composable
expect fun getScreenHeight(): Dp

expect val defaultNavBarPadding: Dp
