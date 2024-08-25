package com.dk.piley.ui.reminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.util.MediumSpacer
import com.dk.piley.util.calculateDelayDuration
import com.dk.piley.util.defaultPadding

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DelaySelection(
    modifier: Modifier = Modifier,
    defaultRangeIndex: Int = 0,
    defaultDurationIndex: Int = 0,
    onSetDelay: (Long) -> Unit = {},
    onSelectValues: (DelayRange, Int) -> Unit = { _, _ -> },
) {
    val dim = LocalDim.current
    var rangeSelectionIndex by remember { mutableIntStateOf(defaultRangeIndex) }
    var durationSelectionIndex by remember { mutableIntStateOf(defaultDurationIndex) }
    val delayRanges = stringArrayResource(R.array.delay_by).toList()

    Column(modifier.defaultPadding()) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(dim.large)) {
            delaySelectionMap.keys.forEachIndexed { index, delayRange ->
                FilterChip(
                    onClick = {
                        rangeSelectionIndex = index
                        durationSelectionIndex = 0
                        onSetDelay(
                            calculateDelayDuration(
                                delayRange = delayRange,
                                delayDurationIndex = 0
                            )
                        )
                        onSelectValues(delayRange, 0)
                    },
                    label = {
                        Text(delayRanges[delayRange.ordinal])
                    },
                    selected = index == rangeSelectionIndex,
                )
            }
        }
        MediumSpacer()
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dim.large)
        ) {
            delaySelectionMap[DelayRange.entries[rangeSelectionIndex]]
                ?.forEachIndexed { index, delayDuration ->
                    FilterChip(
                        onClick = {
                            val delayRange = DelayRange.entries[rangeSelectionIndex]
                            durationSelectionIndex = index
                            onSetDelay(
                                calculateDelayDuration(
                                    delayRange = delayRange,
                                    delayDurationIndex = index
                                )
                            )
                            onSelectValues(delayRange, index)
                        },
                        label = {
                            Text(delayDuration.toString())
                        },
                        selected = index == durationSelectionIndex,
                    )
                }
        }
    }
}

@Preview
@Composable
fun DelaySelectionPreview() {
    DelaySelection()
}