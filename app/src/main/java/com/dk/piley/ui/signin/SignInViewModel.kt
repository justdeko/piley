package com.dk.piley.ui.signin

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.dk.piley.R
import com.dk.piley.backup.BackupManager
import com.dk.piley.common.StatefulAndroidViewModel
import com.dk.piley.model.common.Resource
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.user.User
import com.dk.piley.model.user.UserRepository
import com.dk.piley.util.INITIAL_MESSAGE
import com.dk.piley.util.usernameCharacterLimit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


/**
 * Sign in view model
 *
 * @property application generic application context
 * @property userRepository user repository instance
 * @property pileRepository pile repository instance
 * @property backupManager user backup manager instance
 */
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository,
    private val pileRepository: PileRepository,
    private val backupManager: BackupManager,
) : StatefulAndroidViewModel<SignInViewState>(application, SignInViewState()) {

    init {
        viewModelScope.launch {
            collectState(combine(userRepository.getBaseUrl()) { (baseUrl) ->
                state.value.copy(baseUrlValue = baseUrl)
            })
        }
    }

    /**
     * Attempt register by performing remote registration call
     *
     */
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
                    // if registration successful, continue to local operations
                    is Resource.Success -> onRemoteRegisterOrSignInSuccess(user)
                    // if registration failed, show error message
                    is Resource.Failure -> {
                        setLoading(false)
                        setMessage(application.getString(R.string.user_register_error_message))
                    }
                }
            }
        }
    }

    /**
     * Local actions after remote register or sign in success
     *
     * @param user user instance created from sign in/registration data
     * @param isSignIn whether the previous action was a sign in
     */
    private suspend fun onRemoteRegisterOrSignInSuccess(user: User, isSignIn: Boolean = false) {
        // create user and set as signed in
        userRepository.insertUser(user)
        userRepository.setSignedInUser(user.email)
        // if it is a sign in, perform remote backup fetch
        if (isSignIn) {
            backupManager.syncBackupToLocalForUserFlow().collect {
                when (it) {
                    is Resource.Loading -> Timber.i("Attempting to load remote backup")
                    // if backup successful, update ui and proceed to main screen
                    is Resource.Success -> {
                        if (it.data != null) {
                            // set user backup date
                            userRepository.insertUser(
                                user.copy(
                                    lastBackup = it.data
                                )
                            )
                            Timber.i("Backup loaded, going into main view")
                            updateUIOnSignInSuccess()
                        } else {
                            // if no backup returned, create new pile and set as default
                            createAndSetUserPile()
                        }
                    }

                    // if loading backup unsuccessful, create new pile and set as default
                    is Resource.Failure -> createAndSetUserPile()
                }
            }
        } else {
            // set user as first time since it is a register process
            state.update { it.copy(firstTime = true) }
            createAndSetUserPile(false)
        }
    }

    /**
     * Create new pile and set as user default
     *
     * @param isSignIn
     */
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

    /**
     * Update ui on sign in or register success by by stopping loading and showing success message
     * if it is a sign in, also restart the application to properly load the database
     *
     * @param isSignIn whether the user performed a sign in
     */
    private fun updateUIOnSignInSuccess(isSignIn: Boolean = true) {
        setLoading(false)
        setSignInState(SignInState.SIGNED_IN)
        // TODO remove intermediate solution when runtime db fixed
        if (isSignIn) {
            setMessage(application.getString(R.string.sign_in_success_message))
            restartApplication(
                getApplication(),
                application.getString(R.string.sign_in_success_message)
            )
        }
    }

    /**
     * Restart application programmatically
     *
     * @param context generic context needed to get launch intent for app
     * @param initialMessage initial message that is shown on app restart
     */
    private fun restartApplication(context: Context, initialMessage: String?) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent!!.component
        val mainIntent =
            Intent.makeRestartActivityTask(componentName).putExtra(INITIAL_MESSAGE, initialMessage)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

    /**
     * Attempt sign in or register
     *
     */
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

    /**
     * Perform offline user registration by creating user entity,
     * setting as signed in user and creating default pile
     *
     */
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
            state.update { it.copy(firstTime = true) }
            userRepository.insertUser(user)
            userRepository.setSignedInUser(user.email)
            createAndSetUserPile(false)
            setLoading(false)
        }
    }

    /**
     * Set base url to make requests with
     *
     * @param url the url string
     */
    fun setBaseUrl(url: String) {
        viewModelScope.launch {
            state.update { it.copy(baseUrlValue = url) }
            userRepository.setBaseUrl(url)
        }
    }

    /**
     * Attempt remote sign in to backend
     *
     * @param email user email
     * @param password user password
     */
    private suspend fun attemptRemoteSignIn(email: String, password: String) {
        userRepository.getUserFromRemoteFlow(email, password).collectLatest {
            when (it) {
                is Resource.Loading -> setLoading(true)
                // if successful, perform local actions for registration
                is Resource.Success -> onRemoteRegisterOrSignInSuccess(
                    User(
                        name = it.data.name,
                        email = it.data.email,
                        password = password
                    ),
                    isSignIn = true
                )
                // if unsuccessful, show error message
                is Resource.Failure -> {
                    setLoading(false)
                    setMessage(application.getString(R.string.sign_in_error_message))
                }
            }
        }
    }

    /**
     * Set current sign in state
     *
     * @param signInState sign in state
     */
    fun setSignInState(signInState: SignInState) =
        state.update { it.copy(signInState = signInState) }

    /**
     * Set whether something is loading
     *
     * @param loading loading is true
     */
    private fun setLoading(loading: Boolean) = state.update { it.copy(loading = loading) }

    /**
     * Set email input value
     *
     * @param input email string value
     */
    fun setEmail(input: String) = state.update { it.copy(email = input) }

    /**
     * Set username input value, only if character limit not exceeded
     *
     * @param input user name string value
     */
    fun setUsername(input: String) {
        if (input.length > usernameCharacterLimit) return
        state.update { it.copy(username = input) }
    }

    /**
     * Set password input value
     *
     * @param input password string value
     */
    fun setPassword(input: String) = state.update { it.copy(password = input) }

    /**
     * Set user message
     *
     * @param message message value
     */
    fun setMessage(message: String?) = state.update { it.copy(message = message) }
}

data class SignInViewState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val signInState: SignInState = SignInState.SIGNED_OUT,
    val firstTime: Boolean = false,
    val loading: Boolean = false,
    val message: String? = null,
    val baseUrlValue: String = ""
)

