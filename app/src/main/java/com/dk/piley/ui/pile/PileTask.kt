package com.dk.piley.ui.pile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.model.task.Task
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.ui.theme.confirm_green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PileTask(
    modifier: Modifier,
    dismissState: DismissState,
    task: Task,
    transitionState: MutableTransitionState<Boolean>,
    onClick: (task: Task) -> Unit = {}
) {
    val density = LocalDensity.current
    val dim = LocalDim.current

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null, // TODO indication only when clicking, not when holding
            onClick = { onClick(task) }
        ),
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> Color.LightGray
                    DismissValue.DismissedToEnd -> confirm_green
                    DismissValue.DismissedToStart -> MaterialTheme.colorScheme.error
                }, label = "dismiss color"
            )
            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Done
                DismissDirection.EndToStart -> Icons.Default.Delete
            }
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f,
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
        dismissContent = {
            AnimatedVisibility(
                visibleState = transitionState,
                enter = slideInVertically {
                    // 40dp slide in from top
                    with(density) { -dim.extraLarge.roundToPx() }
                } + fadeIn(initialAlpha = 0.3f)
            ) {
                PileEntry(
                    modifier = Modifier
                        .defaultMinSize(minHeight = dim.large)
                        .fillMaxWidth(),
                    taskText = task.title
                )
            }
        }
    )
}

@Composable
fun PileEntry(modifier: Modifier = Modifier, taskText: String) {
    Card(modifier = modifier.padding(horizontal = LocalDim.current.medium)) {
        Text(
            text = taskText,
            modifier = modifier
                .padding(horizontal = LocalDim.current.large, vertical = 12.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PileEntryPreview() {
    PileyTheme(useDarkTheme = true) {
        PileEntry(modifier = Modifier.fillMaxWidth(), taskText = "Hey there")
    }
}