package com.dk.piley.ui.pile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dk.piley.model.task.Task
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.theme.confirm_green
import com.dk.piley.util.MediumSpacer

/**
 * Pile task within a pile
 *
 * @param modifier generic modifier
 * @param dismissState dismiss state of task card
 * @param task task entity
 * @param transitionState animation transition state of task card
 * @param onClick on task click
 */
@Composable
fun PileTask(
    modifier: Modifier,
    dismissState: SwipeToDismissBoxState,
    task: Task,
    transitionState: MutableTransitionState<Boolean>,
    onClick: (task: Task) -> Unit = {}
) {
    val density = LocalDensity.current
    val dim = LocalDim.current

    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onClick(task) }
        )
    ) {
        AnimatedVisibility(
            visibleState = transitionState,
            enter = slideInVertically {
                // 40dp slide in from top
                with(density) { -dim.medium.roundToPx() }
            } + fadeIn(initialAlpha = 0.3f),
            exit = fadeOut()
        ) {
            SwipeToDismissBox(
                modifier = Modifier.fillMaxSize(),
                state = dismissState,
                backgroundContent = {
                    val direction = dismissState.dismissDirection
                    val color by animateColorAsState(
                        when (dismissState.targetValue) {
                            SwipeToDismissBoxValue.Settled -> Color.LightGray
                            SwipeToDismissBoxValue.StartToEnd -> confirm_green
                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                        }, label = "dismiss color"
                    )
                    val alignment = when (direction) {
                        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                        else -> Alignment.CenterStart
                    }
                    val icon = when (direction) {
                        SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Done
                        SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                        else -> Icons.Default.Done
                    }
                    val scale by animateFloatAsState(
                        if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f,
                        label = "dismiss icon scale"
                    )

                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = dim.large),
                        contentAlignment = alignment
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.scale(scale),
                            tint = color
                        )
                    }
                },
                content = {
                    PileEntry(
                        modifier = Modifier
                            .defaultMinSize(minHeight = dim.large)
                            .fillMaxWidth(),
                        taskText = task.title,
                        isRecurring = task.isRecurring
                    )
                }
            )
        }
    }
}

@Composable
fun PileEntry(modifier: Modifier = Modifier, taskText: String, isRecurring: Boolean = false) {
    Card(modifier = modifier.padding(horizontal = LocalDim.current.medium)) {
        Row(
            modifier = modifier.padding(
                horizontal = LocalDim.current.medium,
                vertical = 12.dp
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isRecurring) {
                Icon(
                    Icons.Outlined.AccessTime,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = "recurring task"
                )
                MediumSpacer()
            }
            Text(
                text = taskText,
                textAlign = TextAlign.Center
            )
        }
    }
}
