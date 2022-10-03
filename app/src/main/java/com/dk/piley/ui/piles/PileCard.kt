package com.dk.piley.ui.piles

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun PileCard(modifier: Modifier = Modifier, pileWithTasks: PileWithTasks) {
    Card(
        modifier = modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = pileWithTasks.pile.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
            ) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "open tasks",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(text = "${pileWithTasks.tasks.count { it.status == TaskStatus.DEFAULT }}")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
            ) {
                Icon(
                    imageVector = Icons.Filled.DoneAll,
                    contentDescription = "completed tasks",
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(text = "${pileWithTasks.tasks.count { it.status == TaskStatus.DONE }}")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { /*TODO*/ },
                enabled = false
            ) {
                Text(text = "Set Current")
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