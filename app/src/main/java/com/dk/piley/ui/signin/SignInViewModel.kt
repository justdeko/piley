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
import com.dk.piley.util.utcZoneId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import timber.log.Timber
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
                    is Resource.Loading -> Timber.i("attempting to load remote backup")
                    is Resource.Success -> {
                        if (it.data != null) {
                            // set user backup date
                            userRepository.insertUser(
                                user.copy(
                                    lastBackup = LocalDateTime.ofInstant(
                                        it.data,
                                        utcZoneId
                                    )
                                )
                            )
                            Timber.i("Backup loaded, going into main view")
                            setLoading(false)
                            setSignInState(SignInState.SIGNED_IN)
                        } else {
                            createAndSetUserPile()
                        }
                    }

                    is Resource.Failure -> createAndSetUserPile()
                }
            }
        } else {
            createAndSetUserPile()
        }
    }

    private suspend fun createAndSetUserPile() {
        // create default pile
        val pile = Pile(
            name = getApplication<Application>().getString(R.string.daily_pile_name),
        )
        val pileId = pileRepository.insertPile(pile)
        // update assigned pile as selected and set signed in state
        userRepository.getSignedInUserEntity()?.let { signedInUser ->
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
            when (state.value.signInState) {
                SignInState.REGISTER -> {
                    // Register
                    attemptRegister()
                }

                SignInState.REGISTER_OFFLINE -> {
                    // register but only locally
                    doOfflineRegister()
                }

                else -> {
                    // Attempt sign in
                    attemptRemoteSignIn(userData.email, userData.password)
                }
            }
        }
    }

    private fun doOfflineRegister() {
        setLoading(true)
        val userData = state.value
        val user = User(
            name = userData.username,
            email = userData.email,
            password = userData.password,
            isOffline = true
        )
        viewModelScope.launch {
            userRepository.insertUser(user)
            userRepository.setSignedInUser(user.email)
            createAndSetUserPile()
            setLoading(false)
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
                        password = password
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

