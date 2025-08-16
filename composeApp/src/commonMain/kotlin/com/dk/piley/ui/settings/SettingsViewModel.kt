package com.dk.piley.ui.settings

import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.backup.ExportResult
import com.dk.piley.model.backup.IDatabaseExporter
import com.dk.piley.model.backup.ImportResult
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.user.NightMode
import com.dk.piley.model.user.PileMode
import com.dk.piley.model.user.User
import com.dk.piley.model.user.UserRepository
import com.dk.piley.reminder.DelayRange
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Settings view model
 *
 * @property userRepository user repository instance
 * @property pileRepository pile repository instance
 * @property databaseExporter database exporter instance
 */
class SettingsViewModel(
    private val userRepository: UserRepository,
    private val pileRepository: PileRepository,
    private val databaseExporter: IDatabaseExporter,
) : StatefulViewModel<SettingsViewState>(SettingsViewState()) {

    init {
        viewModelScope.launch {
            val userFlow = userRepository.getSignedInUserNotNullFlow()
            val skipSplashScreenFlow = userRepository.getSkipSplashScreenFlow()
            collectState(
                userFlow.combine(skipSplashScreenFlow) { user, skipSplashScreen ->
                    state.value.copy(
                        user = user,
                        skipSplashScreen = skipSplashScreen
                    )
                }
            )
        }
    }

    /**
     * Update night mode enabled setting
     *
     * @param nightMode new night mode value
     */
    fun updateNightMode(nightMode: NightMode) {
        viewModelScope.launch {
            userRepository.insertUser(state.value.user.copy(nightMode = nightMode))
        }
    }

    /**
     * Update dynamic color enabled setting
     *
     * @param enabled whether dynamic color is enabled
     */
    fun updateDynamicColorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userRepository.insertUser(state.value.user.copy(dynamicColorOn = enabled))
        }
    }

    /**
     * Update skip splash screen setting
     *
     * @param skip whether the splash screen should be skipped
     */
    fun updateSkipSplashScreen(skip: Boolean) {
        viewModelScope.launch {
            userRepository.setSkipSplashScreen(skip)
        }
    }

    /**
     * Update default pile mode setting
     *
     * @param pileMode new pile mode value
     */
    fun updateDefaultPileMode(pileMode: PileMode) {
        viewModelScope.launch {
            userRepository.insertUser(state.value.user.copy(pileMode = pileMode))
        }
    }

    /**
     * Reset all pile modes to free
     *
     */
    fun onResetPileModes() {
        viewModelScope.launch {
            pileRepository.resetPileModes()
        }
    }

    /**
     * Update hide keyboard automatically enabled setting
     *
     * @param hide whether the auto hide is enabled
     */
    fun updateHideKeyboardEnabled(hide: Boolean) {
        viewModelScope.launch {
            userRepository.insertUser(state.value.user.copy(autoHideKeyboard = hide))
        }
    }

    /**
     * Set show recurring tasks by default setting
     *
     * @param shown whether recurring tasks should be shown
     */
    fun setShowRecurringTasks(shown: Boolean) {
        viewModelScope.launch {
            userRepository.insertUser(state.value.user.copy(showRecurringTasks = shown))
        }
    }

    /**
     * Update reminder delay setting
     *
     * @param range the selected default delay range
     * @param durationIndex index of the selected default duration
     */
    fun updateReminderDelay(range: DelayRange, durationIndex: Int) {
        viewModelScope.launch {
            userRepository.insertUser(
                state.value.user.copy(
                    defaultReminderDelayRange = range,
                    defaultReminderDelayIndex = durationIndex
                )
            )
        }
    }

    /**
     * Delete user
     *
     */
    fun deleteUser() {
        viewModelScope.launch {
            val existingUser = userRepository.getSignedInUserEntity()
            // only delete user if it still exists
            if (existingUser != null) {
                deleteUserLocally()
            } else {
                state.update { it.copy(message = StatusMessage.UserDeletedError) }
            }
        }
    }

    /**
     * Delete user locally by deleting all tables and resetting preferences
     *
     */
    private suspend fun deleteUserLocally() {
        pileRepository.deletePileData()
        userRepository.setSignedInUser("")
        userRepository.deleteUserTable()
        state.update {
            it.copy(
                loading = false,
                userDeleted = true,
                message = StatusMessage.UserDeleted
            )
        }
    }

    /**
     * Reset user message
     *
     */
    fun resetMessage() {
        state.update { it.copy(message = null) }
    }

    /**
     * Update user
     *
     * @param result new user data for the update
     */
    fun updateUser(result: EditUserResult) {
        viewModelScope.launch {
            val existingUser = userRepository.getSignedInUserEntity()
            // only update user if exists and old password is correct
            if (existingUser != null) {
                updateUserLocally(existingUser, result)
            } else {
                state.update {
                    it.copy(
                        loading = false,
                        message = StatusMessage.UserUpdateError
                    )
                }
            }
        }
    }

    /**
     * Update user locally by overwriting existing user properties
     *
     * @param existingUser existing user entity
     * @param result new user data for the update
     */
    private suspend fun updateUserLocally(existingUser: User, result: EditUserResult) {
        userRepository.insertUser(
            existingUser.copy(
                name = result.name.trim(),
            )
        )
        state.update {
            it.copy(
                loading = false,
                message = StatusMessage.UserUpdateSuccessful
            )
        }
    }

    /**
     * Export database
     *
     */
    fun exportDatabase() {
        state.update { it.copy(loading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            databaseExporter.exportPileDatabase().collect { exportResult ->
                when (exportResult) {
                    is ExportResult.Error -> state.update {
                        it.copy(message = StatusMessage.BackupError(exportResult.message))
                    }

                    is ExportResult.Success -> state.update {
                        val path = if (exportResult.showAction) exportResult.path else null
                        it.copy(message = StatusMessage.BackupSuccess(path))
                    }
                }
                state.update { it.copy(loading = false) }
            }
        }
    }

    /**
     * Share file
     *
     * @param filePath path to the file to share
     */
    fun shareFile(filePath: String) = databaseExporter.shareFile(filePath)

    /**
     * Import database
     *
     * @param file file to import
     */
    fun importDatabase(file: PlatformFile) {
        if (!file.name.endsWith(".db")) {
            state.update { it.copy(message = StatusMessage.ImportError("Invalid file type")) }
            return
        }
        state.update { it.copy(loading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            databaseExporter.importPileDatabase(file).collect { importResult ->
                when (importResult) {
                    is ImportResult.Error -> state.update {
                        it.copy(message = StatusMessage.ImportError(importResult.message))
                    }

                    is ImportResult.Success -> state.update {
                        it.copy(message = StatusMessage.ImportSuccess)
                    }
                }
                state.update { it.copy(loading = false) }
            }
        }
    }

    fun setShowSyncScreen(enabled: Boolean) {
        viewModelScope.launch {
            userRepository.insertUser(state.value.user.copy(showSyncScreen = enabled))
        }
    }
}

data class SettingsViewState(
    val user: User = User(),
    val loading: Boolean = false,
    val message: StatusMessage? = null,
    val userDeleted: Boolean = false,
    val skipSplashScreen: Boolean = false,
)

sealed interface StatusMessage {
    data object UserUpdateSuccessful : StatusMessage
    data object UserUpdateError : StatusMessage
    data object UserDeleted : StatusMessage
    data object UserDeletedError : StatusMessage
    data class BackupSuccess(val path: String?) : StatusMessage
    data class BackupError(val message: String) : StatusMessage
    data object ImportSuccess : StatusMessage
    data class ImportError(val message: String) : StatusMessage
}
