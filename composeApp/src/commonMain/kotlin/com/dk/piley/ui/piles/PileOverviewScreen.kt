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
import com.dk.piley.model.pile.Pile
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.nav.pileScreen
import com.dk.piley.util.isTabletWide
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.add_pile_button

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
        onDeletePile = { viewModel.deletePile(it) },
        onSelectPile = { viewModel.setSelectedPile(it) },
        onPileClick = { pileId ->
            navController.navigate("${Screen.Pile.route}?${Screen.Pile.argument}=$pileId")
        },
        onPileLongClick = { pileId ->
            navController.navigate(pileScreen.root + "/" + pileId)
        }
    )
}

/**
 * Pile overview screen content
 *
 * @param modifier generic modifier
 * @param viewState piles view state
 * @param pileTransitionStates pile animation transition states
 * @param onCreatePile on create new pile
 * @param onDeletePile on delete pile
 * @param onSelectPile on select pile as default
 * @param onPileClick on click pile with id
 * @param onPileLongClick on double click pile with id
 */
@Composable
fun PileOverviewScreen(
    modifier: Modifier = Modifier,
    viewState: PilesViewState,
    pileTransitionStates: List<MutableTransitionState<Boolean>>,
    onCreatePile: (String) -> Unit = {},
    onDeletePile: (Pile) -> Unit = {},
    onSelectPile: (Long) -> Unit = {},
    onPileClick: (Long) -> Unit = {},
    onPileLongClick: (Long) -> Unit = {}
) {
    val gridState = rememberLazyStaggeredGridState()
    val isTabletWide = isTabletWide()
    var createPileDialogOpen by rememberSaveable { (mutableStateOf(false)) }
    val expandedFab by remember {
        derivedStateOf {
            gridState.firstVisibleItemScrollOffset <= 0
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
                    viewState.piles,
                    key = { _, pileWithTasks -> pileWithTasks.pile.pileId }
                ) { index, pileWithTasks ->
                    PileCard(
                        modifier = Modifier.animateItem(),
                        expandedMode = isTabletWide,
                        pileWithTasks = pileWithTasks,
                        onSelectPile = onSelectPile,
                        selected = viewState.selectedPileId == pileWithTasks.pile.pileId,
                        onClick = { onPileClick(pileWithTasks.pile.pileId) },
                        onLongClick = { onPileLongClick(pileWithTasks.pile.pileId) },
                        transitionState = pileTransitionStates[index]
                    )
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
