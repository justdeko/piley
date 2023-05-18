package com.dk.piley.ui.signin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.R
import com.dk.piley.backup.BackupManager
import com.dk.piley.model.common.Resource
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
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
    private val backupManager: BackupManager,
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

    private suspend fun onRegisterSuccess(user: User, isSignIn: Boolean = false) {
        // create user and set as signed in
        userRepository.insertUser(user)
        userRepository.setSignedInUser(user.email)
        if (isSignIn) {
            backupManager.syncBackupToLocalForUserFlow().collect {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data) {
                            setLoading(false)
                            setSignInState(SignInState.SIGNED_IN)
                        } else {
                            createAndSetUserPile(user)
                        }
                    }

                    is Resource.Failure -> createAndSetUserPile(user)
                }
            }
        } else {
            createAndSetUserPile(user)
        }
    }

    private suspend fun createAndSetUserPile(user: User) {
        // create default pile and assign to user
        val pile = Pile(
            name = getApplication<Application>().getString(R.string.daily_pile_name),
            userEmail = user.email
        )
        val pileId = pileRepository.insertPile(pile)
        // update assigned pile as selected and set signed in state
        userRepository.getSignedInUserNotNull().first().let { signedInUser ->
            userRepository.insertUser(
                signedInUser.copy(
                    selectedPileId = pileId,
                    defaultPileId = pileId
                )
            )
        }
        // signal loading process has finished to user and set state to signed in
        setLoading(false)
        setSignInState(SignInState.SIGNED_IN)
    }

    fun attemptSignIn() {
        val userData = state.value
        viewModelScope.launch {
            if (state.value.signInState == SignInState.REGISTER) {
                // Register
                attemptRegister()
            } else {
                // Attempt sign in
                attemptRemoteSignIn(userData.email, userData.password)
            }
        }
    }

    private suspend fun attemptRemoteSignIn(email: String, password: String) {
        userRepository.getUserFromRemoteFlow(email, password).collectLatest {
            when (it) {
                is Resource.Loading -> setLoading(true)
                is Resource.Success -> onRegisterSuccess(
                    User(
                        name = it.data.name,
                        email = it.data.email,
                        password = it.data.password
                    ),
                    isSignIn = true
                )

                is Resource.Failure -> {
                    setLoading(false)
                    setSignInState(SignInState.SIGN_IN_ERROR)
                }
            }
        }
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

