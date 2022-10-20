package com.dk.piley.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    SettingsScreen(modifier = modifier)
}

@Composable
private fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {

    }
}

@PreviewMainScreen
@Composable
fun SettingsScreenPreview() {
    PileyTheme {
        Surface {
            SettingsScreen()
        }
    }
}