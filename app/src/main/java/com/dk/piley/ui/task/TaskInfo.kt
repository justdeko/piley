package com.dk.piley.ui.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.model.task.Task
import com.dk.piley.ui.common.FullWidthInfo
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.OutlineCard
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.BigSpacer
import com.dk.piley.util.dateTimeString
import com.dk.piley.util.toLocalDateTime
import java.time.Instant

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
            text = stringResource(R.string.task_info_title),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = LocalDim.current.medium),
            textAlign = TextAlign.Start
        )
        OutlineCard(Modifier.padding(LocalDim.current.medium)) {
            FullWidthInfo(
                label = stringResource(R.string.task_created_at_label),
                value = task.createdAt.toLocalDateTime().dateTimeString()
            )
            if (task.isRecurring && task.completionTimes.isNotEmpty()) {
                BigSpacer()
                FullWidthInfo(
                    label = stringResource(R.string.task_last_completed_at_label),
                    value = task.completionTimes.last().toLocalDateTime().dateTimeString()
                )
            }
            BigSpacer()
            FullWidthInfo(
                label = stringResource(R.string.task_modified_at_label),
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
                createdAt = Instant.now().minusMillis(1000 * 60 * 60 * 3), // 3 hours
                modifiedAt = Instant.now()
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
                createdAt = Instant.now().minusMillis(1000 * 60 * 60 * 3), // 3 hours
                modifiedAt = Instant.now(),
                isRecurring = true,
                completionTimes = listOf(Instant.now().minusMillis(1000 * 60 * 60 * 3), Instant.now().minusMillis(1000 * 60 * 60 * 1))
            )
        )
    }
}