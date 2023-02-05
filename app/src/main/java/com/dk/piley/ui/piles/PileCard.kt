package com.dk.piley.ui.piles

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.R
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun PileCard(
    modifier: Modifier = Modifier,
    pileWithTasks: PileWithTasks,
    selected: Boolean = false,
    canDelete: Boolean = true,
    onSelectPile: (Long) -> Unit = {},
    onDeletePile: () -> Unit = {},
) {
    val pileModeValues = stringArrayResource(R.array.pile_modes).toList()
    Card(
        modifier = modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (canDelete) {
                    IconButton(onClick = onDeletePile) {
                        Icon(
                            Icons.Filled.Delete,
                            tint = Color.Red,
                            contentDescription = "delete the pile"
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(16.dp))
                }
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
            }
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = pileWithTasks.pile.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Filled.LibraryAdd,
                    contentDescription = "open tasks",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(text = "${pileWithTasks.tasks.count { it.status == TaskStatus.DEFAULT }}")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Filled.DoneAll,
                    contentDescription = "completed tasks",
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(text = "${pileWithTasks.tasks.count { it.status == TaskStatus.DONE }}")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Filled.Reorder,
                    contentDescription = "pile mode",
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(text = pileModeValues[pileWithTasks.pile.pileMode.value])
            }
            Spacer(modifier = Modifier.height(8.dp))
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