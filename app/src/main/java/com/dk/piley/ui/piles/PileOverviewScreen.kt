package com.dk.piley.ui.piles

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
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
import com.dk.piley.util.getPreviewTransitionStates
import com.dk.piley.util.pileTitleCharacterLimit
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
    val gridState = rememberLazyStaggeredGridState()
    var createPileDialogOpen by rememberSaveable { (mutableStateOf(false)) }
    var pileTitle by rememberSaveable { mutableStateOf("") }
    val expandedFab by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0
        }
    }

    // delete pile only after animation is finished
    pileTransitionStates.forEachIndexed { index, transition ->
        if (!transition.targetState && !transition.currentState) {
            onDeletePile(viewState.piles[index].pile)
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
            LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxSize(),
                state = gridState,
                columns = StaggeredGridCells.Adaptive(150.dp),
            ) {
                itemsIndexed(
                    viewState.piles,
                    key = { _, pileWithTasks -> pileWithTasks.pile.pileId }
                ) { index, pileWithTasks ->
                    PileCard(
                        modifier = Modifier.animateItemPlacement(),
                        pileWithTasks = pileWithTasks,
                        onSelectPile = onSelectPile,
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
                        onValueChange = {
                            if (it.length <= pileTitleCharacterLimit) {
                                pileTitle = it
                            }
                        },
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
                        enabled = !viewState.piles.map { it.pile.name }
                            .contains(pileTitle) && pileTitle.isNotBlank()
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
            val transitionStates = previewPileWithTasksList.getPreviewTransitionStates()
            PileOverviewScreen(viewState = pilesViewState, pileTransitionStates = transitionStates)
        }
    }
}