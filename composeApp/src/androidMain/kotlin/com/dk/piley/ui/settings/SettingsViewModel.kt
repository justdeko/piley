package com.dk.piley.ui.settings

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.dk.piley.R
import com.dk.piley.backup.BackupManager
import com.dk.piley.common.StatefulAndroidViewModel
import com.dk.piley.model.common.Resource
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.user.NightMode
import com.dk.piley.model.user.PileMode
import com.dk.piley.model.user.User
import com.dk.piley.model.user.UserRepository
import com.dk.piley.ui.reminder.DelayRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Settings view model
 *
 * @property application generic application context
 * @property userRepository user repository instance
 * @property pileRepository pile repository instance
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository,
    private val pileRepository: PileRepository,
    private val backupManager: BackupManager
) : StatefulAndroidViewModel<SettingsViewState>(application, SettingsViewState()) {

    init {
        viewModelScope.launch {
            val userFlow = userRepository.getSignedInUserNotNullFlow()
            val baseUrlFlow = userRepository.getBaseUrlFlow()
            collectState(
                userFlow.combine(baseUrlFlow) { user, baseUrl ->
                    state.value.copy(user = user, baseUrlValue = baseUrl)
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
     * Set base url to make requests with
     *
     * @param url the url string
     */
    fun setBaseUrl(url: String) {
        viewModelScope.launch {
            userRepository.setBaseUrl(url)
        }
    }

    /**
     * Update backup frequency setting
     *
     * @param frequency new frequency value
     */
    fun updateBackupFrequency(frequency: Int) {
        viewModelScope.launch {
            userRepository.insertUser(state.value.user.copy(defaultBackupFrequency = frequency))
        }
    }

    /**
     * Delete user
     *
     * @param password user password
     */
    fun deleteUser(password: String) {
        viewModelScope.launch {
            val existingUser = userRepository.getSignedInUserEntity()
            // only delete user if it still exists and matches stored password
            if (existingUser != null && existingUser.password == password) {
                // if user is in offline mode, no api call necessary
                if (existingUser.isOffline) {
                    deleteUserLocally()
                } else {
                    userRepository.deleteUserFlow(existingUser.email, password)
                        .collect { resource ->
                            when (resource) {
                                is Resource.Loading -> state.update { it.copy(loading = true) }
                                is Resource.Failure -> {
                                    // show error message when remote delete unsuccessful
                                    state.update {
                                        it.copy(
                                            loading = false,
                                            message = resource.exception.message
                                        )
                                    }
                                }

                                is Resource.Success -> {
                                    // delete user and all piles, set signed in user to empty
                                    deleteUserLocally()
                                }
                            }
                        }
                }
            } else {
                state.update { it.copy(message = application.getString(R.string.delete_user_error_wrong_password)) }
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
        userRepository.setSignedOut(true)
        userRepository.deleteUserTable()
        state.update {
            it.copy(
                loading = false,
                userDeleted = true,
                message = application.getString(R.string.delete_user_success_info)
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
            if (existingUser != null && result.oldPassword == existingUser.password) {
                // if user is in offline mode, only update locally
                if (existingUser.isOffline) {
                    updateUserLocally(existingUser, result)
                } else {
                    userRepository.updateUserFlow(
                        oldUser = existingUser,
                        newPassword = result.newPassword,
                        newName = result.name.trim()
                    ).collect { resource ->
                        when (resource) {
                            is Resource.Loading -> state.update { it.copy(loading = true) }
                            // if remote update failed, show error message
                            is Resource.Failure -> {
                                state.update {
                                    it.copy(
                                        loading = false,
                                        message = resource.exception.message
                                    )
                                }
                            }
                            // if remote update successful, also update locally
                            is Resource.Success -> {
                                updateUserLocally(existingUser, result)
                            }
                        }
                    }
                }
            } else {
                state.update {
                    it.copy(
                        loading = false,
                        message = application.getString(R.string.update_user_error_wrong_password)
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
                password = result.newPassword.ifBlank { result.oldPassword }
            )
        )
        state.update {
            it.copy(
                loading = false,
                message = application.getString(R.string.user_update_success_info)
            )
        }
    }

    /**
     * Update pull backup frequency setting
     *
     * @param days after how many days the backup should be queried again
     */
    fun updatePullBackupPeriod(days: Int) {
        viewModelScope.launch {
            userRepository.insertUser(state.value.user.copy(loadBackupAfterDays = days))
        }
    }

    /**
     * Make user online by connecting to backup server
     *
     * @param result
     */
    fun makeUserOnline(result: MakeUserOnlineResult) {
        viewModelScope.launch {
            // get current user and update params
            val currentUser = userRepository.getSignedInUserEntity() ?: return@launch
            val newUser = currentUser.copy(
                name = result.name,
                email = result.email,
                password = result.password,
                isOffline = false
            )
            // cancel operation
            // set the base url before making request
            userRepository.setBaseUrl(result.serverUrl)
            // create user object
            userRepository.registerUserFlow(newUser).collectLatest {
                when (it) {
                    is Resource.Loading -> setLoading(true)
                    // if registration successful, continue to local operations
                    is Resource.Success -> onRemoteRegisterSuccess(newUser)
                    // if registration failed, show error message
                    is Resource.Failure -> {
                        state.update { viewState ->
                            viewState.copy(
                                loading = false,
                                message = application.getString(R.string.error_make_user_online)
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Perform final local actions on remote register success
     *
     * @param user the user entity to update
     */
    private suspend fun onRemoteRegisterSuccess(user: User) {
        // update user
        userRepository.insertUser(user)
        // set relevant preferences
        userRepository.setSignedInUser(user.email)
        userRepository.setSignedOut(false)
        // perform a backup
        val successful = backupManager.doBackup()
        val message = if (!successful) {
            application.getString(R.string.make_user_online_backup_failed)
        } else application.getString(R.string.make_user_online_success)
        state.update { it.copy(message = message, loading = false) }
        setLoading(false)
    }

    /**
     * Set whether something is loading
     *
     * @param loading loading is true
     */
    private fun setLoading(loading: Boolean) = state.update { it.copy(loading = loading) }
}

data class SettingsViewState(
    val user: User = User(),
    val loading: Boolean = false,
    val message: String? = null,
    val userDeleted: Boolean = false,
    val baseUrlValue: String = "",
)