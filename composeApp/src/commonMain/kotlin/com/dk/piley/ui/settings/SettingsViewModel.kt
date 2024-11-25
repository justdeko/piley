package com.dk.piley.ui.settings

import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.user.NightMode
import com.dk.piley.model.user.PileMode
import com.dk.piley.model.user.User
import com.dk.piley.model.user.UserRepository
import com.dk.piley.reminder.DelayRange
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Settings view model
 *
 * @property userRepository user repository instance
 * @property pileRepository pile repository instance
 */
class SettingsViewModel(
    private val userRepository: UserRepository,
    private val pileRepository: PileRepository,
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
                state.update { it.copy(message = StatusMessage.USER_DELETED_ERROR) }
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
                message = StatusMessage.USER_DELETED
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
                        message = StatusMessage.USER_UPDATE_ERROR
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
                message = StatusMessage.USER_UPDATE_SUCCESSFUL
            )
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

enum class StatusMessage {
    USER_UPDATE_SUCCESSFUL,
    USER_UPDATE_ERROR,
    USER_DELETED,
    USER_DELETED_ERROR
}
