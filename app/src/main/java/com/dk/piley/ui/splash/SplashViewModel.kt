package com.dk.piley.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SplashViewState())

    val state: StateFlow<SplashViewState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(userRepository.getSignedInUser()) { (user) ->
                SplashViewState(signedIn = user != null)
            }.collect { _state.value = it }
        }
    }

    fun isSignedIn() = state.value.signedIn
}

data class SplashViewState(
    val signedIn: Boolean = false
)
