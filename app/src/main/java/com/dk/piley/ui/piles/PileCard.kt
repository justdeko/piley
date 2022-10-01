package com.dk.piley.ui.piles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.Task
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun PileCard(modifier: Modifier = Modifier, pileWithTasks: PileWithTasks) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .aspectRatio(1f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = pileWithTasks.pile.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

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
            modifier = Modifier
                .width(200.dp), pileWithTasks = pileWithTasks
        )
    }
}