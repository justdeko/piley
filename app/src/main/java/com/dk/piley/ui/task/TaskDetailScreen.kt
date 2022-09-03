package com.dk.piley.ui.task

import android.content.res.Configuration
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material3.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.model.task.Task
import com.dk.piley.ui.theme.PileyTheme
import kotlinx.coroutines.launch
import java.util.*

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
        onClose = { navController.popBackStack() },
        onEditDesc = { viewModel.editDescription(it) }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskDetailScreen(
    modifier: Modifier = Modifier,
    viewState: TaskDetailViewState,
    onDeleteTask: () -> Unit = {},
    onCompleteTask: () -> Unit = {},
    onClose: () -> Unit = {},
    onEditDesc: (String) -> Unit = {},
    onAddReminder: (Date) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
    AddReminderDrawer(content = {
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
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
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            "set a task reminder",
                            modifier = Modifier.scale(
                                1.5F
                            ),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Text(
                    text = viewState.task.title,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                EditDescriptionField(
                    value = viewState.descriptionTextValue,
                    onChange = {
                        onEditDesc(it)
                    }
                )
                Text(
                    text = viewState.reminderDateTimeText ?: "No reminder set",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
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
        }
    }, modifier = Modifier, drawerState = drawerState, onAddReminder = onAddReminder, initialDate = viewState.task.reminder)
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun TaskDetailScreenPreview() {
    PileyTheme {
        Surface {
            val state = TaskDetailViewState(task = Task(title = "Hello"))
            TaskDetailScreen(viewState = state)
        }
    }
}