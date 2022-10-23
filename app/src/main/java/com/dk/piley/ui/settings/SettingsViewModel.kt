package com.dk.piley.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.user.NightMode
import com.dk.piley.model.user.User
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsViewState())

    val state: StateFlow<SettingsViewState>
        get() = _state

    init {
        viewModelScope.launch {
            // TODO: remove hardcoded
            val userFlow = userRepository.getUserById(1)
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
}

data class SettingsViewState(
    val user: User = User(),
)