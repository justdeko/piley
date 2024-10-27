package com.dk.piley.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.Piley
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.model.user.NightMode
import com.dk.piley.model.user.PileMode
import com.dk.piley.model.user.User
import com.dk.piley.reminder.DelayRange
import com.dk.piley.ui.common.ContentAlertDialog
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.TitleTopAppBar
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.profile.AppInfo
import com.dk.piley.ui.reminder.DelaySelection
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.ui.viewModelFactory
import com.dk.piley.util.IndefiniteProgressBar
import com.dk.piley.util.navigateClearBackstack

/**
 * Settings screen
 *
 * @param modifier generic modifier
 * @param navController generic nav controller
 * @param snackbarHostState host state for showing snackbars
 * @param viewModel Settings view model
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: SettingsViewModel = viewModel(
        factory = viewModelFactory {
            SettingsViewModel(
                userRepository = Piley.appModule.userRepository,
                pileRepository = Piley.appModule.pileRepository
            )
        }
    )
) {
    val viewState by viewModel.state.collectAsState()
    val context = LocalContext.current

    // snackbar handler
    viewState.message?.let { message ->
        LaunchedEffect(message, snackbarHostState) {
            snackbarHostState.showSnackbar(
                when (message) {
                    StatusMessage.USER_UPDATE_SUCCESSFUL -> context.getString(R.string.user_update_success_info)
                    StatusMessage.USER_UPDATE_ERROR -> context.getString(R.string.update_user_error_wrong_password)
                    StatusMessage.USER_DELETED -> context.getString(R.string.delete_user_success_info)
                    StatusMessage.USER_DELETED_ERROR -> context.getString(R.string.delete_user_error_wrong_password)
                }
            )
            // reset message
            viewModel.resetMessage()
        }
    }

    if (viewState.userDeleted) {
        LaunchedEffect(true) {
            navController.navigateClearBackstack(Screen.Splash.route)
        }
    }

    SettingsScreen(
        modifier = modifier,
        viewState = viewState,
        onNightModeChange = { viewModel.updateNightMode(it) },
        onDynamicColorChange = { viewModel.updateDynamicColorEnabled(it) },
        onPileModeChange = { viewModel.updateDefaultPileMode(it) },
        onResetPileModes = { viewModel.onResetPileModes() },
        onAutoHideKeyboardChange = { viewModel.updateHideKeyboardEnabled(it) },
        onReminderDelayChange = { range, index -> viewModel.updateReminderDelay(range, index) },
        onEditUser = { result -> viewModel.updateUser(result) },
        onDeleteUser = { viewModel.deleteUser() },
        onCloseSettings = { navController.popBackStack() },
        onStartTutorial = { navController.navigateClearBackstack(Screen.Intro.route) },
        onShowRecurringTasks = { shown -> viewModel.setShowRecurringTasks(shown) }
    )
}

/**
 * Settings screen content
 *
 * @param modifier generic modifier
 * @param viewState settings view state
 * @param onNightModeChange on change night mode setting
 * @param onDynamicColorChange on change dynamic color enabled setting
 * @param onPileModeChange on change default pile mode setting
 * @param onResetPileModes on reset pile modes for all piles to free
 * @param onAutoHideKeyboardChange on change auto hide keyboard enabled setting
 * @param onReminderDelayChange on change default reminder delay setting
 * @param onEditUser on edit user
 * @param onDeleteUser on delete user
 * @param onCloseSettings on close settings screen
 * @param onStartTutorial on restart tutorial
 * @param onShowRecurringTasks show recurring tasks by default
 */
@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewState: SettingsViewState,
    onNightModeChange: (NightMode) -> Unit = {},
    onDynamicColorChange: (Boolean) -> Unit = {},
    onPileModeChange: (PileMode) -> Unit = {},
    onResetPileModes: () -> Unit = {},
    onAutoHideKeyboardChange: (Boolean) -> Unit = {},
    onReminderDelayChange: (DelayRange, Int) -> Unit = { _, _ -> },
    onEditUser: (EditUserResult) -> Unit = {},
    onDeleteUser: () -> Unit = {},
    onCloseSettings: () -> Unit = {},
    onStartTutorial: () -> Unit = {},
    onShowRecurringTasks: (Boolean) -> Unit = {},
) {
    val dim = LocalDim.current
    val nightModeValues = stringArrayResource(R.array.night_modes).toList()
    val pileModeValues = stringArrayResource(R.array.pile_modes).toList()
    val scrollState = rememberScrollState()
    var editUserDialogOpen by remember { mutableStateOf(false) }
    var deleteUserDialogOpen by remember { mutableStateOf(false) }

    if (editUserDialogOpen) {
        ContentAlertDialog(onDismiss = { editUserDialogOpen = false }) {
            EditUserContent(
                existingName = viewState.user.name,
                onConfirm = { result ->
                    editUserDialogOpen = false
                    onEditUser(result)
                },
                onCancel = { editUserDialogOpen = false }
            )
        }
    }

    if (deleteUserDialogOpen) {
        ContentAlertDialog(onDismiss = { deleteUserDialogOpen = false }) {
            DeleteUserContent(
                onConfirm = {
                    deleteUserDialogOpen = false
                    onDeleteUser()
                },
                onCancel = {
                    deleteUserDialogOpen = false
                }
            )
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            TitleTopAppBar(
                textValue = stringResource(R.string.settings_screen_title),
                justTitle = true,
                onButtonClick = onCloseSettings,
                contentDescription = "close the task detail"
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(dim.medium)
            ) {
                SettingsSection(
                    title = stringResource(R.string.settings_section_appearance_title),
                    icon = Icons.Filled.FormatPaint
                ) {
                    DropdownSettingsItem(
                        title = stringResource(R.string.night_mode_enabled_setting_title),
                        description = stringResource(R.string.night_mode_enabled_setting_description),
                        optionLabel = stringResource(R.string.night_mode_enabled_setting_option_label),
                        selectedValue = nightModeValues[viewState.user.nightMode.value],
                        values = nightModeValues,
                        onValueChange = {
                            onNightModeChange(NightMode.fromValue(nightModeValues.indexOf(it)))
                        }
                    )
                    SwitchSettingsItem(
                        title = stringResource(R.string.dynamic_color_enabled_setting_title),
                        description = stringResource(R.string.dynamic_color_enabled_setting_description),
                        value = viewState.user.dynamicColorOn,
                        onValueChange = onDynamicColorChange
                    )
                }
                HorizontalDivider()
                SettingsSection(
                    title = stringResource(R.string.settings_section_piles_title),
                    icon = Icons.Filled.ViewAgenda
                ) {
                    DropdownSettingsItem(
                        title = stringResource(R.string.default_pile_mode_setting_title),
                        description = stringResource(R.string.default_pile_mode_setting_description),
                        optionLabel = stringResource(R.string.default_pile_mode_setting_option_label),
                        selectedValue = pileModeValues[viewState.user.pileMode.value],
                        values = pileModeValues,
                        onValueChange = {
                            onPileModeChange(PileMode.fromValue(pileModeValues.indexOf(it)))
                        }
                    )
                    SettingsItem(
                        title = stringResource(R.string.reset_all_pile_modes_setting_title),
                        description = stringResource(R.string.reset_all_pile_modes_setting_description),
                        onClick = onResetPileModes
                    )
                    SwitchSettingsItem(
                        title = stringResource(R.string.automatically_hide_keyboard_setting_title),
                        description = stringResource(R.string.automatically_hide_keyboard_setting_description),
                        value = viewState.user.autoHideKeyboard,
                        onValueChange = onAutoHideKeyboardChange
                    )
                    SwitchSettingsItem(
                        title = stringResource(R.string.show_recurring_tasks_setting_title),
                        description = stringResource(R.string.show_recurring_tasks_setting_description),
                        value = viewState.user.showRecurringTasks,
                        onValueChange = onShowRecurringTasks
                    )
                }
                HorizontalDivider()
                SettingsSection(
                    title = stringResource(R.string.settings_section_notifications_title),
                    icon = Icons.Filled.Notifications
                ) {
                    SettingsItem(
                        title = stringResource(R.string.reminder_delay_duration_setting_title),
                        description = stringResource(R.string.reminder_delay_duration_setting_description),
                    ) {
                        DelaySelection(
                            defaultRangeIndex = viewState.user.defaultReminderDelayRange.ordinal,
                            defaultDurationIndex = viewState.user.defaultReminderDelayIndex,
                            onSelectValues = onReminderDelayChange
                        )
                    }
                }
                HorizontalDivider()
                SettingsSection(
                    title = stringResource(R.string.settings_section_user_title),
                    icon = Icons.Filled.Person
                ) {
                    SettingsItem(
                        title = stringResource(R.string.update_user_setting_title),
                        description = stringResource(R.string.update_user_setting_description),
                        onClick = { editUserDialogOpen = true }
                    )
                    SettingsItem(
                        title = stringResource(R.string.delete_user_setting_title),
                        description = stringResource(R.string.delete_user_setting_description),
                        onClick = { deleteUserDialogOpen = true }
                    )
                    SettingsItem(
                        title = stringResource(R.string.start_tutorial_setting_title),
                        description = stringResource(R.string.start_tutorial_setting_description),
                        onClick = { onStartTutorial() }
                    )
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        AppInfo()
                    }
                }
            }
            IndefiniteProgressBar(visible = viewState.loading)
        }
    }
}

@PreviewMainScreen
@Preview(heightDp = 1000)
@Composable
fun SettingsScreenPreview() {
    PileyTheme {
        Surface {
            val state = SettingsViewState(User(), loading = true)
            SettingsScreen(viewState = state)
        }
    }
}