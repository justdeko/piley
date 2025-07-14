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
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dk.piley.Piley
import com.dk.piley.model.pile.PileColor
import com.dk.piley.model.task.Task
import com.dk.piley.model.user.PileMode
import com.dk.piley.ui.common.EditDescriptionField
import com.dk.piley.ui.common.TitleTopAppBar
import com.dk.piley.ui.common.TwoPaneScreen
import com.dk.piley.ui.nav.Screen
import com.dk.piley.util.AlertDialogHelper
import com.dk.piley.util.MediumSpacer
import com.dk.piley.util.defaultPadding
import com.dk.piley.util.navigateClearBackstack
import com.dk.piley.util.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.clear_statistics_dialog_confirm
import piley.composeapp.generated.resources.clear_statistics_dialog_description
import piley.composeapp.generated.resources.clear_statistics_dialog_title
import piley.composeapp.generated.resources.delete_pile_button_text
import piley.composeapp.generated.resources.delete_pile_dialog_confirm_button
import piley.composeapp.generated.resources.delete_pile_dialog_description
import piley.composeapp.generated.resources.delete_pile_dialog_title
import kotlin.time.Clock

/**
 * Pile detail screen
 *
 * @param modifier generic modifier
 * @param navController generic nav controller
 * @param viewModel pile detail view model
 */
@Composable
fun PileDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PileDetailViewModel = viewModel {
        PileDetailViewModel(
            pileRepository = Piley.getModule().pileRepository,
            taskRepository = Piley.getModule().taskRepository,
            userRepository = Piley.getModule().userRepository,
            savedStateHandle = createSavedStateHandle()
        )
    }
) {
    val viewState by viewModel.state.collectAsState()
    PileDetailScreen(
        modifier = modifier,
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
        onTaskUndo = { viewModel.undoTask(it) },
        onSelectColor = { viewModel.selectColor(it) },
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
 * @param onTaskUndo on undo task completion or removal
 * @param onSelectColor on select pile color
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
    onTaskUndo: (Task) -> Unit = {},
    onSelectColor: (PileColor) -> Unit = {},
    initialStatisticsGraphTransitionValue: Boolean = true
) {
    val today = Clock.System.now().toLocalDateTime().date
    var dialogOpen by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    var deletePileDialogOpen by remember { mutableStateOf(false) }

    if (deletePileDialogOpen) {
        AlertDialogHelper(
            title = stringResource(Res.string.delete_pile_dialog_title),
            description = stringResource(Res.string.delete_pile_dialog_description),
            confirmText = stringResource(Res.string.delete_pile_dialog_confirm_button),
            onDismiss = { deletePileDialogOpen = false },
            onConfirm = {
                onDeletePile()
                deletePileDialogOpen = false
            }
        )
    }

    if (dialogOpen) {
        AlertDialogHelper(
            title = stringResource(Res.string.clear_statistics_dialog_title),
            description = stringResource(Res.string.clear_statistics_dialog_description),
            confirmText = stringResource(Res.string.clear_statistics_dialog_confirm),
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
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
            TitleTopAppBar(
                textValue = viewState.titleTextValue,
                onEdit = onEditTitle,
                canEdit = true,
                contentDescription = "close the pile detail",
                onButtonClick = onClose
            )
            TwoPaneScreen(
                mainContent = { isTablet ->
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        EditDescriptionField(value = viewState.descriptionTextValue,
                            onChange = { onEditDescription(it) }
                        )
                        ColorPicker(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            selectedColor = viewState.pile.color,
                            onSelected = onSelectColor
                        )
                        MediumSpacer()
                        PileStatistics(
                            doneCount = viewState.doneCount,
                            recurringCount = viewState.recurringCount,
                            currentCount = viewState.currentCount,
                            completedTaskCounts = viewState.completedTaskCounts,
                            currentDay = today,
                            onClearStatistics = { dialogOpen = true },
                            initialGraphTransitionValue = initialStatisticsGraphTransitionValue
                        )
                        if (!isTablet) {
                            PileDetailSettings(
                                viewState = viewState,
                                onSetPileMode = onSetPileMode,
                                onSetPileLimit = onSetPileLimit
                            )
                            RecentlyModifiedTasks(tasks = viewState.modifiedTasks, onUndo = onTaskUndo)
                        }
                    }
                },
                detailContent = {
                    val expandedState = remember { mutableStateOf(true) }
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        RecentlyModifiedTasks(tasks = viewState.modifiedTasks, onUndo = onTaskUndo)
                        PileDetailSettings(
                            viewState = viewState,
                            onSetPileMode = onSetPileMode,
                            onSetPileLimit = onSetPileLimit,
                            expandedState = expandedState
                        )
                    }
                }
            )
        }
        Button(
            onClick = { deletePileDialogOpen = true },
            enabled = viewState.canDelete,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text(text = stringResource(Res.string.delete_pile_button_text))
        }
    }
}
