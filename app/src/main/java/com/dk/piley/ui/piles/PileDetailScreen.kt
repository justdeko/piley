package com.dk.piley.ui.piles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.user.PileMode
import com.dk.piley.ui.charts.FrequencyChart
import com.dk.piley.ui.common.EditDescriptionField
import com.dk.piley.ui.common.EditableTitleText
import com.dk.piley.ui.profile.TaskStats
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.AlertDialogHelper
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDateTime

@Composable
fun PileDetailScreen(
    navController: NavController,
    viewModel: PileDetailViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    PileDetailScreen(
        viewState = viewState,
        onDeletePile = {
            navController.popBackStack()
            viewModel.deletePile()
        },
        onEditTitle = { viewModel.editTitle(it) },
        onEditDescription = { viewModel.editDescription(it) },
        onSetPileMode = { viewModel.setPileMode(it) },
        onSetPileLimit = { viewModel.setPileLimit(it) },
        onClose = { navController.popBackStack() },
        onClearStatistics = { viewModel.clearStatistics() }
    )
}

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
    onClose: () -> Unit = {}
) {
    val today = LocalDateTime.now().toLocalDate()
    val dialogOpen = remember { mutableStateOf(false) }
    if (dialogOpen.value) {
        AlertDialogHelper(
            title = "Clear all pile statistics",
            description = "This is will clear all statistics of this pile by deleting all completed and deleted tasks.\nDo you want to continue?",
            confirmText = "Continue",
            onConfirm = onClearStatistics.also { dialogOpen.value = false },
            onDismiss = { dialogOpen.value = false }
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        "close the task detail",
                        modifier = Modifier.scale(
                            1.5F
                        ),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                EditableTitleText(
                    viewState.titleTextValue, viewState.canDeleteOrEdit, onEditTitle
                )
                IconButton(onClick = { }, enabled = false) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        "close the task detail",
                        modifier = Modifier.scale(
                            1.5F
                        ),
                        tint = Color.Transparent
                    )
                }
            }
            EditDescriptionField(value = viewState.descriptionTextValue,
                onChange = { onEditDescription(it) }
            )
            PileDetailSettings(
                viewState = viewState,
                onSetPileMode = onSetPileMode,
                onSetPileLimit = onSetPileLimit
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Statistics",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 16.dp),
                    textAlign = TextAlign.Start
                )
                TextButton(onClick = { dialogOpen.value = true }) {
                    Text("Clear Statistics")
                }
            }
            TaskStats(
                doneCount = viewState.doneCount,
                deletedCount = viewState.deletedCount,
                currentCount = viewState.currentCount,
                tasksOnly = true
            )
            FrequencyChart(
                modifier = Modifier.padding(horizontal = 16.dp),
                weekDayFrequencies = viewState.completedTaskCounts,
                currentDay = today
            )
        }
        Button(
            onClick = onDeletePile,
            enabled = viewState.canDeleteOrEdit,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text(text = "Delete")
        }
    }
}

@PreviewMainScreen
@Composable
fun PileDetailScreenPreview() {
    AndroidThreeTen.init(LocalContext.current)
    PileyTheme {
        Surface {
            val viewState = PileDetailViewState(
                Pile(
                    name = "Some Pile",
                    description = "Some description",
                    pileMode = PileMode.FIFO,
                    pileLimit = 20
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