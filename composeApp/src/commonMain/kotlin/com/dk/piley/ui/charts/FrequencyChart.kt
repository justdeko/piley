package com.dk.piley.ui.charts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.util.lastSevenDays
import com.dk.piley.util.toLocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.no_pile_completed_hint

/**
 * Frequency chart of completed tasks
 *
 * @param weekDayFrequencies list of frequencies for the past 7 days
 * @param currentDay date of the current day
 * @param modifier generic modifier
 * @param initialTransitionValue initial animation transition value, default is true if in inspection mode and false otherwise
 */
@Composable
fun FrequencyChart(
    weekDayFrequencies: List<Int>,
    currentDay: LocalDate = Clock.System.now().toLocalDateTime().date,
    modifier: Modifier = Modifier,
    initialTransitionValue: Boolean = LocalInspectionMode.current
) {
    val max = weekDayFrequencies.maxOrNull() ?: 0
    val dim = LocalDim.current
    val barHeight = 144.dp

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier.wrapContentHeight()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = barHeight)
            ) {
                weekDayFrequencies.forEachIndexed { index, value ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        val boxHeight =
                            if (value == 0) dim.extraLarge else (value / max.toFloat()) * barHeight
                        this@Row.AnimatedVisibility(
                            visibleState = remember {
                                MutableTransitionState(initialTransitionValue).apply {
                                    targetState = true
                                }
                            },
                            enter = expandVertically(animationSpec = tween(delayMillis = index * 50))
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = dim.medium, horizontal = dim.extraSmall)
                                    .height(boxHeight)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when {
                                                value == 0 -> Color.Transparent
                                                index == 6 -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.secondary
                                            },
                                            RoundedCornerShape(
                                                topStart = dim.medium,
                                                topEnd = dim.medium
                                            )
                                        )
                                        .fillMaxSize()
                                )
                            }
                        }
                        this@Row.AnimatedVisibility(
                            visibleState = remember {
                                MutableTransitionState(initialTransitionValue).apply {
                                    targetState = true
                                }
                            },
                            // only show fade in when half of column animated
                            enter = fadeIn(animationSpec = tween(delayMillis = (index * 50) + 25))
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = dim.medium, horizontal = dim.extraSmall)
                                    .height(if (boxHeight < 20.dp) dim.extraLarge else boxHeight)
                                    .background(color = Color.Transparent)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = value.toString(),
                                    color = when {
                                        boxHeight < 20.dp -> MaterialTheme.colorScheme.onBackground
                                        value == 0 -> MaterialTheme.colorScheme.onBackground
                                        index == 6 -> MaterialTheme.colorScheme.onPrimary
                                        else -> MaterialTheme.colorScheme.onSecondary
                                    },
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
            if (weekDayFrequencies.none { it > 0 }) {
                Text(
                    text = stringResource(Res.string.no_pile_completed_hint),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(
                            start = dim.veryLarge,
                            end = dim.veryLarge,
                            bottom = dim.extraLarge
                        ),
                    textAlign = TextAlign.Center
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dim.medium)
        ) {
            lastSevenDays(currentDay).forEachIndexed { index, day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = if (index == 6) FontWeight.Bold else null,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

