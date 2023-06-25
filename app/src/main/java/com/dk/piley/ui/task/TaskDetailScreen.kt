package com.dk.piley.ui.task

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.model.task.Task
import com.dk.piley.ui.common.EditDescriptionField
import com.dk.piley.ui.theme.PileyTheme
import kotlinx.coroutines.launch

@Composable
fun TaskDetailScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    TaskDetailScreen(
        viewState = viewState,
        onDeleteTask = {
            navController.popBackStack()
            viewModel.deleteTask()
        },
        onCompleteTask = {
            navController.popBackStack()
            viewModel.completeTask()
        },
        onAddReminder = { viewModel.addReminder(it) },
        onCancelReminder = { viewModel.cancelReminder() },
        onClose = { navController.popBackStack() },
        onEditDesc = { viewModel.editDescription(it) }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    modifier: Modifier = Modifier,
    viewState: TaskDetailViewState,
    onDeleteTask: () -> Unit = {},
    onCompleteTask: () -> Unit = {},
    onClose: () -> Unit = {},
    onEditDesc: (String) -> Unit = {},
    onAddReminder: (ReminderState) -> Unit = {},
    onCancelReminder: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
    AddReminderDrawer(
        drawerState = drawerState,
        onAddReminder = onAddReminder,
        onDeleteReminder = onCancelReminder,
        initialDate = viewState.task.reminder
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                CenterAlignedTopAppBar(title = {
                    Text(
                        text = viewState.task.title,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }, navigationIcon = {
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
                })
                EditDescriptionField(
                    value = viewState.descriptionTextValue,
                    onChange = {
                        onEditDesc(it)
                    }
                )
                ReminderInfo(
                    reminderDateTimeText = viewState.reminderDateTimeText,
                    onAddReminder = { scope.launch { drawerState.open() } },
                )
                TaskInfo(
                    modifier = Modifier.fillMaxWidth(),
                    task = viewState.task
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f, false)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onCompleteTask,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text(text = "Complete")
                }
                Button(
                    onClick = onDeleteTask,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(text = "Delete")
                }
            }
        } // TODO more elements
    }
}

@PreviewMainScreen
@Composable
fun TaskDetailScreenPreview() {
    PileyTheme {
        Surface {
            val state = TaskDetailViewState(task = Task(title = "Hello"))
            TaskDetailScreen(viewState = state)
        }
    }
}