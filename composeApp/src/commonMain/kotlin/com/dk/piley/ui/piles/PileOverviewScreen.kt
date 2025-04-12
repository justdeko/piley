package com.dk.piley.ui.piles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.Piley
import com.dk.piley.ui.nav.Screen
import com.dk.piley.util.isTabletWide
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.add_pile_button
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyStaggeredGridState

/**
 * Pile overview screen
 *
 * @param modifier generic modifier
 * @param navController generic nav controller
 * @param viewModel generic piles view model
 */
@Composable
fun PileOverviewScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: PilesViewModel = viewModel {
        PilesViewModel(
            pileRepository = Piley.getModule().pileRepository,
            userRepository = Piley.getModule().userRepository
        )
    }
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
        onSelectPile = { viewModel.setSelectedPile(it) },
        onPileClick = { pileId ->
            navController.navigate("${Screen.Pile.route}?${Screen.Pile.argument}=$pileId")
        },
        onReorderPiles = { viewModel.reorderPiles(it) }
    )
}

/**
 * Pile overview screen content
 *
 * @param modifier generic modifier
 * @param viewState piles view state
 * @param pileTransitionStates pile animation transition states
 * @param onCreatePile on create new pile
 * @param onSelectPile on select pile as default
 * @param onPileClick on click pile with id
 */
@Composable
fun PileOverviewScreen(
    modifier: Modifier = Modifier,
    viewState: PilesViewState,
    pileTransitionStates: List<MutableTransitionState<Boolean>>,
    onCreatePile: (String) -> Unit = {},
    onSelectPile: (Long) -> Unit = {},
    onPileClick: (Long) -> Unit = {},
    onReorderPiles: (List<Long>) -> Unit = {}
) {
    var piles by remember { mutableStateOf(viewState.piles) }
    LaunchedEffect(viewState.piles.size) {
        piles = viewState.piles.sortedBy {
            val pileOrder = viewState.pileOrder
            val indexInOrder = pileOrder.indexOf(it.pile.pileId)
            // attempt to sort by pile order, if not found, use pile id
            // add pile order size to pile id to ensure new piles are at the end
            if (indexInOrder == -1) it.pile.pileId.toInt() + pileOrder.size else indexInOrder
        }
    }
    val gridState = rememberLazyStaggeredGridState()
    val reorderableLazyStaggeredGridState =
        rememberReorderableLazyStaggeredGridState(gridState) { from, to ->
            piles = piles.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }.also { pileList ->
                onReorderPiles(pileList.map { it.pile.pileId })
            }
        }
    val isTabletWide = isTabletWide()
    var createPileDialogOpen by rememberSaveable { (mutableStateOf(false)) }
    val expandedFab by remember {
        derivedStateOf {
            gridState.firstVisibleItemScrollOffset <= 0
        }
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            AnimatedVisibility(visible = expandedFab, enter = fadeIn(), exit = fadeOut()) {
                ExtendedFloatingActionButton(
                    onClick = { createPileDialogOpen = true },
                    expanded = false,
                    icon = { Icon(Icons.Filled.Add, "Add Pile Icon") },
                    text = { Text(text = stringResource(Res.string.add_pile_button)) },
                )
            }
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
                columns = StaggeredGridCells.Adaptive(if (isTabletWide) 200.dp else 150.dp),
            ) {
                itemsIndexed(
                    piles,
                    key = { _, pileWithTasks -> pileWithTasks.pile.pileId }
                ) { index, pileWithTasks ->
                    ReorderableItem(
                        reorderableLazyStaggeredGridState,
                        key = pileWithTasks.pile.pileId
                    ) { _ ->
                        PileCard(
                            modifier = Modifier.draggableHandle(),
                            expandedMode = isTabletWide,
                            pileWithTasks = pileWithTasks,
                            onSelectPile = onSelectPile,
                            selected = viewState.selectedPileId == pileWithTasks.pile.pileId,
                            onClick = { onPileClick(pileWithTasks.pile.pileId) },
                            transitionState = pileTransitionStates[index]
                        )
                    }
                }
            }
        }
        if (createPileDialogOpen) {
            CreatePileAlertDialog(
                onDismiss = { createPileDialogOpen = false },
                existingPileTitles = viewState.piles.map { it.pile.name },
                onConfirm = {
                    onCreatePile(it)
                    createPileDialogOpen = false
                }
            )
        }
    }
}
