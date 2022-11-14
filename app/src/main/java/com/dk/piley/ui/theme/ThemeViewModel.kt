package com.dk.piley.ui.theme

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
            val userFlow = userRepository.getSignedInUserNotNull()
            combine(userFlow) { (user) ->
                ThemeViewState(user.nightMode, user.dynamicColorOn)
            }.collect { _state.value = it }
        }
    }
}

data class ThemeViewState(
    val nightModeEnabled: NightMode = NightMode.SYSTEM,
    val dynamicColorEnabled: Boolean = true
)