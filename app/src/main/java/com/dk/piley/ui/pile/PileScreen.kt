package com.dk.piley.ui.pile

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.task.Task
import com.dk.piley.ui.nav.taskScreen
import com.dk.piley.ui.theme.PileyTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: PileViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    PileScreen(
        modifier = modifier,
        viewState = viewState,
        titlePagerState = viewModel.titlePagerState,
        onDone = { viewModel.done(it) },
        onDelete = { viewModel.delete(it) },
        onAdd = { viewModel.add(it) },
        onClick = { navController.navigate(taskScreen.root + "/" + it.id) }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PileScreen(
    modifier: Modifier = Modifier,
    viewState: PileViewState,
    titlePagerState: PagerState = rememberPagerState(),
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
            pagerState = titlePagerState
        )
        TaskPile(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            viewState.tasks,
            viewState.pile.pileMode,
            onDone = onDone,
            onDelete = onDelete,
            onTaskClick = onClick
        )
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
                    Toast.makeText(context, "Task can't be empty", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@PreviewMainScreen
@Composable
fun ProfileScreenPreview() {
    PileyTheme {
        Surface {
            val tasks = listOf(Task(id = 1, title = "Hi there"), Task(id = 2, title = "Sup"))
            val pile = Pile(name = "Daily")
            val state = PileViewState(
                pile = pile,
                tasks = tasks,
                pileIdTitleList = listOf(Pair(1, "Pile1"), Pair(2, "Pile2"))
            )
            PileScreen(viewState = state)
        }
    }
}