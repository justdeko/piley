package com.dk.piley.ui.settings

import androidx.lifecycle.ViewModel
import com.dk.piley.model.user.User
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsViewState())

    val state: StateFlow<SettingsViewState>
        get() = _state

}

data class SettingsViewState(
    val user: User = User(name = "")
)