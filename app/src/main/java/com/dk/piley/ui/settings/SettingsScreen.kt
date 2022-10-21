package com.dk.piley.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.model.user.NightMode
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val viewState by viewModel.state.collectAsState()
    SettingsScreen(modifier = modifier, viewState = viewState)
}

@Composable
private fun SettingsScreen(modifier: Modifier = Modifier, viewState: SettingsViewState) {
    Column(modifier = modifier.fillMaxSize()) {
        SettingsSection(title = "Appearance", icon = Icons.Filled.FormatPaint) {
            DropdownSettingsItem(
                title = "Night mode enabled",
                description = "Set whether night mode is enabled.",
                optionLabel = "Night Mode",
                selectedValue = NightMode.SYSTEM.name,
                values = NightMode.values().map { it.name },
                onValueChange = {}
            )
        }
    }
}

@PreviewMainScreen
@Composable
fun SettingsScreenPreview() {
    PileyTheme {
        Surface {
            val state = SettingsViewState()
            SettingsScreen(viewState = state)
        }
    }
}