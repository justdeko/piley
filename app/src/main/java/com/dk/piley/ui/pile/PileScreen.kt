package com.dk.piley.ui.pile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun PileScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: PileViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    val context = LocalContext.current
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
                .fillMaxWidth()
                .weight(1f),
            viewState.tasks,
            onDone = {
                viewModel.done(it)
            },
            onDelete = {
                viewModel.delete(it)
            }
        )
        AddTaskField(
            value = query,
            onChange = { v: TextFieldValue -> query = v },
            onDone = {
                if (query.text.isNotBlank()) {
                    viewModel.add(query.text)
                } else {
                    Toast.makeText(context, "Task can't be empty", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}