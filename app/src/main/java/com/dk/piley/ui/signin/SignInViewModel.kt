package com.dk.piley.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.user.User
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SignInViewState())

    val state: StateFlow<SignInViewState>
        get() = _state

    fun createUser() {
        val userData = state.value
        val user = User(
            name = userData.username,
            email = userData.email,
            password = userData.password
        )
        viewModelScope.launch {
            userRepository.insertUser(user)
        }
    }

    fun attemptSignIn() {
        val userData = state.value
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(userData.email).first()
            val canSignIn = if (user != null) {
                user.password != userData.password
            } else false
            _state.update { it.copy(canSignIn = canSignIn) }
        }
    }

    fun setEmail(input: String) = _state.update { it.copy(email = input) }
    fun setName(input: String) = _state.update { it.copy(username = input) }
    fun setPassword(input: String) = _state.update { it.copy(password = input) }
}

data class SignInViewState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val canSignIn: Boolean = false,
)
