package com.dk.piley

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.user.NightMode
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ThemeViewState())

    val state: StateFlow<ThemeViewState>
        get() = _state

    init {
        viewModelScope.launch {
            // TODO: remove hardcoded
            val userFlow = userRepository.getUserById(1)
            combine(userFlow) { (user) ->
                ThemeViewState(user.nightMode)
            }.collect { _state.value = it }
        }
    }
}

data class ThemeViewState(
    val nightModeEnabled: NightMode = NightMode.SYSTEM
)