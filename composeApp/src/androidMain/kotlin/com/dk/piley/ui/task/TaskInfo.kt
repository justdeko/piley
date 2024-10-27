package com.dk.piley.ui.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.model.task.Task
import com.dk.piley.ui.common.FullWidthInfo
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.OutlineCard
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.BigSpacer
import com.dk.piley.util.dateTimeString
import com.dk.piley.util.toLocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.task_created_at_label
import piley.composeapp.generated.resources.task_info_title
import piley.composeapp.generated.resources.task_last_completed_at_label
import piley.composeapp.generated.resources.task_modified_at_label

/**
 * Task info section
 *
 * @param modifier generic modifier
 * @param task task entity to extract info from
 */
@Composable
fun TaskInfo(
    modifier: Modifier = Modifier,
    task: Task
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.task_info_title),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = LocalDim.current.medium),
            textAlign = TextAlign.Start
        )
        OutlineCard(Modifier.padding(LocalDim.current.medium)) {
            FullWidthInfo(
                label = stringResource(Res.string.task_created_at_label),
                value = task.createdAt.toLocalDateTime().dateTimeString()
            )
            if (task.isRecurring && task.completionTimes.isNotEmpty()) {
                BigSpacer()
                FullWidthInfo(
                    label = stringResource(Res.string.task_last_completed_at_label),
                    value = task.completionTimes.last().toLocalDateTime().dateTimeString()
                )
            }
            BigSpacer()
            FullWidthInfo(
                label = stringResource(Res.string.task_modified_at_label),
                value = task.modifiedAt.toLocalDateTime().dateTimeString()
            )
        }
    }
}

@Preview
@Composable
fun TaskInfoPreview() {
    PileyTheme(useDarkTheme = true) {
        TaskInfo(
            Modifier.fillMaxWidth(),
            task = Task(
                createdAt = Clock.System.now().minus(3, DateTimeUnit.HOUR),
                modifiedAt = Clock.System.now()
            )
        )
    }
}

@Preview
@Composable
fun TaskInfoRecurringPreview() {
    PileyTheme(useDarkTheme = true) {
        TaskInfo(
            Modifier.fillMaxWidth(),
            task = Task(
                createdAt = Clock.System.now().minus(3, DateTimeUnit.HOUR), // 3 hours
                modifiedAt = Clock.System.now(),
                isRecurring = true,
                completionTimes = listOf(
                    Clock.System.now().minus(3, DateTimeUnit.HOUR),
                    Clock.System.now().minus(3, DateTimeUnit.HOUR)
                )
            )
        )
    }
}