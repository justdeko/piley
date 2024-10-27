package com.dk.piley.ui.settings

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.dk.piley.R
import com.dk.piley.common.StatefulAndroidViewModel
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.user.NightMode
import com.dk.piley.model.user.PileMode
import com.dk.piley.model.user.User
import com.dk.piley.model.user.UserRepository
import com.dk.piley.reminder.DelayRange
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Settings view model
 *
 * @property application generic application context
 * @property userRepository user repository instance
 * @property pileRepository pile repository instance
 */
class SettingsViewModel(
    private val application: Application,
    private val userRepository: UserRepository,
    private val pileRepository: PileRepository,
) : StatefulAndroidViewModel<SettingsViewState>(application, SettingsViewState()) {

    init {
        viewModelScope.launch {
            val userFlow = userRepository.getSignedInUserNotNullFlow()
            collectState(
                userFlow.map {
                    state.value.copy(user = it)
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
            if (existingUser != null) {
                updateUserLocally(existingUser, result)
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
            )
        )
        state.update {
            it.copy(
                loading = false,
                message = application.getString(R.string.user_update_success_info)
            )
        }
    }
}

data class SettingsViewState(
    val user: User = User(),
    val loading: Boolean = false,
    val message: String? = null,
    val userDeleted: Boolean = false,
)