package com.dk.piley.ui.theme

import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.user.NightMode
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Theme view model
 *
 * @property userRepository user repository instance
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : StatefulViewModel<ThemeViewState>(ThemeViewState()) {

    // observe user and update theme accordingly if user settings change
    init {
        viewModelScope.launch {
            val userFlow = userRepository.getSignedInUserNotNullFlow()
            collectState(
                combine(userFlow) { (user) ->
                    ThemeViewState(user.nightMode, user.dynamicColorOn)
                }
            )
        }
    }
}

data class ThemeViewState(
    val nightModeEnabled: NightMode = NightMode.SYSTEM,
    val dynamicColorEnabled: Boolean = true
)