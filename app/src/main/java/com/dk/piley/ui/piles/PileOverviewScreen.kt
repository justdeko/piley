package com.dk.piley.ui.piles

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import com.dk.piley.ui.nav.pileScreen
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.AlertDialogHelper
import com.dk.piley.util.previewPileWithTasksList

@Composable
fun PileOverviewScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: PilesViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    // animation transition states for pile visibility
    val pileTransitionStates = viewState.piles.map {
        remember {
            MutableTransitionState(false).apply {
                targetState = true
            }
        }
    }
    PileOverviewScreen(
        modifier = modifier,
        viewState = viewState,
        pileTransitionStates = pileTransitionStates,
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
    pileTransitionStates: List<MutableTransitionState<Boolean>>,
    onCreatePile: (String) -> Unit = {},
    onDeletePile: (Pile) -> Unit = {},
    onSelectPile: (Long) -> Unit = {},
    onPileClick: (Pile) -> Unit = {}
) {
    val gridState = rememberLazyGridState()
    var createPileDialogOpen by rememberSaveable { (mutableStateOf(false)) }
    var pileTitle by rememberSaveable { mutableStateOf("") }
    val expandedFab by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0
        }
    }
    var deletePileDialogOpen by remember { mutableStateOf(false) }
    var pileToDeleteIndex: Int? by remember { mutableStateOf(null) }

    // delete pile only after animation is finished
    pileTransitionStates.forEachIndexed { index, transition ->
        if (!transition.targetState && !transition.currentState) {
            onDeletePile(viewState.piles[index].pile)
        }
    }

    if (deletePileDialogOpen) {
        AlertDialogHelper(
            title = stringResource(R.string.delete_pile_dialog_title),
            description = stringResource(R.string.delete_pile_dialog_description),
            confirmText = stringResource(R.string.delete_pile_dialog_confirm_button),
            onDismiss = { deletePileDialogOpen = false },
            onConfirm = {
                // play pile delete animation and close dialog
                pileToDeleteIndex?.let { index -> pileTransitionStates[index].targetState = false }
                deletePileDialogOpen = false
            }
        )
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
                itemsIndexed(
                    viewState.piles,
                    key = { _, pileWithTasks -> pileWithTasks.pile.pileId }
                ) { index, pileWithTasks ->
                    PileCard(
                        modifier = Modifier.animateItemPlacement(),
                        pileWithTasks = pileWithTasks,
                        // default pile with id 1 cannot be deleted
                        canDelete = pileWithTasks.pile.pileId != 1L,
                        onSelectPile = onSelectPile,
                        onDeletePile = {
                            pileToDeleteIndex = index
                            deletePileDialogOpen = true
                        },
                        selected = viewState.selectedPileId == pileWithTasks.pile.pileId,
                        onClick = onPileClick,
                        transitionState = pileTransitionStates[index]
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
                        // pile name can't exist yet
                        enabled = !viewState.piles.map { it.pile.name }.contains(pileTitle)
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
            val pilesViewState = PilesViewState(previewPileWithTasksList)
            val transitionStates =
                List(previewPileWithTasksList.size) { MutableTransitionState(true) }
            PileOverviewScreen(viewState = pilesViewState, pileTransitionStates = transitionStates)
        }
    }
}