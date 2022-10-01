package com.dk.piley.ui.piles

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.Task
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun PileOverviewScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: PilesViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    PileOverviewScreen(viewState = viewState, modifier = modifier)
}

@Composable
fun PileOverviewScreen(
    modifier: Modifier = Modifier,
    viewState: PilesViewState,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Adaptive(minSize = 128.dp)
        ) {
            items(viewState.piles, key = { it.pile.pileId }) { pile ->
                PileCard(pileWithTasks = pile)
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PileOverviewScreenPreview() {
    PileyTheme {
        Surface {
            val tasks = listOf(Task(id = 0, title = "Hi there"), Task(id = 1, title = "Sup"))
            val piles = listOf(Pile(name = "Default"), Pile(pileId = 1, name = "Custom1"))
            val pilesWithTasks = listOf(
                PileWithTasks(pile = piles[0], tasks = tasks),
                PileWithTasks(pile = piles[1], tasks = tasks.map { it.copy(pileId = 1) })
            )
            val pilesViewState = PilesViewState(pilesWithTasks)
            PileOverviewScreen(viewState = pilesViewState)
        }
    }
}