package com.dk.piley.ui.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.R
import com.dk.piley.common.FullWidthInfo
import com.dk.piley.model.task.Task
import com.dk.piley.ui.common.OutlineCard
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.dateTimeString
import com.dk.piley.util.toLocalDateTime
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.Instant

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
            modifier = Modifier.padding(start = 8.dp),
            textAlign = TextAlign.Start
        )
        OutlineCard(Modifier.padding(8.dp)) {
            FullWidthInfo(
                label = stringResource(R.string.task_created_at_label),
                value = task.createdAt.toLocalDateTime().dateTimeString()
            )
            Spacer(Modifier.size(16.dp))
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
    AndroidThreeTen.init(LocalContext.current)
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