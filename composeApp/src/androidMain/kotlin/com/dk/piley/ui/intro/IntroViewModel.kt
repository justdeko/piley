package com.dk.piley.ui.intro

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var registerName: String

    init {
        // query the name of the registered user, if already specified
        registerName = savedStateHandle.get<String>("name") ?: ""
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
}