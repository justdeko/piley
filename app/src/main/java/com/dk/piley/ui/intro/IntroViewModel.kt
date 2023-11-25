package com.dk.piley.ui.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    /**
     * Set username
     *
     * @param name the user name to insert
     */
    fun setUsername(name: String) {
        viewModelScope.launch {
            userRepository.getSignedInUserEntity()?.let {
                userRepository.insertUser(it.copy(name = name))
            }
        }
    }
}