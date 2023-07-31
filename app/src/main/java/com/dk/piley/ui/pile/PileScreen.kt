package com.dk.piley.ui.pile

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.task.Task
import com.dk.piley.ui.nav.taskScreen
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.getPreviewTransitionStates
import com.dk.piley.util.previewPileWithTasksList
import com.jakewharton.threetenabp.AndroidThreeTen

@Composable
fun PileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: PileViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    val selectedPileViewState by viewModel.selectedPileIndex.collectAsState()
    val taskTransitionStates = viewState.tasks?.map {
        remember {
            MutableTransitionState(false).apply {
                targetState = true
            }
        }
    } ?: emptyList()
    PileScreen(
        modifier = modifier,
        viewState = viewState,
        taskTransitionStates = taskTransitionStates,
        selectedPileIndex = selectedPileViewState,
        onDone = { viewModel.done(it) },
        onDelete = { viewModel.delete(it) },
        onAdd = { viewModel.add(it) },
        onClick = { navController.navigate(taskScreen.root + "/" + it.id) },
        onTitlePageChanged = { page -> viewModel.onPileChanged(page) },
    )
}

@Composable
private fun PileScreen(
    modifier: Modifier = Modifier,
    viewState: PileViewState,
    taskTransitionStates: List<MutableTransitionState<Boolean>>,
    selectedPileIndex: Int = 0,
    onTitlePageChanged: (Int) -> Unit = {},
    onDone: (Task) -> Unit = {},
    onDelete: (Task) -> Unit = {},
    onAdd: (String) -> Unit = {},
    onClick: (Task) -> Unit = {},
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        PileTitlePager(
            modifier = Modifier.fillMaxWidth(),
            pileTitleList = viewState.pileIdTitleList.map { it.second },
            onPageChanged = onTitlePageChanged,
            selectedPageIndex = selectedPileIndex
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            TaskPile(
                modifier = Modifier.fillMaxSize(),
                tasks = viewState.tasks ?: emptyList(),
                pileMode = viewState.pile.pileMode,
                taskTransitionStates = taskTransitionStates,
                onDone = onDone,
                onDelete = onDelete,
                onTaskClick = onClick
            )
            Column(Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = viewState.tasks?.isEmpty() ?: false,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    NoTasksView(
                        Modifier.fillMaxSize()
                    )
                }
            }
        }
        AddTaskField(
            value = query,
            onChange = { v: TextFieldValue -> query = v },
            onDone = {
                if (query.text.isNotBlank()) {
                    if (viewState.autoHideEnabled) {
                        focusManager.clearFocus()
                    }
                    onAdd(query.text.trim())
                    query = TextFieldValue()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.task_empty_not_allowed_hint),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}

@PreviewMainScreen
@Composable
fun ProfileScreenPreview() {
    AndroidThreeTen.init(LocalContext.current)
    PileyTheme {
        Surface {
            val pilesWithTasks = previewPileWithTasksList
            val state = PileViewState(
                pile = pilesWithTasks[0].pile,
                tasks = pilesWithTasks[0].tasks,
                pileIdTitleList = pilesWithTasks.map { Pair(it.pile.pileId, it.pile.name) }
            )
            PileScreen(
                viewState = state,
                taskTransitionStates = pilesWithTasks[0].tasks.getPreviewTransitionStates()
            )
        }
    }
}

@PreviewMainScreen
@Composable
fun ProfileScreenNoTasksPreview() {
    AndroidThreeTen.init(LocalContext.current)
    PileyTheme {
        Surface {
            val state = PileViewState(
                pile = Pile(name = "Daily"),
                tasks = emptyList(),
                pileIdTitleList = listOf(Pair(1, "Pile1"), Pair(2, "Pile2"))
            )
            PileScreen(viewState = state, taskTransitionStates = emptyList())
        }
    }
}