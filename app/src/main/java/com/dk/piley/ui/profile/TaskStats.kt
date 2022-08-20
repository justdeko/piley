package com.dk.piley.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun TaskStats(modifier: Modifier = Modifier, doneCount: Int, deletedCount: Int, currentCount: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        androidx.compose.material3.Text(
            text = "Tasks",
            modifier = modifier
                .padding(all = 16.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            androidx.compose.material3.Text(
                text = doneCount.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            androidx.compose.material3.Text(
                text = currentCount.toString(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            androidx.compose.material3.Text(
                text = deletedCount.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview
@Composable
fun TaskStatsPreview() {
    PileyTheme(useDarkTheme = true) {
        TaskStats(modifier = Modifier.fillMaxWidth(), 2, 3, 1)
    }
}