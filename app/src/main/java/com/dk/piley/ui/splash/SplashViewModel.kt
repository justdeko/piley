package com.dk.piley.ui.splash

import androidx.lifecycle.viewModelScope
import com.dk.piley.backup.BackupManager
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.common.Resource
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Splash view model
 *
 * @property userRepository user repository instance
 * @property backupManager user backup manager instance
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val backupManager: BackupManager
) : StatefulViewModel<SplashViewState>(SplashViewState()) {

    init {
        viewModelScope.launch {
            val userEmail = userRepository.getSignedInUserEmail()
            if (userEmail.isNotBlank()) {
                collectState(
                    combine(loadingBackupFlow()) { (loadingBackup) ->
                        // while loading backup, set state to loading
                        if (loadingBackup) {
                            Timber.d("loading backup..")
                            SplashViewState(InitState.LOADING_BACKUP)
                        } else {
                            Timber.d("backup loading attempt finished...")
                            SplashViewState(InitState.BACKUP_LOADED_SIGNED_IN)
                        }
                    }
                )
            } else {
                state.value = SplashViewState(InitState.NOT_SIGNED_IN)
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
                    Timber.i("Loading backup")
                }

                is Resource.Success -> {
                    Timber.i("Remote backup request completed, replaced local db: ${it.data != null}")
                    emit(false)
                }

                is Resource.Failure -> {
                    Timber.e(it.exception)
                    emit(false)
                }
            }
        }
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
}
