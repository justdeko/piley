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
import androidx.compose.ui.res.stringArrayResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.model.user.NightMode
import com.dk.piley.model.user.User
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val viewState by viewModel.state.collectAsState()
    SettingsScreen(
        modifier = modifier,
        viewState = viewState,
        onNightModeChange = { viewModel.updateNightMode(it) },
        onDynamicColorChange = { viewModel.updateDynamicColorEnabled(it) }
    )
}

@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewState: SettingsViewState,
    onNightModeChange: (NightMode) -> Unit = {},
    onDynamicColorChange: (Boolean) -> Unit = {},
) {
    val nightModeValues = stringArrayResource(R.array.night_modes).toList()
    Column(modifier = modifier.fillMaxSize()) {
        SettingsSection(title = "Appearance", icon = Icons.Filled.FormatPaint) {
            DropdownSettingsItem(
                title = "Night mode enabled",
                description = "Set whether night mode is enabled.",
                optionLabel = "Night Mode",
                selectedValue = nightModeValues[viewState.user.nightMode.value],
                values = nightModeValues,
                onValueChange = {
                    onNightModeChange(NightMode.fromValue(nightModeValues.indexOf(it)))
                }
            )
            SwitchSettingsItem(
                title = "Dynamic Color enabled",
                description = "Set whether dynamic color is enabled.",
                value = viewState.user.dynamicColorOn,
                onValueChange = onDynamicColorChange
            )
        }
    }
}

@PreviewMainScreen
@Composable
fun SettingsScreenPreview() {
    PileyTheme {
        Surface {
            val state = SettingsViewState(User())
            SettingsScreen(viewState = state)
        }
    }
}