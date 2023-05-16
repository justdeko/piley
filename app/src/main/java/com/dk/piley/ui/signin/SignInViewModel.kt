package com.dk.piley.ui.signin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.R
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.remote.Resource
import com.dk.piley.model.user.User
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository,
    private val pileRepository: PileRepository,
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(SignInViewState())

    val state: StateFlow<SignInViewState>
        get() = _state

    private fun attemptRegister() {
        val userData = state.value
        val user = User(
            name = userData.username,
            email = userData.email,
            password = userData.password
        )
        viewModelScope.launch {
            userRepository.registerUserFlow(user).collectLatest {
                when (it) {
                    is Resource.Loading -> setLoading(true)
                    is Resource.Success -> onRegisterSuccess(user)
                    is Resource.Failure -> {
                        setLoading(false)
                        setSignInState(SignInState.REGISTER_ERROR)
                    }
                }
            }
        }
    }

    private suspend fun onRegisterSuccess(user: User) {
        // create user and set as signed in
        val userId = userRepository.insertUser(user)
        userRepository.setSignedInUser(userId)
        // create default pile and assign to user
        val pile = Pile(
            name = getApplication<Application>().getString(R.string.daily_pile_name),
            userId = userId
        )
        val pileId = pileRepository.insertPile(pile)
        // signal loading process has finished to user
        setLoading(false)
        // update assigned pile as selected and set signed in state
        userRepository.getSignedInUserNotNull().first().let { signedInUser ->
            userRepository.insertUser(
                signedInUser.copy(
                    selectedPileId = pileId,
                    defaultPileId = pileId
                )
            )
            setSignInState(SignInState.SIGNED_IN)
        }
    }

    fun attemptSignIn() {
        val userData = state.value
        viewModelScope.launch {
            if (state.value.signInState == SignInState.REGISTER) {
                // Register
                attemptRegister()
            } else {
                // Attempt sign in
                userRepository.getUserByEmail(userData.email).first()?.let { user ->
                    if (user.password == userData.password) {
                        userRepository.setSignedInUser(user.userId)
                        setSignInState(SignInState.SIGNED_IN)
                    } else {
                        setSignInState(SignInState.SIGN_IN_ERROR)
                    }
                } ?: run { attemptRemoteSignIn(userData.email, userData.password) }
            }
        }
    }

    private suspend fun attemptRemoteSignIn(email: String, password: String) {
        userRepository.authenticateUserFlow(email, password).collectLatest {
            when (it) {
                is Resource.Loading -> setLoading(true)
                is Resource.Success -> onSignInSuccess()
                is Resource.Failure -> {
                    setLoading(false)
                    setSignInState(SignInState.SIGN_IN_ERROR)
                }
            }
        }
    }

    private fun onSignInSuccess() {
        TODO("Not yet implemented")
    }

    fun setSignInState(signInState: SignInState) =
        _state.update { it.copy(signInState = signInState) }

    private fun setLoading(loading: Boolean) = _state.update { it.copy(loading = loading) }
    fun setEmail(input: String) = _state.update { it.copy(email = input) }
    fun setUsername(input: String) = _state.update { it.copy(username = input) }
    fun setPassword(input: String) = _state.update { it.copy(password = input) }
}

data class SignInViewState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val signInState: SignInState = SignInState.SIGNED_OUT,
    val loading: Boolean = false
)

