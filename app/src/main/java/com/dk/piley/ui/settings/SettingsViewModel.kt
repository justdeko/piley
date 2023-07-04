package com.dk.piley.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.common.Resource
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.user.NightMode
import com.dk.piley.model.user.PileMode
import com.dk.piley.model.user.User
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val pileRepository: PileRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsViewState())

    val state: StateFlow<SettingsViewState>
        get() = _state

    init {
        viewModelScope.launch {
            val userFlow = userRepository.getSignedInUserNotNullFlow()
            combine(userFlow) { (user) ->
                SettingsViewState(user)
            }.collect { _state.value = it }
        }
    }


    fun updateNightMode(nightMode: NightMode) {
        viewModelScope.launch {
            userRepository.insertUser(state.value.user.copy(nightMode = nightMode))
        }
    }

    fun updateDynamicColorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userRepository.insertUser(state.value.user.copy(dynamicColorOn = enabled))
        }
    }

    fun updateDefaultPileMode(pileMode: PileMode) {
        viewModelScope.launch {
            userRepository.insertUser(state.value.user.copy(pileMode = pileMode))
        }
    }

    fun onResetPileModes() {
        viewModelScope.launch {
            pileRepository.resetPileModes()
        }
    }

    fun updateHideKeyboardEnabled(hide: Boolean) {
        viewModelScope.launch {
            userRepository.insertUser(state.value.user.copy(autoHideKeyboard = hide))
        }
    }

    fun updateReminderDelay(delay: Int) {
        viewModelScope.launch {
            userRepository.insertUser(state.value.user.copy(defaultReminderDelay = delay))
        }
    }

    fun updateBackupFrequency(frequency: Int) {
        viewModelScope.launch {
            userRepository.insertUser(state.value.user.copy(defaultBackupFrequency = frequency))
        }
    }

    fun deleteUser(password: String) {
        viewModelScope.launch {
            val existingUser = userRepository.getSignedInUserEntity()
            if (existingUser != null && existingUser.password == password) {
                if (existingUser.isOffline) {
                    deleteUserLocally()
                } else {
                    userRepository.deleteUserFlow(existingUser.email, password)
                        .collect { resource ->
                            when (resource) {
                                is Resource.Loading -> _state.update { it.copy(loading = true) }
                                is Resource.Failure -> {
                                    _state.update {
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
                _state.update { it.copy(message = "Error deleting user: Your password is incorrect") }
            }
        }
    }

    private suspend fun deleteUserLocally() {
        userRepository.setSignedInUser("")
        userRepository.deleteUserTable()
        pileRepository.deletePileData()
        _state.update {
            it.copy(
                loading = false,
                userDeleted = true,
                message = "User deleted, signing out"
            )
        }
    }

    fun resetToastMessage() {
        _state.update { it.copy(message = null) }
    }

    fun updateUser(result: EditUserResult) {
        viewModelScope.launch {
            val existingUser = userRepository.getSignedInUserEntity()
            if (existingUser != null && result.oldPassword == existingUser.password) {
                if (existingUser.isOffline) {
                    updateUserLocally(existingUser, result)
                } else {
                    userRepository.updateUserFlow(
                        oldUser = existingUser,
                        newPassword = result.newPassword,
                        newName = result.name
                    ).collect { resource ->
                        when (resource) {
                            is Resource.Loading -> _state.update { it.copy(loading = true) }
                            is Resource.Failure -> {
                                _state.update {
                                    it.copy(
                                        loading = false,
                                        message = resource.exception.message
                                    )
                                }
                            }

                            is Resource.Success -> {
                                updateUserLocally(existingUser, result)
                            }
                        }
                    }
                }
            } else {
                _state.update {
                    it.copy(
                        loading = false,
                        message = "Error updating user: Your password is incorrect"
                    )
                }
            }
        }
    }

    private suspend fun updateUserLocally(existingUser: User, result: EditUserResult) {
        userRepository.insertUser(
            existingUser.copy(
                name = result.name,
                password = result.newPassword.ifBlank { result.oldPassword }
            )
        )
        _state.update {
            it.copy(
                loading = false,
                message = "User updated successfully!"
            )
        }
    }
}

data class SettingsViewState(
    val user: User = User(),
    val loading: Boolean = false,
    val message: String? = null,
    val userDeleted: Boolean = false
)