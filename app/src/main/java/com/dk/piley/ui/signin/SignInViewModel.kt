package com.dk.piley.ui.signin

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.R
import com.dk.piley.backup.BackupManager
import com.dk.piley.model.common.Resource
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.user.User
import com.dk.piley.model.user.UserRepository
import com.dk.piley.util.usernameCharacterLimit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(
    private val application: Application,
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
            name = userData.username.trim(),
            email = userData.email,
            password = userData.password
        )
        viewModelScope.launch {
            userRepository.registerUserFlow(user).collectLatest {
                when (it) {
                    is Resource.Loading -> setLoading(true)
                    is Resource.Success -> onRemoteRegisterOrSignInSuccess(user)
                    is Resource.Failure -> {
                        setLoading(false)
                        setMessage(application.getString(R.string.user_register_error_message))
                    }
                }
            }
        }
    }

    private suspend fun onRemoteRegisterOrSignInSuccess(user: User, isSignIn: Boolean = false) {
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
                                        ZoneId.systemDefault()
                                    )
                                )
                            )
                            Timber.i("Backup loaded, going into main view")
                            updateUIOnSignInSuccess()
                        } else {
                            createAndSetUserPile()
                        }
                    }

                    is Resource.Failure -> createAndSetUserPile()
                }
            }
        } else {
            // set user as first time since it is a register process
            _state.update { it.copy(firstTime = true) }
            createAndSetUserPile(false)
        }
    }

    private suspend fun createAndSetUserPile(isSignIn: Boolean = true) {
        // create default pile
        val pile = Pile(
            name = application.getString(R.string.daily_pile_name),
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
        updateUIOnSignInSuccess(isSignIn)
    }

    private fun updateUIOnSignInSuccess(isSignIn: Boolean = true) {
        setLoading(false)
        setMessage(application.getString(R.string.sign_in_success_message))
        setSignInState(SignInState.SIGNED_IN)
        // TODO remove intermediate solution when runtime db fixed
        if (isSignIn) {
            restartApplication(getApplication())
        }
    }

    /**
     * Programmatically restart application
     */
    private fun restartApplication(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
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
            name = userData.username.trim(),
            email = userData.email,
            password = userData.password,
            isOffline = true
        )
        viewModelScope.launch {
            // set user as first time since it is a register process
            _state.update { it.copy(firstTime = true) }
            userRepository.insertUser(user)
            userRepository.setSignedInUser(user.email)
            createAndSetUserPile(false)
            setLoading(false)
        }
    }

    private suspend fun attemptRemoteSignIn(email: String, password: String) {
        userRepository.getUserFromRemoteFlow(email, password).collectLatest {
            when (it) {
                is Resource.Loading -> setLoading(true)
                is Resource.Success -> onRemoteRegisterOrSignInSuccess(
                    User(
                        name = it.data.name,
                        email = it.data.email,
                        password = password
                    ),
                    isSignIn = true
                )

                is Resource.Failure -> {
                    setLoading(false)
                    setMessage(application.getString(R.string.sign_in_error_message))
                }
            }
        }
    }

    fun setSignInState(signInState: SignInState) =
        _state.update { it.copy(signInState = signInState) }

    private fun setLoading(loading: Boolean) = _state.update { it.copy(loading = loading) }
    fun setEmail(input: String) = _state.update { it.copy(email = input) }
    fun setUsername(input: String) {
        if (input.length > usernameCharacterLimit) return
        _state.update { it.copy(username = input) }
    }

    fun setPassword(input: String) = _state.update { it.copy(password = input) }
    fun setMessage(message: String?) = _state.update { it.copy(message = message) }
}

data class SignInViewState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val signInState: SignInState = SignInState.SIGNED_OUT,
    val firstTime: Boolean = false,
    val loading: Boolean = false,
    val message: String? = null,
)

