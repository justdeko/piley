package com.dk.piley.ui.piles

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.Task
import com.dk.piley.ui.nav.pileScreen
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
        onCreatePile = { viewModel.createPile(it) },
        onDeletePile = { viewModel.deletePile(it) },
        onSelectPile = { viewModel.setSelectedPile(it) },
        onPileClick = { navController.navigate(pileScreen.root + "/" + it.pileId) }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PileOverviewScreen(
    modifier: Modifier = Modifier,
    viewState: PilesViewState,
    onCreatePile: (String) -> Unit = {},
    onDeletePile: (Pile) -> Unit = {},
    onSelectPile: (Long) -> Unit = {},
    onPileClick: (Pile) -> Unit = {},
) {
    val gridState = rememberLazyGridState()
    val dailyPileName = stringResource(R.string.daily_pile_name)
    var createPileDialogOpen by rememberSaveable { (mutableStateOf(false)) }
    var pileTitle by rememberSaveable { mutableStateOf("") }
    val expandedFab by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0
        }
    }
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { createPileDialogOpen = true },
                expanded = expandedFab,
                icon = { Icon(Icons.Filled.Add, "Add Pile Icon") },
                text = { Text(text = stringResource(R.string.add_pile_button)) },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                        // default pile with name "Daily" cannot be deleted
                        canDelete = pileWithTasks.pile.name != dailyPileName,
                        onSelectPile = onSelectPile,
                        onDeletePile = { onDeletePile(pileWithTasks.pile) },
                        selected = viewState.selectedPileId == pileWithTasks.pile.pileId,
                        onClick = onPileClick
                    )
                }
            }
        }
        if (createPileDialogOpen) {
            AlertDialog(
                title = { Text(stringResource(R.string.create_pile_dialog_title)) },
                text = {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = pileTitle,
                        onValueChange = { pileTitle = it },
                        placeholder = { Text(stringResource(R.string.pile_title_hint)) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    )
                },
                onDismissRequest = { createPileDialogOpen = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onCreatePile(pileTitle)
                            createPileDialogOpen = false
                            pileTitle = ""
                        },
                        enabled = pileTitle.isNotBlank() && pileTitle != dailyPileName
                    ) {
                        Text(stringResource(R.string.pile_create_dialog_confirm_button_text))
                    }
                }, dismissButton = {
                    TextButton(onClick = { createPileDialogOpen = false }) {
                        Text(stringResource(R.string.pile_create_dialog_dismiss_button_text))
                    }
                })
        }
    }
}

@PreviewMainScreen
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