package com.dk.piley.ui.splash

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.dk.piley.R
import com.dk.piley.backup.BackupManager
import com.dk.piley.common.StatefulAndroidViewModel
import com.dk.piley.model.common.Resource
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.user.UserRepository
import com.dk.piley.ui.signin.firstTimeUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Splash view model
 *
 * @property application generic application object
 * @property userRepository user repository instance
 * @property pileRepository pile repository instance
 * @property backupManager user backup manager instance
 * @constructor Create empty Splash view model
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository,
    private val pileRepository: PileRepository,
    private val taskRepository: TaskRepository,
    private val backupManager: BackupManager
) : StatefulAndroidViewModel<SplashViewState>(application, SplashViewState()) {

    init {
        viewModelScope.launch {
            val userEmail = userRepository.getSignedInUserEmail()
            val signedOut = userRepository.getSignedOut()
            if (userEmail.isNotBlank()) {
                // if the tutorial has not been shown but the user is not new, set it to shown
                val tutorialShown = userRepository.getTutorialShown()
                if (!tutorialShown) {
                    userRepository.setTutorialShown(true)
                }
                collectState(
                    combine(loadingBackupFlow()) { (loadingBackup) ->
                        // while loading backup, set state to loading
                        if (loadingBackup) {
                            Timber.d("Loading backup..")
                            SplashViewState(InitState.LOADING_BACKUP)
                        } else {
                            Timber.d("Backup loading attempt finished...")
                            // restart all alarms just in case
                            taskRepository.restartAlarms()
                            SplashViewState(InitState.BACKUP_LOADED_SIGNED_IN)
                        }
                    }
                )
            } else {
                if (signedOut) {
                    state.value = SplashViewState(InitState.NOT_SIGNED_IN)
                } else {
                    doFirstTimeRegister()
                }
            }
        }
    }

    /**
     * Loading backup flow that syncs the backup for the user and emits whether it is still loading
     *
     * @return true if the backup is still being loaded, false if the backup was loaded successfully or unsuccessfully
     */
    private suspend fun loadingBackupFlow(): Flow<Boolean> = flow {
        backupManager.syncBackupToLocalForUserFlow().collect {
            when (it) {
                is Resource.Loading -> {
                    emit(true)
                    Timber.d("Loading backup")
                }

                is Resource.Success -> {
                    Timber.d("Remote backup request completed, replaced local db: ${it.data != null}")
                    emit(false)
                }

                is Resource.Failure -> {
                    Timber.e(it.exception)
                    emit(false)
                }
            }
        }
    }

    /**
     * Perform a first-time registration
     *
     */
    private fun doFirstTimeRegister() {
        viewModelScope.launch {
            userRepository.insertUser(firstTimeUser)
            userRepository.setSignedInUser(firstTimeUser.email)
            createAndSetUserPile()
        }
    }

    /**
     * Create and set user pile for first-time user
     *
     */
    private suspend fun createAndSetUserPile() {
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
        // signal loading process has finished to user and set state to first time
        // or navigate straight to main screen if tutorial already shown
        val tutorialShown = userRepository.getTutorialShown()
        val finalState =
            if (tutorialShown) InitState.BACKUP_LOADED_SIGNED_IN else InitState.FIRST_TIME
        state.update { SplashViewState(finalState) }
    }
}


data class SplashViewState(
    val initState: InitState = InitState.INIT
)

enum class InitState {
    INIT,
    NOT_SIGNED_IN,
    LOADING_BACKUP,
    BACKUP_LOADED_SIGNED_IN,
    FIRST_TIME
}
