package com.dk.piley

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dk.piley.model.task.Task
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import com.dk.piley.ui.pile.PileViewModel
import com.dk.piley.ui.theme.PileyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PileyTheme {
                Home()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    modifier: Modifier = Modifier,
    viewModel: PileViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        var query by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(
                TextFieldValue("")
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Pile(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth(),
                viewState.tasks
            )
            AddTaskField(
                value = query,
                onChange = { v: TextFieldValue -> query = v },
                onDone = { viewModel.add(query.text) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Pile(modifier: Modifier = Modifier, tasks: List<Task> = emptyList()) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
    ) {
        items(tasks, key = { it.id }) { task ->
            PileEntry(
                modifier = Modifier
                    .animateItemPlacement()
                    .defaultMinSize(minHeight = 20.dp)
                    .fillMaxWidth(),
                taskText = task.title
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PileEntry(modifier: Modifier = Modifier, taskText: String) {
    Card(modifier = modifier.padding(horizontal = 8.dp)) {
        Text(
            text = taskText,
            modifier = modifier
                .padding(all = 16.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AddTaskField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onChange: (TextFieldValue) -> Unit,
    onDone: KeyboardActionScope.() -> Unit
) {
    TextField(
        colors = TextFieldDefaults.textFieldColors(
            disabledTextColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        value = value,
        onValueChange = onChange,
        placeholder = { Text("Add your task here") },
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = onDone),
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PileyTheme(useDarkTheme = true) {
        Home()
    }
}