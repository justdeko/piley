package com.dk.piley.ui.piles

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.user.PileMode
import com.dk.piley.ui.common.EditDescriptionField
import com.dk.piley.ui.common.TitleTopAppBar
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.AlertDialogHelper
import com.dk.piley.util.defaultPadding
import com.dk.piley.util.navigateClearBackstack
import com.dk.piley.util.toLocalDateTime
import kotlinx.datetime.Clock

/**
 * Pile detail screen
 *
 * @param navController generic nav controller
 * @param viewModel pile detail view model
 */
@Composable
fun PileDetailScreen(
    navController: NavController,
    viewModel: PileDetailViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    PileDetailScreen(
        viewState = viewState,
        onDeletePile = {
            navController.navigateClearBackstack(Screen.Piles.route)
            viewModel.deletePile()
        },
        onEditTitle = { viewModel.editTitle(it) },
        onEditDescription = { viewModel.editDescription(it) },
        onSetPileMode = { viewModel.setPileMode(it) },
        onSetPileLimit = { viewModel.setPileLimit(it) },
        onClose = { navController.popBackStack() },
        onClearStatistics = { viewModel.clearStatistics() },
        initialStatisticsGraphTransitionValue = false
    )
}

/**
 * Pile detail screen content
 *
 * @param modifier generic modifier
 * @param viewState pile detail view state
 * @param onDeletePile on pile deletion
 * @param onEditTitle on edit pile title
 * @param onEditDescription on edit pile description
 * @param onSetPileMode on set pile completion mode
 * @param onSetPileLimit on set pile task limit
 * @param onClearStatistics on clear pile statistics
 * @param onClose on close pile screen
 * @param initialStatisticsGraphTransitionValue initial animation transition value of statistics graph
 */
@Composable
fun PileDetailScreen(
    modifier: Modifier = Modifier,
    viewState: PileDetailViewState,
    onDeletePile: () -> Unit = {},
    onEditTitle: (String) -> Unit = {},
    onEditDescription: (String) -> Unit = {},
    onSetPileMode: (PileMode) -> Unit = {},
    onSetPileLimit: (Int) -> Unit = {},
    onClearStatistics: () -> Unit = {},
    onClose: () -> Unit = {},
    initialStatisticsGraphTransitionValue: Boolean = true
) {
    val today = Clock.System.now().toLocalDateTime().date
    var dialogOpen by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    var deletePileDialogOpen by remember { mutableStateOf(false) }

    if (deletePileDialogOpen) {
        AlertDialogHelper(
            title = stringResource(R.string.delete_pile_dialog_title),
            description = stringResource(R.string.delete_pile_dialog_description),
            confirmText = stringResource(R.string.delete_pile_dialog_confirm_button),
            onDismiss = { deletePileDialogOpen = false },
            onConfirm = {
                onDeletePile()
                deletePileDialogOpen = false
            }
        )
    }

    if (dialogOpen) {
        AlertDialogHelper(
            title = stringResource(R.string.clear_statistics_dialog_title),
            description = stringResource(R.string.clear_statistics_dialog_description),
            confirmText = stringResource(R.string.clear_statistics_dialog_confirm),
            onConfirm = {
                onClearStatistics()
                dialogOpen = false
            },
            onDismiss = { dialogOpen = false }
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .defaultPadding()
            .verticalScroll(scrollState)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TitleTopAppBar(
                textValue = viewState.titleTextValue,
                onEdit = onEditTitle,
                canDeleteOrEdit = viewState.canDeleteOrEdit,
                contentDescription = "close the pile detail",
                onButtonClick = onClose
            )
            EditDescriptionField(value = viewState.descriptionTextValue,
                onChange = { onEditDescription(it) }
            )
            PileStatistics(
                doneCount = viewState.doneCount,
                deletedCount = viewState.deletedCount,
                currentCount = viewState.currentCount,
                completedTaskCounts = viewState.completedTaskCounts,
                currentDay = today,
                onClearStatistics = { dialogOpen = true },
                initialGraphTransitionValue = initialStatisticsGraphTransitionValue
            )
            PileDetailSettings(
                viewState = viewState,
                onSetPileMode = onSetPileMode,
                onSetPileLimit = onSetPileLimit
            )
        }
        Button(
            onClick = { deletePileDialogOpen = true },
            enabled = viewState.canDeleteOrEdit,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text(text = stringResource(R.string.delete_pile_button_text))
        }
    }
}

@PreviewMainScreen
@Composable
fun PileDetailScreenPreview() {
    PileyTheme {
        Surface {
            val viewState = PileDetailViewState(
                Pile(
                    name = "Some Pile",
                    description = "Some description",
                    pileMode = PileMode.FIFO,
                    pileLimit = 14
                ),
                titleTextValue = "some text",
                descriptionTextValue = "some description",
                completedTaskCounts = listOf(2, 3, 0, 0, 2, 3, 4),
                doneCount = 2,
                deletedCount = 4,
                currentCount = 1
            )
            PileDetailScreen(viewState = viewState)
        }
    }
}