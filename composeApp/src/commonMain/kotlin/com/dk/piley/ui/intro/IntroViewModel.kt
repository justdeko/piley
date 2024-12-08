package com.dk.piley.ui.intro

import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.navigation.Shortcut
import com.dk.piley.model.navigation.ShortcutEventRepository
import com.dk.piley.model.user.UserRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IntroViewModel(
    private val userRepository: UserRepository,
    shortcutEventRepository: ShortcutEventRepository,
) : StatefulViewModel<IntroViewState>(IntroViewState()) {

    init {
        viewModelScope.launch {
            collectState(shortcutEventRepository.keyEventFlow
                .map {
                    state.value.copy(
                        keyEvent = when (it) {
                            Shortcut.NavigateLeft -> KeyEventAction.LEFT
                            Shortcut.NavigateRight -> KeyEventAction.RIGHT
                            else -> null
                        }
                    )
                }
            )
        }
    }

    /**
     * Set username
     *
     * @param name the user name to insert
     */
    fun setUsername(name: String) {
        viewModelScope.launch {
            // set tutorial shown to true to prevent it from new displays
            userRepository.setTutorialShown()
            // only set the name if the passed name is not blank, otherwise just use sample username
            if (name.isNotBlank()) {
                userRepository.getSignedInUserEntity()?.let {
                    userRepository.insertUser(it.copy(name = name))
                }
            }
        }
    }

    fun onConsumeKeyEvent() {
        state.update { it.copy(keyEvent = null) }
    }
}

data class IntroViewState(
    val keyEvent: KeyEventAction? = null
)

enum class KeyEventAction {
    LEFT,
    RIGHT
}