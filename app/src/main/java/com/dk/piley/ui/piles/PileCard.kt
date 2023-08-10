package com.dk.piley.ui.piles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.TinySpacer
import com.dk.piley.util.defaultPadding

@Composable
fun PileCard(
    modifier: Modifier = Modifier,
    pileWithTasks: PileWithTasks,
    selected: Boolean = false,
    onSelectPile: (Long) -> Unit = {},
    onClick: (pile: Pile) -> Unit = {},
    transitionState: MutableTransitionState<Boolean> = MutableTransitionState(true)
) {
    val density = LocalDensity.current
    val dim = LocalDim.current

    Box {
        AnimatedVisibility(
            visibleState = transitionState,
            enter = slideInHorizontally {
                // slide in from left
                with(density) { -dim.extraLarge.roundToPx() }
            } + fadeIn(initialAlpha = 0.3f),
            exit = fadeOut()
        ) {
            ElevatedCard(
                modifier = modifier
                    .padding(dim.medium)
                    .clip(MaterialTheme.shapes.large)
                    .clickable { onClick(pileWithTasks.pile) }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        IconToggleButton(
                            checked = selected,
                            onCheckedChange = { onSelectPile(pileWithTasks.pile.pileId) }
                        ) {
                            if (selected) {
                                Icon(
                                    Icons.Filled.Home,
                                    contentDescription = "pile selected as default"
                                )
                            } else {
                                Icon(
                                    Icons.Outlined.Home,
                                    contentDescription = "pile not selected as default"
                                )
                            }
                        }
                        Column(Modifier.padding(dim.medium), horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    textAlign = TextAlign.End,
                                    text = "${pileWithTasks.tasks.count { it.status == TaskStatus.DEFAULT }}"
                                )
                                TinySpacer()
                                Icon(
                                    modifier = Modifier.scale(0.8f),
                                    imageVector = Icons.Filled.AddCircle,
                                    contentDescription = "open tasks",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    textAlign = TextAlign.End,
                                    text = "${pileWithTasks.tasks.count { it.status == TaskStatus.DONE }}"
                                )
                                TinySpacer()
                                Icon(
                                    modifier = Modifier.scale(0.8f),
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "completed tasks",
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                    TinySpacer()
                    Text(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .defaultPadding(),
                        text = pileWithTasks.pile.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PileCardPreview() {
    PileyTheme(useDarkTheme = true) {
        val pileWithTasks = PileWithTasks(
            pile = Pile(name = "Default"),
            tasks = listOf(Task(title = "hey"), Task(title = "hey too"))
        )
        PileCard(
            modifier = Modifier.width(200.dp), pileWithTasks = pileWithTasks
        )
    }
}

@Preview
@Composable
fun PileCardLongTitlePreview() {
    PileyTheme(useDarkTheme = true) {
        val pileWithTasks = PileWithTasks(
            pile = Pile(name = "A very long pile name"),
            tasks = listOf(Task(title = "hey"), Task(title = "hey too"))
        )
        PileCard(
            modifier = Modifier.width(200.dp), pileWithTasks = pileWithTasks
        )
    }
}