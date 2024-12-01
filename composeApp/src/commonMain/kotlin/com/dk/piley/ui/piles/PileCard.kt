package com.dk.piley.ui.piles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.util.BigSpacer
import com.dk.piley.util.TinySpacer
import com.dk.piley.util.dateTimeString
import com.dk.piley.util.getUpcomingTasks
import com.dk.piley.util.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.no_upcoming_tasks_hint
import piley.composeapp.generated.resources.upcoming_tasks_section_title

/**
 * Pile card
 *
 * @param modifier generic modifier
 * @param pileWithTasks pile entity with tasks
 * @param expandedMode whether the pile is in expanded mode that displays more information
 * @param selected whether this pile is selected
 * @param onSelectPile on pile selection
 * @param onClick on card click
 * @param onLongClick on card long click
 * @param transitionState card animation transition state
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PileCard(
    modifier: Modifier = Modifier,
    pileWithTasks: PileWithTasks,
    expandedMode: Boolean = false,
    selected: Boolean = false,
    onSelectPile: (Long) -> Unit = {},
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
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
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
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
                    Text(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(
                                start = LocalDim.current.large,
                                end = LocalDim.current.medium
                            ),
                        text = pileWithTasks.pile.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            hyphens = Hyphens.Auto,
                            lineBreak = LineBreak.Paragraph
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    if (expandedMode) {
                        if (pileWithTasks.pile.description.isNotBlank()) {
                            Text(
                                modifier = Modifier.padding(
                                    start = LocalDim.current.large,
                                    end = LocalDim.current.medium
                                ),
                                text = pileWithTasks.pile.description,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        BigSpacer()
                        UpcomingTaskItem(
                            modifier = Modifier.padding(
                                start = LocalDim.current.large,
                                end = LocalDim.current.medium
                            ),
                            task = getUpcomingTasks(listOf(pileWithTasks)).firstOrNull()?.second
                        )
                    }
                    BigSpacer()
                }
            }
        }
    }
}

@Composable
fun UpcomingTaskItem(
    modifier: Modifier = Modifier,
    task: Task?
) {
    Column(modifier = modifier) {
        if (task != null) {
            Text(
                text = stringResource(Res.string.upcoming_tasks_section_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            TinySpacer()
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = task.reminder?.toLocalDateTime()?.dateTimeString() ?: "",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            Text(
                text = stringResource(Res.string.no_upcoming_tasks_hint),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
