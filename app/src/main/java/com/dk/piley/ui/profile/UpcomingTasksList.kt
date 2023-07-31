package com.dk.piley.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.R
import com.dk.piley.model.task.Task
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.dateTimeString
import com.dk.piley.util.previewUpcomingTasksList
import com.jakewharton.threetenabp.AndroidThreeTen

@Composable
fun UpcomingTasksList(modifier: Modifier = Modifier, pileNameTaskList: List<Pair<String, Task>>) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (pileNameTaskList.isNotEmpty()) {
            pileNameTaskList.forEach { (pileName, task) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(2f)) {
                        Text(
                            text = task.title,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = pileName,
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Start
                        )
                    }
                    Text(
                        modifier = Modifier.weight(1f),
                        text = task.reminder?.dateTimeString() ?: "",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Start
                    )
                }
            }
        } else {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                text = stringResource(R.string.no_upcoming_tasks_hint),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun UpcomingTasksListPreview() {
    AndroidThreeTen.init(LocalContext.current)
    PileyTheme(useDarkTheme = true) {
        UpcomingTasksList(
            modifier = Modifier.fillMaxWidth(),
            pileNameTaskList = previewUpcomingTasksList
        )
    }
}

@Preview
@Composable
fun UpcomingTasksListEmptyPreview() {
    AndroidThreeTen.init(LocalContext.current)
    PileyTheme(useDarkTheme = true) {
        UpcomingTasksList(
            modifier = Modifier.fillMaxWidth(),
            pileNameTaskList = emptyList()
        )
    }
}