package com.dk.piley.ui.piles

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    PileOverviewScreen(
        modifier = modifier,
        viewState = viewState,
        onCreatePile = { viewModel.createPile("sup") },
        onDeletePile = { viewModel.deletePile(it) },
        onSelectPile = { viewModel.setSelectedPile(it) },
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PileOverviewScreen(
    modifier: Modifier = Modifier,
    viewState: PilesViewState,
    onCreatePile: () -> Unit = {},
    onDeletePile: (Pile) -> Unit = {},
    onSelectPile: (Long) -> Unit = {},
) {
    val gridState = rememberLazyGridState()
    val expandedFab by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0
        }
    }
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreatePile,
                expanded = expandedFab,
                icon = { Icon(Icons.Filled.Add, "Add Pile Icon") },
                text = { Text(text = "Add Pile") },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                state = gridState,
                columns = GridCells.Adaptive(150.dp),
            ) {
                items(viewState.piles, key = { it.pile.pileId }) { pileWithTasks ->
                    PileCard(
                        modifier = Modifier.animateItemPlacement(),
                        pileWithTasks = pileWithTasks,
                        onSelectPile = onSelectPile,
                        onDeletePile = { onDeletePile(pileWithTasks.pile) },
                        selected = viewState.selectedPileId == pileWithTasks.pile.pileId
                    )
                }
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
            val tasks = listOf(Task(id = 1, title = "Hi there"), Task(id = 2, title = "Sup"))
            val piles = listOf(Pile(name = "Daily"), Pile(pileId = 2, name = "Custom1"))
            val pilesWithTasks = listOf(
                PileWithTasks(pile = piles[0], tasks = tasks),
                PileWithTasks(pile = piles[1], tasks = tasks.map { it.copy(pileId = 2) })
            )
            val pilesViewState = PilesViewState(pilesWithTasks)
            PileOverviewScreen(viewState = pilesViewState)
        }
    }
}