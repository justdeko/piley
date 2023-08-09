package com.dk.piley.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.dk.piley.R

@Composable
fun AlertDialogHelper(
    title: String,
    description: String,
    confirmText: String = stringResource(R.string.ok),
    dismissText: String? = stringResource(R.string.cancel),
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

@Composable
fun IndefiniteProgressBar(modifier: Modifier = Modifier, visible: Boolean = false) {
    AnimatedVisibility(visible) {
        LinearProgressIndicator(modifier = modifier.fillMaxWidth())
    }
}

enum class SlideDirection { UP, DOWN, LEFT, RIGHT }

@Composable
fun InitialSlideIn(
    direction: SlideDirection,
    pathLengthInDp: Int,
    density: Density,
    initialAlpha: Float = 0f,
    initialTransitionStateValue: Boolean = false,
    content: @Composable () -> Unit
) {
    // create horizontal or vertical slide transition depending on direction and path length
    val slideTransition = when (direction) {
        SlideDirection.UP -> slideInVertically { with(density) { pathLengthInDp.dp.roundToPx() } }
        SlideDirection.DOWN -> slideInVertically { with(density) { -pathLengthInDp.dp.roundToPx() } }
        SlideDirection.LEFT -> slideInHorizontally { with(density) { pathLengthInDp.dp.roundToPx() } }
        SlideDirection.RIGHT -> slideInHorizontally { with(density) { -pathLengthInDp.dp.roundToPx() } }
    }
    AnimatedVisibility(
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

fun <T> List<T>.getPreviewTransitionStates(initial: Boolean = true) =
    List(this.size) { MutableTransitionState(initial) }

fun Modifier.roundedOutline() = composed {
    clip(RoundedCornerShape(16.dp))
        .border(
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(16.dp)
        )
}