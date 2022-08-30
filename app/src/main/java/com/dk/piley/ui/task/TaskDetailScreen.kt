package com.dk.piley.ui.task

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.model.task.Task
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun TaskDetailScreen(
    taskId: Long?,
    navController: NavHostController = rememberNavController(),
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    if (taskId != null) {
        viewModel.setTask(taskId)
    }
    TaskDetailScreen(viewState = viewState, onDeleteTask = {
        navController.popBackStack()
        viewModel.deleteTask()
    })
}

@Composable
fun TaskDetailScreen(
    modifier: Modifier = Modifier,
    viewState: TaskDetailViewState,
    onDeleteTask: () -> Unit = {},
    onCompleteTask: () -> Unit = {}

) {
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = viewState.task.title,
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            modifier = Modifier
                .weight(1f, false)
                .fillMaxWidth()
                .padding(16.dp),
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
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun TaskDetailScreenPreview() {
    PileyTheme {
        Surface {
            val state = TaskDetailViewState(Task(title = "Hello"))
            TaskDetailScreen(viewState = state)
        }
    }
}